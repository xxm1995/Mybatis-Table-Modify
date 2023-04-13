package cn.bootx.mybatis.table.modify.impl.mysql.service;

import cn.bootx.mybatis.table.modify.annotation.DbColumn;
import cn.bootx.mybatis.table.modify.annotation.IgnoreUpdate;
import cn.bootx.mybatis.table.modify.impl.mysql.annotation.MySqlFieldType;
import cn.bootx.mybatis.table.modify.impl.mysql.constants.MySql4JavaType;
import cn.bootx.mybatis.table.modify.impl.mysql.constants.MySqlFieldTypeEnum;
import cn.bootx.mybatis.table.modify.impl.mysql.entity.*;
import cn.bootx.mybatis.table.modify.impl.mysql.mapper.MySqlTableModifyMapper;
import cn.bootx.mybatis.table.modify.utils.ColumnUtils;
import cn.hutool.core.util.ObjectUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * MySQL 字段处理
 * @author xxm
 * @date 2023/4/13
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MySqlColumnHandlerService {
    private final MySqlTableModifyMapper mysqlTableModifyMapper;

    /**
     * 获取建表的字段
     */
    public void getCreateColumn(Class<?> clas,  MySqlModifyMap baseTableMap){

        // 获取model的tableName
        String tableName = ColumnUtils.getTableName(clas);

        // 用于实体类配置的全部字段
        List<MySqlEntityColumn> entityColumns;
        try {
            entityColumns = getEntityColumns(clas);
            baseTableMap.getAddColumns().put(tableName,entityColumns);
            if (entityColumns.size() == 0) {
                log.warn("扫描发现" + clas.getName() + "没有建表字段请检查！");
            }

        } catch (Exception e) {
            log.error("表：{}，初始化字段结构失败！", tableName);
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 获取要更新的字段
     */
    public void getModifyColumn(Class<?> clas,  MySqlModifyMap baseTableMap) {
        // 获取model的tableName
        String tableName = ColumnUtils.getTableName(clas);

        // 用于实体类配置的全部字段
        List<MySqlEntityColumn> entityColumns;
        try {
            entityColumns = getEntityColumns(clas);
            if (entityColumns.size() == 0) {
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

        // 找出增加的字段
        List<MySqlEntityColumn> addColumns = getAddColumns(entityColumns, tableColumns);
        if (addColumns.size() != 0) {
            baseTableMap.getAddColumns().put(tableName, addColumns);
        }
        // 找出删除的字段
        List<String> removeColumns = getRemoveColumns(entityColumns, tableColumns);
        if (removeColumns.size() != 0) {
            baseTableMap.getRemoveColumns().put(tableName, removeColumns);
        }
        // 找出更新的字段
        List<MySqlEntityColumn> updateColumns = getUpdateColumns(entityColumns,tableColumns);
        if (updateColumns.size() != 0) {
            baseTableMap.getUpdateColumns().put(tableName, updateColumns);
        }
    }

    /**
     * 根据数据库中表的结构和model中表的结构对比找出新增的字段
     * @param entityColumns model中的所有字段
     * @param tableColumns 数据库中的结构
     * @return 新增的字段
     */
    private List<MySqlEntityColumn> getAddColumns(List<MySqlEntityColumn> entityColumns, List<MysqlTableColumnInfo> tableColumns) {

        List<String> tableColumnNames = tableColumns.stream()
                .map(MysqlTableColumnInfo::getColumnName)
                .map(String::toLowerCase)
                .collect(Collectors.toList());
        // 数据库中不存在, 进行添加
        return entityColumns.stream()
                .filter(column->!tableColumnNames.contains(column.getComment().toLowerCase()))
                .collect(Collectors.toList());
    }

    /**
     * 根据数据库中表的结构和model中表的结构对比找出删除的字段
     * @param entityColumns model中的所有字段
     * @param tableColumns 数据库中的结构
     */
    private List<String> getRemoveColumns(List<MySqlEntityColumn> entityColumns, List<MysqlTableColumnInfo> tableColumns) {
        List<String> entityColumnNames = entityColumns.stream()
                .map(MySqlEntityColumn::getColumnName)
                .map(String::toLowerCase)
                .collect(Collectors.toList());
        // 实体类上不存在, 进行删除
        return tableColumns.stream()
                .map(MysqlTableColumnInfo::getColumnName)
                .filter(columnName -> !entityColumnNames.contains(columnName.toLowerCase()))
                .collect(Collectors.toList());
    }

    /**
     * 根据数据库中表的结构和model中表的结构对比找出修改类型默认值等属性的字段
     * @return 需要修改的字段
     */
    private List<MySqlEntityColumn> getUpdateColumns(List<MySqlEntityColumn> entityColumns, List<MysqlTableColumnInfo> tableColumnList) {
        // 现有的数据库索引配置
        List<String> tableIndexNames = tableColumnList.stream()
                .map(MysqlTableColumnInfo::getColumnComment)
                .map(String::toLowerCase)
                .distinct()
                .collect(Collectors.toList());

        // 现有的数据库索引配置 MAP
        val tableColumnMap = tableColumnList.stream()
                .collect(Collectors.toMap(column -> column.getColumnName().toLowerCase(), Function.identity()));

        // 把两者都有的索引筛选出来进行对比
        return entityColumns.stream()
                .filter(column->tableIndexNames.contains(column.getColumnName().toLowerCase()))
                .filter(entityColumn->compareColumn(tableColumnMap.get(entityColumn.getColumnName().toLowerCase()),entityColumn))
                .collect(Collectors.toList());





        Map<String, MySqlEntityColumn> fieldMap = getAllFieldMap(entityColumns);
        List<Object> modifyFieldList = new ArrayList<>();
        for (MysqlTableColumnInfo sysColumn : tableColumnList) {
            // 数据库中有该字段时，验证是否有更新
            MySqlEntityColumn columnParam = fieldMap.get(sysColumn.getColumnName().toLowerCase());
            if (columnParam != null && !columnParam.isIgnoreUpdate()) {
                // 该复制操作时为了解决multiple primary key defined的同时又不会drop primary key
                MySqlEntityColumn modifyTableParam = columnParam.clone();
                // 1.验证主键
                // 原本不是主键，现在变成了主键，那么要去做更新
                if (!"PRI".equals(sysColumn.getColumnKey()) && columnParam.isKey()) {
                    modifyFieldList.add(modifyTableParam);
                    continue;
                }
                // 原本是主键，现在依然主键，坚决不能在alter语句后加primary key，否则会报multiple primary
                // key defined
                if ("PRI".equals(sysColumn.getColumnKey()) && columnParam.isKey()) {
                    modifyTableParam.setKey(false);
                }
                // 2.验证类型
                if (!sysColumn.getDataType().equalsIgnoreCase(columnParam.getFieldType())) {
                    modifyFieldList.add(modifyTableParam);
                    continue;
                }
                // 3.验证长度个小数点位数
                String typeAndLength = columnParam.getFieldType().toLowerCase();
                if (columnParam.getParamCount() == 1) {
                    // 拼接出类型加长度，比如varchar(1)
                    typeAndLength = typeAndLength + "(" + columnParam.getParamCount() + ")";
                }
                else if (columnParam.getParamCount() == 2) {
                    // 拼接出类型加长度，比如varchar(1)
                    typeAndLength = typeAndLength + "(" + columnParam.getParamCount() + ","
                            + columnParam.getDecimalLength() + ")";
                }

                // 判断类型+长度是否相同
                if (!sysColumn.getColumnType().toLowerCase().equals(typeAndLength)) {
                    modifyFieldList.add(modifyTableParam);
                    continue;
                }
                // 5.验证自增
                if ("auto_increment".equals(sysColumn.getExtra()) && !columnParam.isAutoIncrement()) {
                    modifyFieldList.add(modifyTableParam);
                    continue;
                }
                if (!"auto_increment".equals(sysColumn.getExtra()) && columnParam.isAutoIncrement()) {
                    modifyFieldList.add(modifyTableParam);
                    continue;
                }
                // 6.验证默认值
                if (sysColumn.getColumnDefault() == null || sysColumn.getColumnDefault().equals("")) {
                    // 数据库默认值是null，model中注解设置的默认值不为NULL时，那么需要更新该字段
                    if (columnParam.getDefaultValue() != null
                            && !ColumnUtils.DEFAULT_VALUE.equals(columnParam.getDefaultValue())) {
                        modifyFieldList.add(modifyTableParam);
                        continue;
                    }
                }
                else if (!sysColumn.getColumnDefault().equals(columnParam.getDefaultValue())) {
                    if (MySqlFieldTypeEnum.BIT.toString().toLowerCase().equals(columnParam.getFieldType())
                            && !columnParam.isDefaultValueNative()) {
                        if (("true".equals(columnParam.getDefaultValue())
                                || "1".equals(columnParam.getDefaultValue()))
                                && !"b'1'".equals(sysColumn.getColumnDefault())) {
                            // 两者不相等时，需要更新该字段
                            modifyFieldList.add(modifyTableParam);
                            continue;
                        }
                        if (("false".equals(columnParam.getDefaultValue())
                                || "0".equals(columnParam.getDefaultValue()))
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
                if (sysColumn.getIsNullable().equals("NO") && !columnParam.isKey()) {
                    if (columnParam.isFieldIsNull()) {
                        // 一个是可以一个是不可用，所以需要更新该字段
                        modifyFieldList.add(modifyTableParam);
                        continue;
                    }
                }
                else if (sysColumn.getIsNullable().equals("YES") && !columnParam.isKey()) {
                    if (!columnParam.isFieldIsNull()) {
                        // 一个是可以一个是不可用，所以需要更新该字段
                        modifyFieldList.add(modifyTableParam);
                        continue;
                    }
                }
                // 8.验证注释
                if (!sysColumn.getColumnComment().equals(columnParam.getComment())) {
                    modifyFieldList.add(modifyTableParam);
                }
            }
        }
        return modifyFieldList;
    }


    /**
     * 判断字段配置是否一致
     * @param mysqlTableColumnInfo
     * @param entityIndex
     * @return 是否需要变动 true 需要更新
     */
    private boolean compareColumn(MysqlTableColumnInfo mysqlTableColumnInfo, MySqlEntityColumn entityIndex){

        // 验证类型
        if (!mysqlTableColumnInfo.getDataType().equalsIgnoreCase(entityIndex.getFieldType())) {
            return false;
        }
        // 数据库中有该字段时，验证是否有更新
        MySqlEntityColumn columnParam = fieldMap.get(sysColumn.getColumnName().toLowerCase());
        if (columnParam != null && !columnParam.isIgnoreUpdate()) {
            // 该复制操作时为了解决multiple primary key defined的同时又不会drop primary key
            MySqlEntityColumn modifyTableParam = columnParam.clone();
            // 1.验证主键
            // 原本不是主键，现在变成了主键，那么要去做更新
            if (!"PRI".equals(sysColumn.getColumnKey()) && columnParam.isKey()) {
                modifyFieldList.add(modifyTableParam);
                continue;
            }
            // 原本是主键，现在依然主键，坚决不能在alter语句后加primary key，否则会报multiple primary
            // key defined
            if ("PRI".equals(sysColumn.getColumnKey()) && columnParam.isKey()) {
                modifyTableParam.setKey(false);
            }
            // 2.验证类型
            if (!sysColumn.getDataType().equalsIgnoreCase(columnParam.getFieldType())) {
                modifyFieldList.add(modifyTableParam);
                continue;
            }
            // 3.验证长度个小数点位数
            String typeAndLength = columnParam.getFieldType().toLowerCase();
            if (columnParam.getParamCount() == 1) {
                // 拼接出类型加长度，比如varchar(1)
                typeAndLength = typeAndLength + "(" + columnParam.getParamCount() + ")";
            }
            else if (columnParam.getParamCount() == 2) {
                // 拼接出类型加长度，比如varchar(1)
                typeAndLength = typeAndLength + "(" + columnParam.getParamCount() + ","
                        + columnParam.getDecimalLength() + ")";
            }

            // 判断类型+长度是否相同
            if (!sysColumn.getColumnType().toLowerCase().equals(typeAndLength)) {
                modifyFieldList.add(modifyTableParam);
                continue;
            }
            // 5.验证自增
            if ("auto_increment".equals(sysColumn.getExtra()) && !columnParam.isAutoIncrement()) {
                modifyFieldList.add(modifyTableParam);
                continue;
            }
            if (!"auto_increment".equals(sysColumn.getExtra()) && columnParam.isAutoIncrement()) {
                modifyFieldList.add(modifyTableParam);
                continue;
            }
            // 6.验证默认值
            if (sysColumn.getColumnDefault() == null || sysColumn.getColumnDefault().equals("")) {
                // 数据库默认值是null，model中注解设置的默认值不为NULL时，那么需要更新该字段
                if (columnParam.getDefaultValue() != null
                        && !ColumnUtils.DEFAULT_VALUE.equals(columnParam.getDefaultValue())) {
                    modifyFieldList.add(modifyTableParam);
                    continue;
                }
    }

    /**
     * 迭代出所有model的所有fields
     * @param clas 准备做为创建表依据的class
     * @return 表的全部字段
     */
    private List<MySqlEntityColumn> getEntityColumns(Class<?> clas) {
        List<MySqlEntityColumn> entityColumns = new ArrayList<>();
        Field[] fields = clas.getDeclaredFields();

        // 判断是否有父类，如果有拉取父类的field，这里支持多层继承
        fields = this.recursionParents(clas, fields);

        // 遍历字段
        for (Field field : fields) {
            // 判断方法中是否有指定注解类型的注解
            if (ColumnUtils.hasColumn(field, clas)) {
                MySqlTypeAndLength mySqlTypeAndLength = getTypeAndLength(field, clas);
                MySqlEntityColumn entityColumn = new MySqlEntityColumn()
                        .setColumnName(ColumnUtils.getColumnName(field, clas))
                        .setOrder(ColumnUtils.getColumnOrder(field, clas))
                        .setFieldType(mySqlTypeAndLength.getType().toLowerCase())
                        .setParamCount(mySqlTypeAndLength.getParamCount())
                        .setFieldIsNull(ColumnUtils.isNull(field, clas))
                        .setKey(ColumnUtils.isKey(field, clas))
                        .setAutoIncrement(ColumnUtils.isAutoIncrement(field, clas))
                        .setDefaultValue(ColumnUtils.getDefaultValue(field, clas))
                        .setDefaultValueNative(ColumnUtils.getDefaultValueNative(field, clas))
                        .setComment(ColumnUtils.getComment(field, clas));
                // 长度需要配置
                if (mySqlTypeAndLength.getParamCount() == 1) {
                    entityColumn.setParamCount(mySqlTypeAndLength.getLength());
                }
                // 小数也需要配置
                else if (mySqlTypeAndLength.getParamCount() == 2) {
                    entityColumn.setParamCount(mySqlTypeAndLength.getLength())
                            .setDecimalLength(mySqlTypeAndLength.getDecimalLength());
                }
                // 获取当前字段的@IgnoreUpdate注解
                IgnoreUpdate ignoreUpdate = field.getAnnotation(IgnoreUpdate.class);
                if (Objects.nonNull(ignoreUpdate)) {
                    entityColumn.setIgnoreUpdate(ignoreUpdate.value());
                }
                entityColumns.add(entityColumn);
            }
        }
        // 进行排序
        entityColumns.sort(Comparator.comparingInt(MySqlEntityColumn::getOrder));
        return new ArrayList<>(entityColumns);
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
