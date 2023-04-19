package cn.bootx.mybatis.table.modify.impl.mysql.service;

import cn.bootx.mybatis.table.modify.annotation.*;
import cn.bootx.mybatis.table.modify.configuration.MybatisTableModifyProperties;
import cn.bootx.mybatis.table.modify.handler.TableHandlerService;
import cn.bootx.mybatis.table.modify.impl.mysql.mapper.MySqlTableModifyMapper;
import cn.bootx.mybatis.table.modify.utils.ClassScanner;
import cn.bootx.mybatis.table.modify.utils.ColumnUtils;
import cn.bootx.mybatis.table.modify.constants.DatabaseType;
import cn.bootx.mybatis.table.modify.constants.UpdateType;
import cn.bootx.mybatis.table.modify.impl.mysql.entity.MySqlModifyMap;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @author sunchenbin
 * @version 2016年6月23日 下午6:07:21
 */
@Slf4j
@Component
@Transactional
@RequiredArgsConstructor
public class MySqlTableHandlerService implements TableHandlerService {

    private final MySqlTableModifyMapper mysqlTableModifyMapper;

    private final MySqlIndexInfoService mySqlIndexInfoServiceService;

    private final MySqlColumnInfoService mySqlColumnInfoService;

    private final MySqlCreateTableService mySqlCreateTableService;

    private final MySqlModifyTableService mySqlModifyTableService;

    private final MybatisTableModifyProperties mybatisTableModifyProperties;

    private final MySqlTableInfoService mySqlTableInfoService;

    /**
     * 处理器类型
     */
    @Override
    public DatabaseType getDatabaseType() {
        return DatabaseType.MYSQL;
    }

    /**
     * 读取配置文件的三种状态（创建表、更新表、不做任何事情）
     */
    @Override
    public void startModifyTable() {
        log.debug("开始执行MySql的处理方法");

        // 自动创建模式：update表示更新，create表示删除原表重新创建
        UpdateType updateType = mybatisTableModifyProperties.getUpdateType();

        // 要扫描的entity所在的pack
        String pack = mybatisTableModifyProperties.getScanPackage();

        // 拆成多个pack，支持多个
        String[] packs = pack.split("[,;]");

        // 从包package中获取所有的Class
        Set<Class<?>> classes = ClassScanner.scan(packs, DbTable.class, TableName.class);

        // 初始化用于存储各种操作表结构的容器
        MySqlModifyMap baseTableMap = new MySqlModifyMap();

        // 表名集合
        List<String> tableNames = new ArrayList<>();

        // 循环全部的entity
        for (Class<?> clas : classes) {
            // 没有打注解不需要创建表 或者配置了忽略建表的注解
            if (!ColumnUtils.hasTableAnnotation(clas)) {
                continue;
            }
            // 添加要处理的表, 同时检查是表是否合法
            this.addAndCheckTable(tableNames, clas);
            // 构建出全部表的增删改的map
            this.buildTableMapConstruct(clas, baseTableMap, updateType);
        }

        // 根据传入的信息，分别去创建或修改表结构
        this.createOrModifyTableConstruct(baseTableMap);
    }

    /**
     * 构建出全部表的增删改的map
     * @param clas package中的entity的Class
     * @param baseTableMap 用于存储各种操作表结构的容器
     */
    private void buildTableMapConstruct(Class<?> clas, MySqlModifyMap baseTableMap,
                                        UpdateType updateType) {
        // 获取entity的tableName
        String tableName = ColumnUtils.getTableName(clas);

        // 如果配置文件配置的是DROP_CREATE，表示将所有的表删掉重新创建
        if (updateType == UpdateType.DROP_CREATE) {
            log.info("由于配置的模式是DROP_CREATE，因此先删除表后续根据结构重建，删除表：{}", tableName);
            mysqlTableModifyMapper.dropTableByName(tableName);
        }

        // 先查该表是否以存在
        if (mysqlTableModifyMapper.existsByTableName(tableName)){
            // 获取表更新信息
            mySqlTableInfoService.getModifyTable(clas,baseTableMap);
            // 获取表更新字段
            mySqlColumnInfoService.getModifyColumn(clas,baseTableMap);
            // 获取更新索引
            mySqlIndexInfoServiceService.getModifyIndex(clas,baseTableMap);
        } else {
            // 获取建表信息
            mySqlTableInfoService.getCreateTable(clas,baseTableMap);
            // 获取建表字段
            mySqlColumnInfoService.getCreateColumn(clas,baseTableMap);
            // 获取建表索引
            mySqlIndexInfoServiceService.getCreateIndex(clas,baseTableMap);
        }

    }

    /**
     * 根据传入的信息，分别去创建或修改表结构
     */
    private void createOrModifyTableConstruct(MySqlModifyMap baseTableMap){
        // 1. 创建表
        mySqlCreateTableService.createTable(baseTableMap);
        // 2. 修改表结构
        mySqlModifyTableService.modifyTableConstruct(baseTableMap);
    }

    /**
     * 检查名称
     */
    private void addAndCheckTable(List<String> tableNames, Class<?> clas) {
        String tableName = ColumnUtils.getTableName(clas);
        if (tableNames.contains(tableName)) {
            throw new RuntimeException(tableName + "表名出现重复，禁止创建！");
        }
        tableNames.add(tableName);
    }

}
