package cn.bootx.table.modify.mybatis.mysq.service;

import cn.bootx.table.modify.constants.UpdateType;
import cn.bootx.table.modify.mybatis.mysq.entity.*;
import cn.bootx.table.modify.mybatis.mysq.mapper.MySqlTableModifyDao;
import cn.bootx.table.modify.mybatis.mysq.util.MySqlInfoUtil;
import cn.bootx.table.modify.properties.MybatisTableModifyProperties;
import cn.hutool.core.collection.CollUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    private final MySqlTableModifyDao mySqlTableModifyDao;

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
                    mySqlTableModifyDao.modifyTable(mySqlModifyParam);
                    log.info("完成更新表：" + table.getName());
                } catch (Exception e){
                    log.error("更新表失败：" + table.getName(),e);
                    // 快速失败
                    if (mybatisTableModifyProperties.isFailFast()){
                        throw e;
                    }
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

            List<String> delIndexes = new ArrayList<>();
            // 要进行删除的索引
            if (CollUtil.isNotEmpty(modifyMap.getDropIndexes().get(table.getName()))) {
                delIndexes.addAll(modifyMap.getDropIndexes().get(table.getName()));
            }
            // 更新的索引需要先删后增
            if (CollUtil.isNotEmpty(modifyMap.getUpdateIndexes().get(table.getName()))){
                List<String> list = modifyMap.getUpdateIndexes().get(table.getName())
                        .stream()
                        .map(MySqlEntityIndex::getName)
                        .collect(Collectors.toList());
                delIndexes.addAll(list);
            }

            if (CollUtil.isNotEmpty(delIndexes)){
                mySqlModifyParam.setDropIndexes(delIndexes);
            }
        }

        // 新增索引
        if (CollUtil.isNotEmpty(modifyMap.getAddIndexes().get(table.getName()))) {
            // 要进行添加的索引
            List<String> addIndexes = modifyMap.getAddIndexes().get(table.getName())
                    .stream()
                    .map(MySqlEntityIndex::toIndex)
                    .collect(Collectors.toList());
            // 更新的索引需要先删后增
            if (CollUtil.isNotEmpty(modifyMap.getUpdateIndexes().get(table.getName()))){{
                List<String> list = modifyMap.getUpdateIndexes().get(table.getName())
                        .stream()
                        .map(MySqlEntityIndex::toIndex)
                        .collect(Collectors.toList());
                addIndexes.addAll(list);
            }}
            mySqlModifyParam.setAddIndexes(addIndexes);
        }

        return mySqlModifyParam;
    }

}
