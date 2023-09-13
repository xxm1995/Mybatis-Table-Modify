package cn.bootx.table.modify.utils;

import cn.bootx.table.modify.annotation.*;
import cn.bootx.table.modify.annotation.impl.DbColumnImpl;
import cn.bootx.table.modify.constants.TableCharset;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.text.NamingCase;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

/**
 * 行工具类
 *
 * @author xxm
 * @date 2023/1/16
 */
public class ColumnUtils {

    /** SQL 转义字符 */
    public static final String SQL_ESCAPE_CHARACTER = "`";

    /**
     * 获取表名称
     */
    public static String getTableName(Class<?> clazz) {
        DbTable table = clazz.getAnnotation(DbTable.class);

        DbEnableTimeSuffix enableTimeSuffix = clazz.getAnnotation(DbEnableTimeSuffix.class);
        if (!hasHandler(clazz)) {
            return null;
        }
        String finalTableName = "";
        if (table != null && StrUtil.isNotBlank(table.name())) {
            finalTableName = table.name();
        }
        if (table != null && StrUtil.isNotBlank(table.value())) {
            finalTableName = table.value();
        }

        try {
            TableName tableNamePlus = clazz.getAnnotation(TableName.class);
            if (tableNamePlus != null && StrUtil.isNotBlank(tableNamePlus.value())) {
                finalTableName = tableNamePlus.value();
            }
        } catch (NoClassDefFoundError ignored) {}
        if (StrUtil.isBlank(finalTableName)) {
            // 都为空时采用类名按照驼峰格式转会为表名
            finalTableName = getBuildLowerName(clazz.getSimpleName());
        }
        if (null != enableTimeSuffix && enableTimeSuffix.value()) {
            finalTableName = appendTimeSuffix(finalTableName, enableTimeSuffix.pattern());
        }
        return finalTableName;
    }

    /**
     * 获取表备注
     */
    public static String getTableComment(Class<?> clazz) {
        DbTable table = clazz.getAnnotation(DbTable.class);
        DbComment dbComment = clazz.getAnnotation(DbComment.class);
        if (Objects.nonNull(table) && StrUtil.isNotBlank(table.comment())) {
            return table.comment();
        }
        if (Objects.nonNull(dbComment)) {
            return dbComment.value();
        }
        return null;
    }

    /**
     * 获取表字符集
     */
    public static TableCharset getTableCharset(Class<?> clazz) {
        DbTable table = clazz.getAnnotation(DbTable.class);
        if (!hasHandler(clazz)) {
            return null;
        }
        if (table != null && table.charset() != TableCharset.DEFAULT) {
            return table.charset();
        }
        return null;
    }


    /**
     * 获取行名称
     */
    public static String getColumnName(Field field, Class<?> clazz) {
        DbColumn column = getDbColumnAnno(field, clazz);
        if (!hasColumn(field, clazz)) {
            return null;
        }
        if (column != null && StrUtil.isNotBlank(column.name())) {
            return column.name().toLowerCase().replace(SQL_ESCAPE_CHARACTER, "");
        }
        if (column != null && StrUtil.isNotBlank(column.value())) {
            return column.value().toLowerCase().replace(SQL_ESCAPE_CHARACTER, "");
        }
        try {
            TableField tableField = field.getAnnotation(TableField.class);
            if (tableField != null && StrUtil.isNotBlank(tableField.value()) && tableField.exist()) {
                return tableField.value().toLowerCase().replace(SQL_ESCAPE_CHARACTER, "");
            }
        } catch (NoClassDefFoundError ignored) {}
        try {
            TableId tableId = field.getAnnotation(TableId.class);
            if (tableId != null && StrUtil.isNotBlank(tableId.value())) {
                return tableId.value().replace(SQL_ESCAPE_CHARACTER, "");
            }
        } catch (NoClassDefFoundError ignored) {}
        return getBuildLowerName(field.getName()).replace(SQL_ESCAPE_CHARACTER, "");
    }

