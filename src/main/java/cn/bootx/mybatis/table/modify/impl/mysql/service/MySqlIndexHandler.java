package cn.bootx.mybatis.table.modify.impl.mysql.service;

import cn.bootx.mybatis.table.modify.impl.mysql.annotation.MySqlIndex;
import cn.bootx.mybatis.table.modify.impl.mysql.annotation.MySqlIndexes;
import cn.bootx.mybatis.table.modify.impl.mysql.entity.MySqlEntityIndexInfo;
import cn.bootx.mybatis.table.modify.impl.mysql.entity.MySqlModifyMap;
import cn.bootx.mybatis.table.modify.impl.mysql.entity.MySqlTableIndexInfo;
import cn.bootx.mybatis.table.modify.impl.mysql.mapper.MySqlTableModifyMapper;
import cn.bootx.mybatis.table.modify.utils.ColumnUtils;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

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
public class MySqlIndexHandler {
    private final MySqlTableModifyMapper mysqlTableModifyMapper;

    /**
     * 获取创建表锁需要的索引 不包括主键索引
     * @param clas 实体类
     * @param baseTableMap 用于存储各种操作MySQL表结构的容器
     */
    public void getCreateIndex(Class<?> clas, MySqlModifyMap baseTableMap){
        // 获取model的tableName
        String tableName = ColumnUtils.getTableName(clas);
        // 找出实体类所有的索引
        List<MySqlEntityIndexInfo> entityIndexes = getEntityIndexes(clas);
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

        // 获取model的tableName
        String tableName = ColumnUtils.getTableName(clas);

        // 找出实体类所有的索引
        List<MySqlEntityIndexInfo> entityIndexes = getEntityIndexes(clas);

        // 查询当前表中全部索引
        List<MySqlTableIndexInfo> tableIndexes = mysqlTableModifyMapper.findIndexByTableName(tableName);

        // 找出需要删除的索引
        List<String> dropIndexes = getDropIndexes(tableIndexes, entityIndexes);
        if (CollUtil.isNotEmpty(dropIndexes)) {
            baseTableMap.getDropIndexes().put(tableName, dropIndexes);
        }
        // 找出需要新增的索引
        List<MySqlEntityIndexInfo> addIndexes = getAddIndexes(tableIndexes, entityIndexes);
        if (CollUtil.isNotEmpty(addIndexes)) {
            baseTableMap.getAddIndexes().put(tableName, addIndexes);
        }
        // 找出需要修改的索引
        List<MySqlEntityIndexInfo> updateIndexes = getUpdateIndexes(tableIndexes, entityIndexes);
        if (CollUtil.isNotEmpty(addIndexes)) {
            baseTableMap.getAddIndexes().put(tableName, addIndexes);
        }
    }


    /**
     * 找出需要新建的索引 不包括主键索引
     * @param tableIndexes 当前数据库的索引
     * @param entityIndexes model中的所有字段
     * @return 需要新建的索引
     */
    private List<MySqlEntityIndexInfo> getAddIndexes(List<MySqlTableIndexInfo> tableIndexes, List<MySqlEntityIndexInfo> entityIndexes) {
        if (CollUtil.isEmpty(entityIndexes)) {
            return new ArrayList<>(0);
        }
        List<String> allIndexNames = tableIndexes.stream()
                .map(MySqlTableIndexInfo::getIndexName)
                .distinct()
                .collect(Collectors.toList());

        return entityIndexes.stream()
                .filter(index->!allIndexNames.contains(index.getName()))
                .collect(Collectors.toList());
    }

    /**
     * 找出需要删除的索引
     * @param tableIndexes 当前数据库的索引
     * @param entityIndexes model中所有配置的索引
     * @return 需要删除的索引名称
     */
    private List<String> getDropIndexes(List<MySqlTableIndexInfo> tableIndexes, List<MySqlEntityIndexInfo> entityIndexes) {
        if (CollUtil.isEmpty(tableIndexes)) {
            return new ArrayList<>(0);
        }

        // 定义的索引名称集合
        List<String> allFieldNameList = entityIndexes.stream()
                .map(MySqlEntityIndexInfo::getName)
                .map(String::toLowerCase)
                .distinct()
                .collect(Collectors.toList());
        // 将要删除的索引筛选出来
        return tableIndexes.stream()
                .map(MySqlTableIndexInfo::getIndexName)
                .filter(indexName-> !allFieldNameList.contains(indexName.toLowerCase()))
                .distinct()
                .collect(Collectors.toList());

    }

    /**
     * 找出需要更新的索引
     * @param tableIndexes 当前数据库的索引
     * @param entityIndexes model中的所有字段
     * @return 需要新建的索引
     */
    private List<MySqlEntityIndexInfo> getUpdateIndexes(List<MySqlTableIndexInfo> tableIndexes, List<MySqlEntityIndexInfo> entityIndexes) {
        if (CollUtil.isEmpty(entityIndexes)) {
            return new ArrayList<>(0);
        }
        // 现有的数据库索引配置
        List<String> tableIndexNames = tableIndexes.stream()
                .map(MySqlTableIndexInfo::getIndexName)
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
    private boolean compareIndex(List<MySqlTableIndexInfo> tableIndex, MySqlEntityIndexInfo entityIndex){
        MySqlTableIndexInfo indexInfo = tableIndex.get(0);

        /*
            获取现有的索引类型
            普通索引:
            全文索引:
            唯一索引:
         */
        indexInfo.getIndexType();


        // 比对类型

        // 比对索引字段
        tableIndex.sort(Comparator.comparing(MySqlTableIndexInfo::getSeqInIndex));
        List<String> columnNames = tableIndex.stream()
                .map(MySqlTableIndexInfo::getColumnName)
                .collect(Collectors.toList());
        // 比对索引备注
        return true;
    }


    /**
     * 获取实体类配置的索引
     */
    private List<MySqlEntityIndexInfo> getEntityIndexes(Class<?> clas){
        List<MySqlIndex> indexList = Optional.ofNullable(clas.getAnnotation(MySqlIndexes.class))
                .map(MySqlIndexes::value)
                .map(ListUtil::of)
                .orElse(null);
        if (CollUtil.isEmpty(indexList)) {
            indexList = ListUtil.of(clas.getAnnotation(MySqlIndex.class));
        }
        return indexList.stream()
                .map(index -> new MySqlEntityIndexInfo()
                        .setType(index.type())
                        .setName(index.name())
                        .setColumns(Arrays.asList(index.columns()))
                        .setComment(index.comment()))
                .collect(Collectors.toList());
    }
}
