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
import cn.bootx.mybatis.table.modify.utils.ClassTools;
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
            // 添加要处理的表, 同时检查是否有重名表
            this.addAndCheckTableName(tableNames, clas);
            // 构建出全部表的增删改的map
            this.buildTableMapConstruct(clas, baseTableMap, updateType);
        }

        // 根据传入的信息，分别去创建或修改表结构
        this.createOrModifyTableConstruct(baseTableMap, updateType);
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
    private void addAndCheckTableName(List<String> tableNames, Class<?> clas) {
        String tableName = ColumnUtils.getTableName(clas);
        if (tableNames.contains(tableName)) {
            throw new RuntimeException(tableName + "表名出现重复，禁止创建！");
        }
        tableNames.add(tableName);
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
            getCreateColumn(clas,baseTableMap);
            // 获取索引
            getCreateIndex(clas,baseTableMap);
        } else {
            // 获取表信息
            getModifyTable(clas,baseTableMap);
            // 获取建表字段
            getModifyColumn(clas,baseTableMap);
            // 获取索引
            getModifyIndex(clas,baseTableMap);
        }

    }

    /**
     * 获取创建表锁需要的索引 不包括主键索引
     * @param clas 实体类
     * @param baseTableMap 用于存储各种操作MySQL表结构的容器
     */
    private void getCreateIndex(Class<?> clas, MySqlModifyMap baseTableMap){
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
    private void getModifyIndex(Class<?> clas, MySqlModifyMap baseTableMap){

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
        List<MySqlEntityIndexInfo> addIndexes = getAddIndexList(tableIndexes, entityIndexes);
        if (CollUtil.isNotEmpty(addIndexes)) {
            baseTableMap.getAddIndexes().put(tableName, addIndexes);
        }
        // 找出需要修改的索引
        List<MySqlEntityIndexInfo> updateIndexes = getUpdateIndexList(tableIndexes, entityIndexes);
        if (CollUtil.isNotEmpty(addIndexes)) {
            baseTableMap.getAddIndexes().put(tableName, addIndexes);
        }
    }

    /**
     * 获取建表的字段
     */
    public void getCreateColumn(Class<?> clas,  MySqlModifyMap baseTableMap){

        // 获取model的tableName
        String tableName = ColumnUtils.getTableName(clas);

        // 用于实体类配置的全部字段
        List<MySqlEntityColumn> allFields;
        try {
            allFields = getAllFields(clas);
            if (allFields.size() == 0) {
                log.warn("扫描发现" + clas.getName() + "没有建表字段请检查！");
                return;
            }
        }
        catch (Exception e) {
            log.error("表：{}，初始化字段结构失败！", tableName);
            throw new RuntimeException(e.getMessage());
        }

        // 从sysColumns中取出我们需要比较的列的List
        // 先取出name用来筛选出增加和删除的字段
        List<String> columnNames = ClassTools.getPropertyValueList(tableColumns, MysqlTableColumnInfo.COLUMN_NAME_KEY);

        // 验证对比从model中解析的allFieldList与从数据库查出来的columnList

        // 找出增加的字段
        List<Object> addFieldList = getAddFieldList(allFields, columnNames);

        // 找出删除的字段
        List<Object> removeFieldList = getRemoveFieldList(columnNames, allFields);

        // 找出更新的字段
        List<Object> modifyFieldList = getModifyFieldList(tableColumns, allFields);

        // 找出需要删除主键的字段
        List<Object> dropKeyFieldList = getDropKeyFieldList(tableColumns, allFields);

        if (addFieldList.size() != 0) {
            baseTableMap.getAddColumns().put(tableName, new TableConfig(addFieldList));
        }
        if (removeFieldList.size() != 0) {
            baseTableMap.getRemoveColumns().put(tableName, new TableConfig(removeFieldList));
        }
        if (modifyFieldList.size() != 0) {
            baseTableMap.getUpdateColumns().put(tableName, new TableConfig(modifyFieldList));
        }
        if (dropKeyFieldList.size() != 0) {
            baseTableMap.getDropKeys().put(tableName, new TableConfig(dropKeyFieldList));
        }
    }

    /**
     * 获取要更新的字段
     */
    public void getModifyColumn(Class<?> clas,  MySqlModifyMap baseTableMap) {
        // 获取model的tableName
        String tableName = ColumnUtils.getTableName(clas);

        // 用于实体类配置的全部字段
        List<MySqlEntityColumn> allFields;
        try {
            allFields = getAllFields(clas);
            if (allFields.size() == 0) {
                log.warn("扫描发现" + clas.getName() + "没有建表字段请检查！");
                return;
            }
        }
        catch (Exception e) {
            log.error("表：{}，初始化字段结构失败！", tableName);
            throw new RuntimeException(e.getMessage());
        }
        // 数据库表结构
        List<MysqlTableColumnInfo> tableColumns = mysqlTableModifyMapper.findColumnByTableName(tableName);

        // 找出需要删除的字段

        // 找出需要新增的字段

        // 找出需要修改的字段

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
            baseTableMap.getAddIndexTable().put(tableName, new TableConfig(getAddIndexList(null, allFieldList)));
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
     * 找出需要新建的索引
     * @param tableIndexes 当前数据库的索引
     * @param entityIndexes model中的所有字段
     * @return 需要新建的索引
     */
    private List<MySqlEntityIndexInfo> getAddIndexList(List<MySqlTableIndexInfo> tableIndexes, List<MySqlEntityIndexInfo> entityIndexes) {
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
                .distinct()
                .collect(Collectors.toList());
        // 将要删除的索引筛选出来
        return tableIndexes.stream()
                .map(MySqlTableIndexInfo::getIndexName)
                .filter(indexName-> !allFieldNameList.contains(indexName))
                .distinct()
                .collect(Collectors.toList());

    }

    /**
     * 找出需要更新的索引
     * @param tableIndexes 当前数据库的索引
     * @param entityIndexes model中的所有字段
     * @return 需要新建的索引
     */
    private List<MySqlEntityIndexInfo> getUpdateIndexList(List<MySqlTableIndexInfo> tableIndexes, List<MySqlEntityIndexInfo> entityIndexes) {
        if (CollUtil.isEmpty(entityIndexes)) {
            return new ArrayList<>(0);
        }
        // 现有的数据库索引配置
        List<String> tableIndexNames = tableIndexes.stream()
                .map(MySqlTableIndexInfo::getIndexName)
                .distinct()
                .collect(Collectors.toList());

        // 现有的数据库索引配置 MAP
        val tableIndexMap = tableIndexes.stream()
                .collect(Collectors.groupingBy(MySqlTableIndexInfo::getIndexName));

        // 把两者都有的索引筛选出来进行对比
        return entityIndexes.stream()
                .filter(index->tableIndexNames.contains(index.getName()))
                .filter(entityIndex->compareIndex(tableIndexMap.get(entityIndex.getName()),entityIndex))
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
        entityIndex.
        // 比对索引备注
        return true;
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
                if ("PRI".equals(sysColumn.getColumnKey()) && !columnParam.isFieldIsKey()) {
                    dropKeyFieldList.add(columnParam);
                }

            }
        }
        return dropKeyFieldList;
    }

    /**
     * 根据数据库中表的结构和model中表的结构对比找出修改类型默认值等属性的字段
     * @return 需要修改的字段
     */
    private List<Object> getModifyFieldList(List<MysqlTableColumnInfo> tableColumnList, List<Object> allFieldList) {
        Map<String, MySqlEntityColumn> fieldMap = getAllFieldMap(allFieldList);
        List<Object> modifyFieldList = new ArrayList<>();
        for (MysqlTableColumnInfo sysColumn : tableColumnList) {
            // 数据库中有该字段时，验证是否有更新
            MySqlEntityColumn columnParam = fieldMap.get(sysColumn.getColumnName().toLowerCase());
            if (columnParam != null && !columnParam.isIgnoreUpdate()) {
                // 该复制操作时为了解决multiple primary key defined的同时又不会drop primary key
                MySqlEntityColumn modifyTableParam = columnParam.clone();
                // 1.验证主键
                // 原本不是主键，现在变成了主键，那么要去做更新
                if (!"PRI".equals(sysColumn.getColumnKey()) && columnParam.isFieldIsKey()) {
                    modifyFieldList.add(modifyTableParam);
                    continue;
                }
                // 原本是主键，现在依然主键，坚决不能在alter语句后加primary key，否则会报multiple primary
                // key defined
                if ("PRI".equals(sysColumn.getColumnKey()) && columnParam.isFieldIsKey()) {
                    modifyTableParam.setFieldIsKey(false);
                }
                // 2.验证类型
                if (!sysColumn.getDataType().equalsIgnoreCase(columnParam.getFieldType())) {
                    modifyFieldList.add(modifyTableParam);
                    continue;
                }
                // 3.验证长度个小数点位数
                String typeAndLength = columnParam.getFieldType().toLowerCase();
                if (columnParam.getFileTypeLength() == 1) {
                    // 拼接出类型加长度，比如varchar(1)
                    typeAndLength = typeAndLength + "(" + columnParam.getParamCount() + ")";
                }
                else if (columnParam.getFileTypeLength() == 2) {
                    // 拼接出类型加长度，比如varchar(1)
                    typeAndLength = typeAndLength + "(" + columnParam.getParamCount() + ","
                            + columnParam.getFieldDecimalLength() + ")";
                }

                // 判断类型+长度是否相同
                if (!sysColumn.getColumnType().toLowerCase().equals(typeAndLength)) {
                    modifyFieldList.add(modifyTableParam);
                    continue;
                }
                // 5.验证自增
                if ("auto_increment".equals(sysColumn.getExtra()) && !columnParam.isFieldIsAutoIncrement()) {
                    modifyFieldList.add(modifyTableParam);
                    continue;
                }
                if (!"auto_increment".equals(sysColumn.getExtra()) && columnParam.isFieldIsAutoIncrement()) {
                    modifyFieldList.add(modifyTableParam);
                    continue;
                }
                // 6.验证默认值
                if (sysColumn.getColumnDefault() == null || sysColumn.getColumnDefault().equals("")) {
                    // 数据库默认值是null，model中注解设置的默认值不为NULL时，那么需要更新该字段
                    if (columnParam.getFieldDefaultValue() != null
                            && !ColumnUtils.DEFAULT_VALUE.equals(columnParam.getFieldDefaultValue())) {
                        modifyFieldList.add(modifyTableParam);
                        continue;
                    }
                }
                else if (!sysColumn.getColumnDefault().equals(columnParam.getFieldDefaultValue())) {
                    if (MySqlFieldTypeEnum.BIT.toString().toLowerCase().equals(columnParam.getFieldType())
                            && !columnParam.isFieldDefaultValueNative()) {
                        if (("true".equals(columnParam.getFieldDefaultValue())
                                || "1".equals(columnParam.getFieldDefaultValue()))
                                && !"b'1'".equals(sysColumn.getColumnDefault())) {
                            // 两者不相等时，需要更新该字段
                            modifyFieldList.add(modifyTableParam);
                            continue;
                        }
                        if (("false".equals(columnParam.getFieldDefaultValue())
                                || "0".equals(columnParam.getFieldDefaultValue()))
                                && !"b'0'".equals(sysColumn.getColumnDefault())) {
                            // 两者不相等时，需要更新该字段
                            modifyFieldList.add(modifyTableParam);
                            continue;
                        }
                    }
                    else {
                        // 两者不相等时，需要更新该字段
                        modifyFieldList.add(modifyTableParam);
                        continue;
                    }
                }
                // 7.验证是否可以为null(主键不参与是否为null的更新)
                if (sysColumn.getIsNullable().equals("NO") && !columnParam.isFieldIsKey()) {
                    if (columnParam.isFieldIsNull()) {
                        // 一个是可以一个是不可用，所以需要更新该字段
                        modifyFieldList.add(modifyTableParam);
                        continue;
                    }
                }
                else if (sysColumn.getIsNullable().equals("YES") && !columnParam.isFieldIsKey()) {
                    if (!columnParam.isFieldIsNull()) {
                        // 一个是可以一个是不可用，所以需要更新该字段
                        modifyFieldList.add(modifyTableParam);
                        continue;
                    }
                }
                // 8.验证注释
                if (!sysColumn.getColumnComment().equals(columnParam.getFieldComment())) {
                    modifyFieldList.add(modifyTableParam);
                }
            }
        }
        return modifyFieldList;
    }

    /**
     * 将allFieldList转换为Map结构
     */
    private Map<String, MySqlEntityColumn> getAllFieldMap(List<Object> allFieldList) {
        // 将fieldList转成Map类型，字段名作为主键
        Map<String, MySqlEntityColumn> fieldMap = new HashMap<>();
        for (Object obj : allFieldList) {
            MySqlEntityColumn columnParam = (MySqlEntityColumn) obj;
            fieldMap.put(columnParam.getFieldName().toLowerCase(), columnParam);
        }
        return fieldMap;
    }

    /**
     * 根据数据库中表的结构和model中表的结构对比找出删除的字段
     * @param columnNames 数据库中的结构
     * @param allFieldList model中的所有字段
     */
    private List<Object> getRemoveFieldList(List<String> columnNames, List<Object> allFieldList) {
        List<String> toLowerCaseColumnNames = ClassTools.toLowerCase(columnNames);
        Map<String, MySqlEntityColumn> fieldMap = getAllFieldMap(allFieldList);
        // 用于存删除的字段
        List<Object> removeFieldList = new ArrayList<>();
        for (String fieldNm : toLowerCaseColumnNames) {
            // 判断该字段在新的model结构中是否存在
            if (fieldMap.get(fieldNm) == null) {
                // 不存在，做删除处理
                removeFieldList.add(fieldNm);
            }
        }
        return removeFieldList;
    }

    /**
     * 根据数据库中表的结构和model中表的结构对比找出新增的字段
     * @param allFieldList model中的所有字段
     * @param columnNames 数据库中的结构
     * @return 新增的字段
     */
    private List<MySqlEntityColumn> getAddFieldList(List<MySqlEntityColumn> allFieldList, List<String> columnNames) {
        List<String> toLowerCaseColumnNames = ClassTools.toLowerCase(columnNames);
        List<MySqlEntityColumn> addFieldList = new ArrayList<>();
        for (MySqlEntityColumn obj : allFieldList) {
            MySqlEntityColumn columnParam = obj;
            // 循环新的model中的字段，判断是否在数据库中已经存在
            if (!toLowerCaseColumnNames.contains(columnParam.getFieldName().toLowerCase())) {
                // 不存在，表示要在数据库中增加该字段
                addFieldList.add(obj);
            }
        }
        return addFieldList;
    }

    /**
     * 迭代出所有model的所有fields
     * @param clas 准备做为创建表依据的class
     * @return 表的全部字段
     */
    public List<MySqlEntityColumn> getAllFields(Class<?> clas) {
        List<MySqlEntityColumn> fieldList = new ArrayList<>();
        Field[] fields = clas.getDeclaredFields();

        // 判断是否有父类，如果有拉取父类的field，这里支持多层继承
        fields = this.recursionParents(clas, fields);

        // 遍历字段
        for (Field field : fields) {
            // 判断方法中是否有指定注解类型的注解
            if (ColumnUtils.hasColumn(field, clas)) {
                MySqlTypeAndLength mySqlTypeAndLength = getTypeAndLength(field, clas);
                MySqlEntityColumn entityColumn = new MySqlEntityColumn()
                        .setFieldName(ColumnUtils.getColumnName(field, clas))
                        .setOrder(ColumnUtils.getColumnOrder(field, clas))
                        .setFieldType(mySqlTypeAndLength.getType().toLowerCase())
                        .setFileTypeLength(mySqlTypeAndLength.getParamCount())
                        .setFieldIsNull(ColumnUtils.isNull(field, clas))
                        .setFieldIsKey(ColumnUtils.isKey(field, clas))
                        .setFieldIsAutoIncrement(ColumnUtils.isAutoIncrement(field, clas))
                        .setFieldDefaultValue(ColumnUtils.getDefaultValue(field, clas))
                        .setFieldDefaultValueNative(ColumnUtils.getDefaultValueNative(field, clas))
                        .setFieldComment(ColumnUtils.getComment(field, clas));
                // 长度需要配置
                if (mySqlTypeAndLength.getParamCount() == 1) {
                    entityColumn.setParamCount(mySqlTypeAndLength.getLength());
                }
                // 小数也需要配置
                else if (mySqlTypeAndLength.getParamCount() == 2) {
                    entityColumn.setParamCount(mySqlTypeAndLength.getLength())
                            .setFieldDecimalLength(mySqlTypeAndLength.getDecimalLength());
                }
                // 获取当前字段的@IgnoreUpdate注解
                IgnoreUpdate ignoreUpdate = field.getAnnotation(IgnoreUpdate.class);
                if (Objects.nonNull(ignoreUpdate)) {
                    entityColumn.setIgnoreUpdate(ignoreUpdate.value());
                }
                fieldList.add(entityColumn);
            }
        }
        // 进行排序
        fieldList.sort(Comparator.comparingInt(MySqlEntityColumn::getOrder));
        return new ArrayList<>(fieldList);
    }

    /**
     * 递归扫描父类的fields
     * @param clas 类
     * @param fields 属性
     */
    private Field[] recursionParents(Class<?> clas, Field[] fields) {
        if (clas.getSuperclass() != null) {
            Class<?> clsSup = clas.getSuperclass();
            List<Field> fieldList = new ArrayList<>(Arrays.asList(fields));
            // 获取当前class的所有fields的name列表
            List<String> fdNames = fieldList.stream().map(Field::getName).collect(Collectors.toList());
            for (Field pfd : clsSup.getDeclaredFields()) {
                // 避免重载属性
                if (fdNames.contains(pfd.getName())) {
                    continue;
                }
                fieldList.add(pfd);
            }
            fields = new Field[fieldList.size()];
            int i = 0;
            for (Object field : fieldList.toArray()) {
                fields[i] = (Field) field;
                i++;
            }
            fields = recursionParents(clsSup, fields);
        }
        return fields;
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

    /**
     * Mysql 类型和长度
     */
    public MySqlTypeAndLength getTypeAndLength(Field field, Class<?> clazz) {
        if (!ColumnUtils.hasColumn(field, clazz)) {
            throw new RuntimeException("字段名：" + field.getName() + "没有字段标识的注解，异常抛出！");
        }
        // 根据字段注解获取类型和长度配置
        MySqlFieldType mySqlFieldType = clazz.getAnnotation(MySqlFieldType.class);
        MySqlTypeAndLength typeAndLength;
        if (Objects.isNull(mySqlFieldType)){
            typeAndLength = MySql4JavaType.getTypeAndLength(field.getGenericType().toString());
        } else {
            typeAndLength = mySqlFieldType.value().getTypeAndLength();
        }

        if (ObjectUtil.isAllNotEmpty(typeAndLength)) {
            throw new RuntimeException("字段名：" + field.getName() + "不支持" + field.getGenericType()
                    + "类型转换到mysql类型，仅支持JavaToMysqlType类中的类型默认转换，异常抛出！");
        }

        // 处理字段注解是否有自定义的长度配置
        DbColumn column = ColumnUtils.getColumnAnno(field, clazz);
        if (Objects.isNull(column)) {
            typeAndLengthHandler(typeAndLength,255, 0);
        } else {
            typeAndLengthHandler(typeAndLength, column.length(),column.decimalLength() );
        }
        return typeAndLength;
    }

    /**
     * 构建 Mysql 类型和长度
     */
    private static void typeAndLengthHandler(MySqlTypeAndLength typeAndLength, int length, int decimalLength) {
        if (length != 255) {
            typeAndLength.setLength(length);
        }
        if (decimalLength != 0) {
            typeAndLength.setDecimalLength(decimalLength);
        }
    }
}
