package cn.bootx.mybatis.table.modify.mybatis.mysq.service;

import cn.bootx.mybatis.table.modify.mybatis.mysq.annotation.MySqlIndex;
import cn.bootx.mybatis.table.modify.mybatis.mysq.annotation.MySqlIndexes;
import cn.bootx.mybatis.table.modify.mybatis.mysq.constants.MySqlIndexType;
import cn.bootx.mybatis.table.modify.mybatis.mysq.entity.MySqlEntityIndex;
import cn.bootx.mybatis.table.modify.mybatis.mysq.entity.MySqlModifyMap;
import cn.bootx.mybatis.table.modify.mybatis.mysq.entity.MySqlTableIndex;
import cn.bootx.mybatis.table.modify.mybatis.mysq.mapper.MySqlTableModifyMapper;
import cn.bootx.mybatis.table.modify.mybatis.mysq.util.MySqlInfoUtil;
import cn.bootx.mybatis.table.modify.utils.ColumnUtils;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * MySQL索引处理
 * @author xxm
 * @date 2023/4/13
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MySqlIndexInfoService {
    private final MySqlTableModifyMapper mysqlTableModifyMapper;

    /**
     * 获取创建表锁需要的索引 不包括主键索引
     * @param clas 实体类
     * @param baseTableMap 用于存储各种操作MySQL表结构的容器
     */
    public void getCreateIndex(Class<?> clas, MySqlModifyMap baseTableMap){
        // 获取entity的tableName
        String tableName = ColumnUtils.getTableName(clas);
        // 找出实体类所有的索引
        List<MySqlEntityIndex> entityIndexes = getEntityIndexes(clas);
        if (CollUtil.isNotEmpty(entityIndexes)) {
            baseTableMap.getAddIndexes().put(tableName, entityIndexes);
        }
    }

    /**
     * 获取要进行变动的的索引信息 不包括主键索引
     * @param clas 实体类
     * @param baseTableMap 用于存储各种操作MySQL表结构的容器
     */
    public void getModifyIndex(Class<?> clas, MySqlModifyMap baseTableMap){

        // 获取entity的tableName
        String tableName = ColumnUtils.getTableName(clas);

        // 找出实体类所有的索引
        List<MySqlEntityIndex> entityIndexes = getEntityIndexes(clas);

        // 查询当前表中全部索引
        List<MySqlTableIndex> tableIndexes = mysqlTableModifyMapper.findIndexByTableName(tableName);

        // 找出需要删除的索引
        List<String> dropIndexes = getDropIndexes(tableIndexes, entityIndexes);
        if (CollUtil.isNotEmpty(dropIndexes)) {
            baseTableMap.getDropIndexes().put(tableName, dropIndexes);
        }
        // 找出需要新增的索引
        List<MySqlEntityIndex> addIndexes = getAddIndexes(tableIndexes, entityIndexes);
        if (CollUtil.isNotEmpty(addIndexes)) {
            baseTableMap.getAddIndexes().put(tableName, addIndexes);
        }
        // 找出需要修改的索引
        List<MySqlEntityIndex> updateIndexes = getUpdateIndexes(tableIndexes, entityIndexes);
        if (CollUtil.isNotEmpty(updateIndexes)) {
            baseTableMap.getUpdateIndexes().put(tableName, updateIndexes);
        }
    }


    /**
     * 找出需要新建的索引 不包括主键索引
     * @param tableIndexes 当前数据库的索引
     * @param entityIndexes entity中的所有字段
     * @return 需要新建的索引
     */
    private List<MySqlEntityIndex> getAddIndexes(List<MySqlTableIndex> tableIndexes, List<MySqlEntityIndex> entityIndexes) {
        if (CollUtil.isEmpty(entityIndexes)) {
            return new ArrayList<>(0);
        }
        List<String> allIndexNames = tableIndexes.stream()
                .map(MySqlTableIndex::getIndexName)
                .distinct()
                .collect(Collectors.toList());

        return entityIndexes.stream()
                .filter(index->!allIndexNames.contains(index.getName()))
                .collect(Collectors.toList());
    }

    /**
     * 找出需要删除的索引
     * @param tableIndexes 当前数据库的索引
     * @param entityIndexes entity中所有配置的索引
     * @return 需要删除的索引名称
     */
    private List<String> getDropIndexes(List<MySqlTableIndex> tableIndexes, List<MySqlEntityIndex> entityIndexes) {
        if (CollUtil.isEmpty(tableIndexes)) {
            return new ArrayList<>(0);
        }

        // 定义的索引名称集合
        List<String> entityIndexNames = entityIndexes.stream()
                .map(MySqlEntityIndex::getName)
                .map(String::toLowerCase)
                .distinct()
                .collect(Collectors.toList());
        // 将要删除的索引筛选出来
        return tableIndexes.stream()
                .map(MySqlTableIndex::getIndexName)
                .filter(indexName-> !entityIndexNames.contains(indexName.toLowerCase()))
                .distinct()
                .collect(Collectors.toList());

    }

    /**
     * 找出需要更新的索引
     * @param tableIndexes 当前数据库的索引
     * @param entityIndexes entity中的所有字段
     * @return 需要新建的索引
     */
    private List<MySqlEntityIndex> getUpdateIndexes(List<MySqlTableIndex> tableIndexes, List<MySqlEntityIndex> entityIndexes) {
        if (CollUtil.isEmpty(entityIndexes)) {
            return new ArrayList<>(0);
        }
        // 现有的数据库索引配置
        List<String> tableIndexNames = tableIndexes.stream()
                .map(MySqlTableIndex::getIndexName)
                .map(String::toLowerCase)
                .distinct()
                .collect(Collectors.toList());

        // 现有的数据库索引配置 MAP
        val tableIndexMap = tableIndexes.stream()
                .collect(Collectors.groupingBy(index -> index.getIndexName().toLowerCase()));

        // 把两者都有的索引筛选出来进行对比
        return entityIndexes.stream()
                .filter(index->tableIndexNames.contains(index.getName().toLowerCase()))
                .filter(entityIndex->compareIndex(tableIndexMap.get(entityIndex.getName().toLowerCase()),entityIndex))
                .collect(Collectors.toList());
    }

    /**
     * 比对索引类型
     */
    private boolean compareIndex(List<MySqlTableIndex> tableIndex, MySqlEntityIndex entityIndex){
        MySqlTableIndex tableIndexInfo = tableIndex.get(0);

        /*
            获取现有的索引类型
            普通索引: non_unique: 1 index_type: BTREE
            全文索引: index_type: FULLTEXT
            唯一索引: non_unique: 0 index_type: BTREE
         */

        // 比对类型
        MySqlIndexType indexType = getIndexType(tableIndexInfo);
        if (indexType != entityIndex.getType()){
            return false;
        }

        // 比对索引字段
        tableIndex.sort(Comparator.comparing(MySqlTableIndex::getSeqInIndex));
        String tableColumnNames = tableIndex.stream()
                .map(MySqlTableIndex::getColumnName)
                .map(String::toLowerCase)
                .collect(Collectors.joining(","));
        String entityColumnNames = entityIndex.getColumns().stream()
                .map(String::toLowerCase)
                .collect(Collectors.joining(","));
        if (!Objects.equals(tableColumnNames,entityColumnNames)){
            return false;
        }

        // 比对索引备注
        if (!Objects.equals(tableIndexInfo.getIndexComment(),entityIndex.getComment())){
            return false;
        }
        return true;
    }

    /**
     * 获取索引类型
     */
    private MySqlIndexType getIndexType(MySqlTableIndex indexInfo) {
        if (Objects.equals(indexInfo.getIndexType(),"SPATIAL")){
            return MySqlIndexType.SPATIAL;
        }
        if (Objects.equals(indexInfo.getIndexType(), "BTREE") && !indexInfo.isNonUnique()) {
            return MySqlIndexType.NORMAL;
        }
        else if(Objects.equals(indexInfo.getIndexType(), "BTREE") && indexInfo.isNonUnique()){
            return MySqlIndexType.UNIQUE;
        } else {
            return MySqlIndexType.FULLTEXT;
        }
    }


    /**
     * 获取实体类配置的索引
     */
    private List<MySqlEntityIndex> getEntityIndexes(Class<?> clas){
        // 多个索引注释处理
        List<MySqlIndex> indexList = Optional.ofNullable(clas.getAnnotation(MySqlIndexes.class))
                .map(MySqlIndexes::value)
                .map(ListUtil::of)
                .orElse(new ArrayList<>(0));
        if (CollUtil.isEmpty(indexList)) {
            // 单个注解处理
            MySqlIndex mySqlIndex = clas.getAnnotation(MySqlIndex.class);
            if (Objects.nonNull(mySqlIndex)){
                indexList = ListUtil.of(mySqlIndex);
            }
        }

        return indexList.stream()
                .map(index -> new MySqlEntityIndex()
                        .setType(index.type())
                        .setName(MySqlInfoUtil.getIndexName(index))
                        .setColumns(Arrays.asList(index.columns()))
                        .setComment(index.comment()))
                .collect(Collectors.toList());
    }
}