    /**
     * 获取数据库字段的排序
     */
    public static int getColumnOrder(Field field, Class<?> clazz) {
        DbColumn column = getDbColumnAnno(field, clazz);
        DbOrder order = field.getAnnotation(DbOrder.class);
        if (!hasColumn(field, clazz)) {
            return 0;
        }
        if (Objects.nonNull(order)&&order.value()!=0){
            return order.value();
        }
        if (Objects.nonNull(column)&&column.order()!=0){
            return column.order();
        }
        return 0;
    }

    /**
     * 获取构建小写表名称
     */
    private static String getBuildLowerName(String name) {
        return NamingCase.toUnderlineCase(name);
    }

    /**
     * 是否是主键
     */
    public static boolean isKey(Field field, Class<?> clazz) {
        if (!hasColumn(field, clazz)) {
            return false;
        }

        DbColumn column = getDbColumnAnno(field, clazz);
        DbKey isKey = field.getAnnotation(DbKey.class);

        if (Objects.nonNull(column) && column.isKey()) {
            return true;
        } if (Objects.nonNull(isKey)) {
            return true;
        }
        try {
            TableId tableId = field.getAnnotation(TableId.class);
            if (Objects.nonNull(tableId)){
                return true;
            }
        } catch (NoClassDefFoundError ignored) {}
        return false;
    }

    /**
     * 是否是自增， 主键才可以自增
     */
    public static boolean isAutoIncrement(Field field, Class<?> clazz) {
        DbColumn column = getDbColumnAnno(field, clazz);
        DbAutoIncrement autoIncrement = field.getAnnotation(DbAutoIncrement.class);
        if (!isKey(field, clazz)) {
            return false;
        }
        if (Objects.nonNull(column) && column.isAutoIncrement()){
            return true;
        }
        if (Objects.nonNull(autoIncrement)){
            return true;
        }
        return false;
    }

    /**
     * 是否可以为空
     */
    public static boolean isNull(Field field, Class<?> clazz) {
        DbColumn column = getDbColumnAnno(field, clazz);
        DbNotNull notNull = field.getAnnotation(DbNotNull.class);
        if (!hasColumn(field, clazz)) {
            return true;
        }
        boolean isKey = isKey(field, clazz);
        // 主键默认为非空
        if (isKey) {
            return false;
        }
        if (Objects.nonNull(column) && !column.isNull()) {
            return false;
        }
        if (Objects.nonNull(notNull)){
            return false;
        }
        return true;
    }

    /**
     * 该字段是否需要删除
     */
    public static boolean isDelete(Field field, Class<?> clazz){
        DbColumn dbColumn = getDbColumnAnno(field,clazz);
        DbDelete dbDelete = field.getAnnotation(DbDelete.class);
        if (Objects.nonNull(dbColumn)&&dbColumn.delete()){
            return true;
        } else if(Objects.nonNull(dbDelete)){
            return true;
        } else {
            return false;
        }
    }
    /**
     * 获取字段的备注
     */
    public static String getComment(Field field, Class<?> clazz) {
        DbColumn column = getDbColumnAnno(field, clazz);
        DbComment dbComment = field.getAnnotation(DbComment.class);
        if (Objects.nonNull(column) && StrUtil.isNotBlank(column.comment())) {
            return column.comment();
        }
        if (Objects.nonNull(dbComment)) {
            return dbComment.value();
        }
        return null;
    }

    /**
     * 获取默认值
     */
    public static String getDefaultValue(Field field, Class<?> clazz) {
        DbColumn column = getDbColumnAnno(field, clazz);
        if (!hasColumn(field, clazz)) {
            return null;
        }
        if (Objects.nonNull(column) && StrUtil.isNotBlank(column.defaultValue())) {
            return column.defaultValue();
        }
        return null;
    }


    /**
     * 是否需要对实体类进行处理
     * 没有打注解不需要创建表 或者配置了忽略建表的注解
     */
    public static boolean hasHandler(Class<?> clazz) {
        if (Objects.nonNull(clazz.getAnnotation(DbIgnore.class))){
            return false;
        }
        DbTable dbTable = clazz.getAnnotation(DbTable.class);
        return Objects.nonNull(dbTable) && !dbTable.ignore();
    }

