package cn.bootx.mybatis.table.modify.impl.mysql.service;

import cn.bootx.mybatis.table.modify.configuration.MybatisTableModifyProperties;
import cn.bootx.mybatis.table.modify.constants.UpdateType;
import cn.bootx.mybatis.table.modify.impl.mysql.entity.*;
import cn.bootx.mybatis.table.modify.impl.mysql.mapper.MySqlTableModifyMapper;
import cn.bootx.mybatis.table.modify.impl.mysql.util.MySqlInfoUtil;
import cn.hutool.core.collection.CollUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * MySQL表信息修改处理
 * @author xxm
 * @date 2023/4/7
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MySqlModifyTableService {

    private final MySqlTableModifyMapper mysqlTableModifyMapper;

    private final MybatisTableModifyProperties mybatisTableModifyProperties;

    /**
     * 根据传入的map创建或修改表结构
     * @param baseTableMap 操作sql的数据结构
     */
    public void modifyTableConstruct(MySqlModifyMap baseTableMap) {
        List<MySqlTableUpdate> updateTables = baseTableMap.getUpdateTables();
        for (MySqlTableUpdate table : updateTables) {
            MySqlModifyParam mySqlModifyParam = this.buildUpdateParam(table, baseTableMap);
            if (mySqlModifyParam.isUpdate()) {
                log.info("开始更新表：" + table.getName());
                try {
                    mysqlTableModifyMapper.modifyTable(mySqlModifyParam);
                    log.info("完成更新表：" + table.getName());
                } catch (Exception e){
                    log.error("更新表失败：" + table.getName(),e);
                }
            }
        }
    }


    /**
     * 构建建表语句
     */
    private MySqlModifyParam buildUpdateParam(MySqlTableUpdate table, MySqlModifyMap modifyMap){
        MySqlModifyParam mySqlModifyParam = new MySqlModifyParam();
        // 表基础信息
        mySqlModifyParam.setName(table.getName())
                .setEngine(table.getEngine())
                .setEngineUpdate(table.isEngineUpdate())
                .setComment(table.getComment())
                .setCommentUpdate(table.isCommentUpdate())
                .setCharset(table.getCharset())
                .setCharsetUpdate(table.isCharsetUpdate());

        // 主键是否更新
        if (table.isKeysUpdate()){
            mySqlModifyParam.setKeys(MySqlInfoUtil.buildBracketParams(table.getKeys()));
        }

        UpdateType updateType = mybatisTableModifyProperties.getUpdateType();

        // 新增字段处理
        if (CollUtil.isNotEmpty(modifyMap.getAddColumns().get(table.getName()))) {
            List<String> columns = modifyMap.getAddColumns()
                    .get(table.getName())
                    .stream()
                    .map(MySqlEntityColumn::toColumn)
                    .collect(Collectors.toList());
            mySqlModifyParam.setAddColumns(columns);
        }
        // 更新字段
        if (updateType != UpdateType.CREATE){
            if (CollUtil.isNotEmpty(modifyMap.getUpdateColumns().get(table.getName()))) {
                List<String> columns = modifyMap.getUpdateColumns()
                        .get(table.getName())
                        .stream()
                        .map(MySqlEntityColumn::toColumn)
                        .collect(Collectors.toList());
                mySqlModifyParam.setModifyColumns(columns);
            }
        }
        // 删除字段
        if (updateType != UpdateType.CREATE){
            if (CollUtil.isNotEmpty(modifyMap.getDropColumns().get(table.getName()))) {
                mySqlModifyParam.setDropColumns(modifyMap.getDropColumns().get(table.getName()));
            }
        }

        // 删除索引
        if (updateType != UpdateType.CREATE){
            if (CollUtil.isNotEmpty(modifyMap.getDropIndexes().get(table.getName()))) {
                mySqlModifyParam.setDropIndexes(modifyMap.getDropIndexes().get(table.getName()));
            }
        }

        // 新增索引
        if (CollUtil.isNotEmpty(modifyMap.getAddIndexes().get(table.getName()))) {
            List<String> indexes = modifyMap.getAddIndexes()
                    .get(table.getName())
                    .stream()
                    .map(MySqlEntityIndex::toIndex)
                    .collect(Collectors.toList());
            mySqlModifyParam.setAddIndexes(indexes);
        }

        return mySqlModifyParam;
    }

}
