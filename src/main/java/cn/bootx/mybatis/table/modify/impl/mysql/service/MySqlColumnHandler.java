package cn.bootx.mybatis.table.modify.impl.mysql.service;

import cn.bootx.mybatis.table.modify.annotation.DbColumn;
import cn.bootx.mybatis.table.modify.annotation.IgnoreUpdate;
import cn.bootx.mybatis.table.modify.impl.mysql.annotation.MySqlFieldType;
import cn.bootx.mybatis.table.modify.impl.mysql.constants.MySql4JavaType;
import cn.bootx.mybatis.table.modify.impl.mysql.constants.MySqlFieldTypeEnum;
import cn.bootx.mybatis.table.modify.impl.mysql.entity.*;
import cn.bootx.mybatis.table.modify.impl.mysql.mapper.MySqlTableModifyMapper;
import cn.bootx.mybatis.table.modify.impl.mysql.util.MySqlInfoUtil;
import cn.bootx.mybatis.table.modify.utils.ColumnUtils;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Component;

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
@Component
@RequiredArgsConstructor
public class MySqlColumnHandler {
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
            entityColumns = MySqlInfoUtil.getEntityColumns(clas);
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
            entityColumns = MySqlInfoUtil.getEntityColumns(clas);
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





//        Map<String, MySqlEntityColumn> fieldMap = getAllFieldMap(entityColumns);
//        List<Object> modifyFieldList = new ArrayList<>();
//        for (MysqlTableColumnInfo sysColumn : tableColumnList) {
        // 数据库中有该字段时，验证是否有更新
//            MySqlEntityColumn columnParam = fieldMap.get(sysColumn.getColumnName().toLowerCase());
//                // 该复制操作时为了解决multiple primary key defined的同时又不会drop primary key
//                MySqlEntityColumn modifyTableParam = columnParam.clone();
//                // 1.验证主键
//                // 原本不是主键，现在变成了主键，那么要去做更新
//                if (!"PRI".equals(sysColumn.getColumnKey()) && columnParam.isKey()) {
//                    modifyFieldList.add(modifyTableParam);
//                    continue;
//                }
//                // 原本是主键，现在依然主键，坚决不能在alter语句后加primary key，否则会报multiple primary
//                // key defined
//                if ("PRI".equals(sysColumn.getColumnKey()) && columnParam.isKey()) {
//                    modifyTableParam.setKey(false);
//                }
//        }
//    }
    }


    /**
     * 判断字段配置是否一致
     * @param mysqlTableColumn 表行信息
     * @param entityColumn 实体配行信息
     * @return 是否需要变动 true 需要更新
     */
    private boolean compareColumn(MysqlTableColumnInfo mysqlTableColumn, MySqlEntityColumn entityColumn){

        // 验证类型
        if (!mysqlTableColumn.getDataType().equalsIgnoreCase(entityColumn.getFieldType())) {
            return false;
        }
        // 验证长度和小数点位数
        String typeAndLength = entityColumn.getFieldType().toLowerCase();
        if (entityColumn.getParamCount() == 1) {
            // 拼接出类型加长度，比如varchar(1)
            typeAndLength = typeAndLength + "(" + entityColumn.getParamCount() + ")";
        }
        else if (entityColumn.getParamCount() == 2) {
            // 拼接出类型加长度，比如varchar(1)
            typeAndLength = typeAndLength + "(" + entityColumn.getParamCount() + ","
                    + entityColumn.getDecimalLength() + ")";
        }

        // 判断类型+长度是否相同
        if (!mysqlTableColumn.getColumnType().toLowerCase().equals(typeAndLength)) {
            return false;
        }

        // 验证自增 数据库自增, 实体类不自增
        if ("auto_increment".equals(mysqlTableColumn.getExtra()) && !entityColumn.isAutoIncrement()) {
            return false;
        }
        // 验证自增 数据库不自增, 实体类自增
        if (!"auto_increment".equals(mysqlTableColumn.getExtra()) && entityColumn.isAutoIncrement()) {
            return false;
        }
        // 验证默认值控制 TODO 需优化
        if ( StrUtil.isBlank(mysqlTableColumn.getColumnDefault()) || mysqlTableColumn.getColumnDefault().equals("")) {
            // 数据库默认值是null，model中注解设置的默认值不为NULL时，那么需要更新该字段
            return entityColumn.getDefaultValue() == null
                    || ColumnUtils.DEFAULT_VALUE.equals(entityColumn.getDefaultValue());
        }
        // 验证默认值 TODO 需优化
        if (MySqlFieldTypeEnum.BIT.toString().toLowerCase().equals(entityColumn.getFieldType())
                && !entityColumn.isDefaultValueNative()) {
            if (("true".equals(entityColumn.getDefaultValue())
                    || "1".equals(entityColumn.getDefaultValue()))
                    && !"b'1'".equals(mysqlTableColumn.getColumnDefault())) {
                // 两者不相等时，需要更新该字段
                return false;
            }
            if (("false".equals(entityColumn.getDefaultValue())
                    || "0".equals(entityColumn.getDefaultValue()))
                    && !"b'0'".equals(mysqlTableColumn.getColumnDefault())) {
                // 两者不相等时，需要更新该字段
                return false;
            }
        }
        else {
            return false;
        }
        // 验证是否可以为null 数据库自增, 实体类不自增
        if (mysqlTableColumn.getIsNullable().equals("NO") && !entityColumn.isKey()) {
            if (entityColumn.isFieldIsNull()) {
                return false;
            }
        }
        // 验证是否可以为null
        else if (mysqlTableColumn.getIsNullable().equals("YES") && !entityColumn.isKey()) {
            if (!entityColumn.isFieldIsNull()) {
                return false;
            }
        }
        // 8.验证注释
        if (!Objects.equals(mysqlTableColumn.getColumnComment(),entityColumn.getComment())) {
            return false;
        }

        return true;

        // 该复制操作时为了解决multiple primary key defined的同时又不会drop primary key
//            MySqlEntityColumn modifyTableParam = columnParam.clone();
        // 1.验证主键
        // 原本不是主键，现在变成了主键，那么要去做更新
//            if (!"PRI".equals(sysColumn.getColumnKey()) && columnParam.isKey()) {
//                modifyFieldList.add(modifyTableParam);
//                continue;
//            }
    }

}
