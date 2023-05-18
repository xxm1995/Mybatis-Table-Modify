package cn.bootx.mybatis.table.modify.mybatis.mysq.service;

import cn.bootx.mybatis.table.modify.impl.mysql.entity.*;
import cn.bootx.mybatis.table.modify.mybatis.mysq.mapper.MySqlTableModifyMapper;
import cn.bootx.mybatis.table.modify.mybatis.mysq.util.MySqlInfoUtil;
import cn.bootx.mybatis.table.modify.mybatis.mysq.entity.MySqlEntityColumn;
import cn.bootx.mybatis.table.modify.mybatis.mysq.entity.MySqlModifyMap;
import cn.bootx.mybatis.table.modify.mybatis.mysq.entity.MysqlTableColumn;
import cn.bootx.mybatis.table.modify.utils.ColumnUtils;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Component;

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
public class MySqlColumnInfoService {
    private final MySqlTableModifyMapper mysqlTableModifyMapper;

    /**
     * 获取建表的字段
     */
    public void getCreateColumn(Class<?> clas,  MySqlModifyMap baseTableMap){

        // 获取entity的tableName
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
        // 获取entity的tableName
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
        List<MysqlTableColumn> tableColumns = mysqlTableModifyMapper.findColumnByTableName(tableName);

        // 找出增加的字段
        List<MySqlEntityColumn> addColumns = getAddColumns(entityColumns, tableColumns);
        if (addColumns.size() != 0) {
            baseTableMap.getAddColumns().put(tableName, addColumns);
        }
        // 找出删除的字段
        List<String> dropColumns = getDropColumns(entityColumns, tableColumns);
        if (dropColumns.size() != 0) {
            baseTableMap.getDropColumns().put(tableName, dropColumns);
        }
        // 找出更新的字段
        List<MySqlEntityColumn> updateColumns = getUpdateColumns(entityColumns,tableColumns);
        if (updateColumns.size() != 0) {
            baseTableMap.getUpdateColumns().put(tableName, updateColumns);
        }
    }

    /**
     * 根据数据库中表的结构和entity中表的结构对比找出新增的字段
     * @param entityColumns entity中的所有字段
     * @param tableColumns 数据库中的结构
     * @return 新增的字段
     */
    private List<MySqlEntityColumn> getAddColumns(List<MySqlEntityColumn> entityColumns, List<MysqlTableColumn> tableColumns) {

        List<String> tableColumnNames = tableColumns.stream()
                .map(MysqlTableColumn::getColumnName)
                .map(String::toLowerCase)
                .collect(Collectors.toList());
        // 数据库中不存在, 进行添加
        return entityColumns.stream()
                .filter(column->!tableColumnNames.contains(column.getName().toLowerCase()))
                .collect(Collectors.toList());
    }

    /**
     * 根据数据库中表的结构和entity中表的结构对比找出删除的字段
     * @param entityColumns entity中的所有字段
     * @param tableColumns 数据库中的结构
     */
    private List<String> getDropColumns(List<MySqlEntityColumn> entityColumns, List<MysqlTableColumn> tableColumns) {
        List<String> entityColumnNames = entityColumns.stream()
                .map(MySqlEntityColumn::getName)
                .map(String::toLowerCase)
                .collect(Collectors.toList());
        // 实体类上不存在, 进行删除
        return tableColumns.stream()
                .map(MysqlTableColumn::getColumnName)
                .filter(columnName -> !entityColumnNames.contains(columnName.toLowerCase()))
                .collect(Collectors.toList());
    }

    /**
     * 根据数据库中表的结构和entity中表的结构对比找出修改类型默认值等属性的字段
     * @return 需要修改的字段
     */
    private List<MySqlEntityColumn> getUpdateColumns(List<MySqlEntityColumn> entityColumns, List<MysqlTableColumn> tableColumnList) {
        // 现有的数据库字段配置
        List<String> tableIndexNames = tableColumnList.stream()
                .map(MysqlTableColumn::getColumnName)
                .map(String::toLowerCase)
                .distinct()
                .collect(Collectors.toList());

        // 现有的数据库字段配置 MAP
        val tableColumnMap = tableColumnList.stream()
                .collect(Collectors.toMap(column -> column.getColumnName().toLowerCase(), Function.identity()));

        // 把两者都有的字段筛选出来进行对比, 筛选出来有不同的字段
        return entityColumns.stream()
                .filter(column->tableIndexNames.contains(column.getName().toLowerCase()))
                .filter(entityColumn->!compareColumn(tableColumnMap.get(entityColumn.getName().toLowerCase()),entityColumn))
                .collect(Collectors.toList());

    }


    /**
     * 判断字段配置是否一致
     * @param tableColumn 表行信息
     * @param entityColumn 实体配行信息
     * @return 是否需要变动 true 需要更新
     */
    private boolean compareColumn(MysqlTableColumn tableColumn, MySqlEntityColumn entityColumn){

        // 验证类型
        if (!tableColumn.getDataType().equalsIgnoreCase(entityColumn.getFieldType())) {
            return false;
        }
        // 验证长度和小数点位数
        String typeAndLength = entityColumn.getFieldType().toLowerCase();
        if (entityColumn.getParamCount() == 1) {
            // 拼接出类型加长度，比如varchar(1)
            typeAndLength = typeAndLength + "(" + entityColumn.getLength() + ")";
        }
        else if (entityColumn.getParamCount() == 2) {
            // 拼接出类型加长度，比如varchar(1)
            typeAndLength = typeAndLength + "(" + entityColumn.getLength() + ","
                    + entityColumn.getDecimalLength() + ")";
        }

        // 判断类型+长度是否相同
        if (!tableColumn.getColumnType().toLowerCase().equals(typeAndLength)) {
            return false;
        }

        // 验证自增 数据库自增, 实体类不自增
        if ("auto_increment".equals(tableColumn.getExtra()) && !entityColumn.isAutoIncrement()) {
            return false;
        }
        // 验证自增 数据库不自增, 实体类自增
        if (!"auto_increment".equals(tableColumn.getExtra()) && entityColumn.isAutoIncrement()) {
            return false;
        }
        // 验证默认值控制是否为空
        if (StrUtil.isBlank(entityColumn.getDefaultValue()) != StrUtil.isBlank(tableColumn.getColumnDefault())) {
            return false;
        }
        // entityColumn默认值需要去除两侧单引号,然后比对默认值是否一致
        if (StrUtil.isNotBlank(entityColumn.getDefaultValue())){
            String entityDefaultValue = StrUtil.trim(entityColumn.getDefaultValue(), 0, character -> character.toString().equals("'"));
            if (!Objects.equals(entityDefaultValue,tableColumn.getColumnDefault())){
                return false;
            }
        }
        // 验证是否可以为null
        if (tableColumn.getIsNullable().equals("NO") && !entityColumn.isKey()) {
            if (entityColumn.isFieldIsNull()) {
                return false;
            }
        }
        // 验证是否可以为null
        else if (tableColumn.getIsNullable().equals("YES") && !entityColumn.isKey()) {
            if (!entityColumn.isFieldIsNull()) {
                return false;
            }
        }
        // 8.验证注释
        if (!Objects.equals(tableColumn.getColumnComment(),entityColumn.getComment())) {
            return false;
        }

        return true;
    }

}
