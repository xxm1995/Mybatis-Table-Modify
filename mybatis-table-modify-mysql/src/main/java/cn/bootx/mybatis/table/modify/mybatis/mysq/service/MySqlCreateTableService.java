package cn.bootx.mybatis.table.modify.mybatis.mysq.service;

import cn.bootx.mybatis.table.modify.constants.TableCharset;
import cn.bootx.mybatis.table.modify.mybatis.mysq.constants.MySqlEngineEnum;
import cn.bootx.mybatis.table.modify.impl.mysql.entity.*;
import cn.bootx.mybatis.table.modify.mybatis.mysq.mapper.MySqlTableModifyMapper;
import cn.bootx.mybatis.table.modify.mybatis.mysq.util.MySqlInfoUtil;
import cn.bootx.mybatis.table.modify.mybatis.mysq.entity.*;
import cn.hutool.core.collection.CollUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * MySQL表信息创建处理
 * @author xxm
 * @date 2023/4/19
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MySqlCreateTableService {

    private final MySqlTableModifyMapper mysqlTableModifyMapper;

    /**
     * 根据map结构创建表
     * @param modifyMap 用于存需要创建表的数据
     */
    public void createTable(MySqlModifyMap modifyMap) {
        // 做创建表操作
        for (MySqlEntityTable table : modifyMap.getCreateTables()) {
            log.info("开始创建表：" + table.getName());
            try {
                mysqlTableModifyMapper.createTable(this.buildCreateParam(table,modifyMap));
                log.info("完成创建表：" + table.getName());
            } catch (Exception e){
                log.error("创建表失败：" + table.getName(),e);
            }
        }
    }


    /**
     * 构建建表语句
     */
    private MySqlCreateParam buildCreateParam(MySqlEntityTable table, MySqlModifyMap modifyMap){
        MySqlCreateParam mySqlCreateParam = new MySqlCreateParam();
        // 表基础信息
        mySqlCreateParam.setName(table.getName())
                .setEngine(Optional.ofNullable(table.getEngine()).map(MySqlEngineEnum::getValue).orElse(null))
                .setComment(table.getComment())
                .setCharset(Optional.ofNullable(table.getCharset()).map(TableCharset::getValue).orElse(null));


        // 字段
        List<MySqlEntityColumn> columns = modifyMap.getAddColumns()
                .get(table.getName());
        if (CollUtil.isNotEmpty(columns)) {
            List<String> list = columns.stream()
                    .map(MySqlEntityColumn::toColumn)
                    .collect(Collectors.toList());
            mySqlCreateParam.setColumns(list);
        }
        // 主键
        List<String> keys = table.getKeys();
        if (CollUtil.isNotEmpty(keys)){
            mySqlCreateParam.setKeys(MySqlInfoUtil.buildBracketParams(keys));
        }
        // 索引
        List<MySqlEntityIndex> indexes = modifyMap.getAddIndexes()
                .get(table.getName());
        if (CollUtil.isNotEmpty(indexes)){
            List<String> list = indexes.stream()
                    .map(MySqlEntityIndex::toIndex)
                    .collect(Collectors.toList());
            mySqlCreateParam.setIndexes(list);
        }


        return mySqlCreateParam;
    }

}