    /**
     * 本行是否需要进行处理, 兼容mybatis plus注解
     * simple模式: 开启后Field不写注解@Column也可以采用默认的驼峰转换法创建字段
     * append模式: 开启追加模式,字段必须要添加@DbColumn注解才会触发更新或新增
     */
    public static boolean hasColumn(Field field, Class<?> clazz) {
        // 是否开启simple模式
        boolean isSimple = isSimple(clazz);
        // 是否开启了追加模式
        boolean isAppend = isAppend(clazz);
        // 当前属性名在排除建表的字段内, 不参与建表的字段
        String[] excludeFields = excludeFields(clazz);
        if (Arrays.asList(excludeFields).contains(field.getName())) {
            return false;
        }
        // 排除静态字段
        if (Modifier.isStatic(field.getModifiers())) {
            return false;
        }
        DbColumn column = field.getAnnotation(DbColumn.class);
        DbIgnore ignore = field.getAnnotation(DbIgnore.class);

        // 判断是否忽略该字段
        if (Objects.nonNull(ignore)||(Objects.nonNull(column) && column.ignore())) {
            return false;
        }
        // 查看是否满足追加模式, 满足追加但没添加 @DbColumn 注解则不进行处理
        if (Objects.isNull(column) && isAppend){
            return false;
        }
        // 处理MP相关注解
        try {
            TableField tableField = field.getAnnotation(TableField.class);
            TableId tableId = field.getAnnotation(TableId.class);
            // 如果有MP相关注解, 进行处理, 没有的话返回simple模式
            if (column == null && (tableField == null || !tableField.exist()) && tableId == null) {
                return isSimple;
            }
            return true;
        } catch (NoClassDefFoundError ignored) {}
        // 不存在 @DbColumn 直接返回simple模式
        if (Objects.isNull(column) ) {
            return isSimple;
        }
        return true;
    }


    /**
     * 获取列注解
     */
    public static DbColumn getDbColumnAnno(Field field, Class<?> clazz) {
        // 不参与建表的字段
        String[] excludeFields = excludeFields(clazz);
        if (Arrays.asList(excludeFields).contains(field.getName())) {
            return null;
        }
        DbColumn column = field.getAnnotation(DbColumn.class);
        if (column != null) {
            return column;
        }
        // 开启了simple模式
        if (isSimple(clazz)) {
            return new DbColumnImpl();
        }
        return null;
    }

    /**
     * 排除字段
     */
    private static String[] excludeFields(Class<?> clazz) {
        String[] excludeFields = {};
        DbTable tableName = clazz.getAnnotation(DbTable.class);
        if (tableName != null) {
            excludeFields = tableName.excludeFields();
        }
        return excludeFields;
    }

    /**
     * 是否是简单模式
     */
    private static boolean isSimple(Class<?> clazz) {
        boolean isSimple = false;
        DbTable tableName = clazz.getAnnotation(DbTable.class);
        if (tableName != null) {
            isSimple = tableName.isSimple();
        }
        return isSimple;
    }
    /**
     * 是否是追加模式
     */
    public static boolean isAppend(Class<?> clazz) {
        boolean isAppend = false;
        DbTable dbTable = clazz.getAnnotation(DbTable.class);
        if (Objects.nonNull(dbTable)) {
            isAppend = dbTable.isAppend();
        }
        return isAppend;
    }

    /**
     * 添加时间后缀
     * @param tableName 表名
     * @param pattern 时间格式
     * @return
     */
    public static String appendTimeSuffix(String tableName, String pattern) {
        String suffix;
        try {
            suffix = DateUtil.format(new Date(), pattern);
        }
        catch (Exception e) {
            throw new RuntimeException("无法转换时间格式" + pattern);
        }
        return tableName + "_" + suffix;
    }

}
