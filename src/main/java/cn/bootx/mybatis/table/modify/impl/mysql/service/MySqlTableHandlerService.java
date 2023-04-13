package cn.bootx.mybatis.table.modify.impl.mysql.service;

import cn.bootx.mybatis.table.modify.annotation.*;
import cn.bootx.mybatis.table.modify.configuration.MybatisTableModifyProperties;
import cn.bootx.mybatis.table.modify.constants.TableCharset;
import cn.bootx.mybatis.table.modify.handler.TableHandlerService;
import cn.bootx.mybatis.table.modify.impl.mysql.annotation.MySqlFieldType;
import cn.bootx.mybatis.table.modify.impl.mysql.annotation.MySqlIndex;
import cn.bootx.mybatis.table.modify.impl.mysql.annotation.MySqlEngine;
import cn.bootx.mybatis.table.modify.impl.mysql.annotation.MySqlIndexes;
import cn.bootx.mybatis.table.modify.impl.mysql.constants.MySqlEngineEnum;
import cn.bootx.mybatis.table.modify.impl.mysql.constants.MySql4JavaType;
import cn.bootx.mybatis.table.modify.impl.mysql.constants.MySqlFieldTypeEnum;
import cn.bootx.mybatis.table.modify.impl.mysql.entity.*;
import cn.bootx.mybatis.table.modify.impl.mysql.mapper.MySqlTableModifyMapper;
import cn.bootx.mybatis.table.modify.utils.ClassScanner;
import cn.bootx.mybatis.table.modify.utils.ColumnUtils;
import cn.bootx.mybatis.table.modify.constants.DatabaseType;
import cn.bootx.mybatis.table.modify.constants.UpdateType;
import cn.bootx.mybatis.table.modify.impl.mysql.entity.MySqlModifyMap;
import cn.bootx.mybatis.table.modify.impl.mysql.entity.MySqlEntityColumn;
import cn.bootx.mybatis.table.modify.domain.TableConfig;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

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

    private final MySqlIndexHandlerService mySqlIndexHandlerService;

    private final MySqlColumnHandlerService mySqlColumnHandlerService;

    private final MySqlTableModifyService mySqlTableModifyService;

    private final MybatisTableModifyProperties mybatisTableModifyProperties;

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

        // 要扫描的model所在的pack
        String pack = mybatisTableModifyProperties.getScanPackage();

        // 拆成多个pack，支持多个
        String[] packs = pack.split("[,;]");

        // 从包package中获取所有的Class
        Set<Class<?>> classes = ClassScanner.scan(packs, DbTable.class, TableName.class);

        // 初始化用于存储各种操作表结构的容器
        MySqlModifyMap baseTableMap = new MySqlModifyMap();

        // 表名集合
        List<String> tableNames = new ArrayList<>();

        // 循环全部的model
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
        this.createOrModifyTableConstruct(baseTableMap, updateType);
    }

    /**
     * 构建出全部表的增删改的map
     * @param clas package中的model的Class
     * @param baseTableMap 用于存储各种操作表结构的容器
     */
    private void buildTableMapConstruct(Class<?> clas, MySqlModifyMap baseTableMap,
                                        UpdateType updateType) {
        // 获取model的tableName
        String tableName = ColumnUtils.getTableName(clas);

        // 如果配置文件配置的是DROP_CREATE，表示将所有的表删掉重新创建
        if (updateType == UpdateType.DROP_CREATE) {
            log.info("由于配置的模式是DROP_CREATE，因此先删除表后续根据结构重建，删除表：{}", tableName);
            mysqlTableModifyMapper.dropTableByName(tableName);
        }

        // 先查该表是否以存在
        if (Objects.isNull(mysqlTableModifyMapper.existsByTableName(tableName))){
            // 获取表信息
            getCreateTable(clas,baseTableMap);
            // 获取建表字段
            mySqlColumnHandlerService.getCreateColumn(clas,baseTableMap);
            // 获取索引
            mySqlIndexHandlerService.getCreateIndex(clas,baseTableMap);
        } else {
            // 获取表信息
            getModifyTable(clas,baseTableMap);
            // 获取建表字段
            mySqlColumnHandlerService.getModifyColumn(clas,baseTableMap);
            // 获取索引
            mySqlIndexHandlerService.getModifyIndex(clas,baseTableMap);
        }

    }

    /**
     * 根据传入的信息，分别去创建或修改表结构
     */
    private void createOrModifyTableConstruct(MySqlModifyMap baseTableMap, UpdateType updateType){
        // 1. 创建表
        mySqlTableModifyService.createTable(baseTableMap.getCreateTables());
        // 2. 修改表结构
        mySqlTableModifyService.modifyTableConstruct(baseTableMap,updateType);
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

    /**
     * 创建表
     */
    public void getCreateTable(Class<?> clas,MySqlModifyMap baseTableMap){
        String tableName = ColumnUtils.getTableName(clas);

        // 获取表注释
        String tableComment = ColumnUtils.getTableComment(clas);

        // 获取表字符集
        TableCharset tableCharset = ColumnUtils.getTableCharset(clas);

        // 获取表引擎
        MySqlEngineEnum tableEngine = getTableEngine(clas);
    }

    /**
     * 更新表信息
     */
    public void getModifyTable(Class<?> clas,MySqlModifyMap baseTableMap){

    }

    /**
     * 数据表是需要创建还是更新
     */
    private boolean getCreateOrModify(String tableName, Class<?> clas, List<Object> allFieldList, MySqlModifyMap baseTableMap){

        // 获取表注释
        String tableComment = ColumnUtils.getTableComment(clas);

        // 获取表字符集
        TableCharset tableCharset = ColumnUtils.getTableCharset(clas);

        // 获取表引擎
        MySqlEngineEnum tableEngine = getTableEngine(clas);

        // 表不存在时, 添加到新建表容器中
        Map<String, Object> map = new HashMap<>();
        if (Objects.isNull(tableInfo)) {
            if (StrUtil.isNotBlank(tableComment)) {
                map.put(MySqlTableInfo.TABLE_COMMENT_KEY, tableComment);
            }
            if (tableCharset != null && tableCharset != TableCharset.DEFAULT) {
                map.put(MySqlTableInfo.TABLE_COLLATION_KEY, tableCharset.getValue().toLowerCase());
            }
            if (tableEngine != null && tableEngine != MySqlEngineEnum.DEFAULT) {
                map.put(MySqlTableInfo.TABLE_ENGINE_KEY, tableEngine.toString());
            }
            baseTableMap.getCreateTables().put(tableName, new TableConfig(allFieldList, map));
            baseTableMap.getAddIndexTable().put(tableName, new TableConfig(getAddIndexes(null, allFieldList)));
            baseTableMap.getAddUniqueTable().put(tableName, new TableConfig(getAddUniqueList(null, allFieldList)));
            return true;
        }
        else {
            // 判断表注释是否要更新
            if (StrUtil.isNotBlank(tableComment) && !Objects.equals(tableComment, tableInfo.getTableComment())) {
                map.put(MySqlTableInfo.TABLE_COMMENT_KEY, tableComment);
            }
            // 判断表字符集是否要更新
            if (tableCharset != null && tableCharset != TableCharset.DEFAULT
                    && !tableCharset.getValue()
                    .toLowerCase()
                    .equals(tableInfo.getTableCollation().replace(MySqlTableInfo.TABLE_COLLATION_SUFFIX, ""))) {
                map.put(MySqlTableInfo.TABLE_COLLATION_KEY, tableCharset.getValue().toLowerCase());
            }
            // 判断表引擎是否要更新
            if (tableEngine != null && tableEngine != MySqlEngineEnum.DEFAULT
                    && !tableEngine.toString().equals(tableInfo.getEngine())) {
                map.put(MySqlTableInfo.TABLE_ENGINE_KEY, tableEngine.toString());
            }
            baseTableMap.getModifyComments().put(tableName, new TableConfig(map));
        }
        return false;
    }


    /**
     * 返回需要删除主键的字段
     * @param tableColumnList 表结构
     * @param allFieldList model中的所有字段
     * @return 需要删除主键的字段
     */
    private List<Object> getDropKeyFieldList(List<MysqlTableColumnInfo> tableColumnList, List<Object> allFieldList) {
        Map<String, MySqlEntityColumn> fieldMap = getAllFieldMap(allFieldList);
        List<Object> dropKeyFieldList = new ArrayList<>();
        for (MysqlTableColumnInfo sysColumn : tableColumnList) {
            // 数据库中有该字段时
            MySqlEntityColumn columnParam = fieldMap.get(sysColumn.getColumnName().toLowerCase());
            if (columnParam != null) {
                // 原本是主键，现在不是了，那么要去做删除主键的操作
                if ("PRI".equals(sysColumn.getColumnKey()) && !columnParam.isKey()) {
                    dropKeyFieldList.add(columnParam);
                }

            }
        }
        return dropKeyFieldList;
    }

    /**
     * 将allFieldList转换为Map结构
     */
    private Map<String, MySqlEntityColumn> getAllFieldMap(List<Object> allFieldList) {
        // 将fieldList转成Map类型，字段名作为主键
        Map<String, MySqlEntityColumn> fieldMap = new HashMap<>();
        for (Object obj : allFieldList) {
            MySqlEntityColumn columnParam = (MySqlEntityColumn) obj;
            fieldMap.put(columnParam.getColumnName().toLowerCase(), columnParam);
        }
        return fieldMap;
    }

    /**
     * 获取表引擎类型
     */
    public MySqlEngineEnum getTableEngine(Class<?> clazz) {
        MySqlEngine mySqlEngine = clazz.getAnnotation(MySqlEngine.class);
        if (!ColumnUtils.hasTableAnnotation(clazz)) {
            return null;
        }
        if (mySqlEngine != null && mySqlEngine.value() != MySqlEngineEnum.DEFAULT) {
            return mySqlEngine.value();
        }
        return null;
    }
}
