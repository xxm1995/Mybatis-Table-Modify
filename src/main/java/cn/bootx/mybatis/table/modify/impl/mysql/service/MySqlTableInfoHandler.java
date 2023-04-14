package cn.bootx.mybatis.table.modify.impl.mysql.service;

import cn.bootx.mybatis.table.modify.constants.TableCharset;
import cn.bootx.mybatis.table.modify.impl.mysql.annotation.MySqlEngine;
import cn.bootx.mybatis.table.modify.impl.mysql.constants.MySqlEngineEnum;
import cn.bootx.mybatis.table.modify.impl.mysql.entity.MySqlEntityColumn;
import cn.bootx.mybatis.table.modify.impl.mysql.entity.MySqlEntityTableInfo;
import cn.bootx.mybatis.table.modify.impl.mysql.entity.MySqlModifyMap;
import cn.bootx.mybatis.table.modify.impl.mysql.entity.MysqlTableColumnInfo;
import cn.bootx.mybatis.table.modify.impl.mysql.mapper.MySqlTableModifyMapper;
import cn.bootx.mybatis.table.modify.impl.mysql.util.MySqlInfoUtil;
import cn.bootx.mybatis.table.modify.utils.ColumnUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * MySQL表信息变动
 * @author xxm
 * @date 2023/4/14
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MySqlTableInfoHandler {

    private final MySqlTableModifyMapper mysqlTableModifyMapper;


    /**
     * 创建表
     */
    public void getCreateTable(Class<?> clas, MySqlModifyMap baseTableMap){
        String tableName = ColumnUtils.getTableName(clas);

        // 获取表注释
        String comment = ColumnUtils.getTableComment(clas);

        // 获取表字符集
        TableCharset charset = ColumnUtils.getTableCharset(clas);

        // 获取表引擎
        MySqlEngineEnum engine = getTableEngine(clas);

        // 获取主键
        List<String> keys = getTableKeys(clas);

        MySqlEntityTableInfo entityTable = new MySqlEntityTableInfo()
                .setComment(comment)
                .setCharset(tableName)
                .setEngine(engine)
                .setKeys(keys);
        baseTableMap.getCreateTables().put(tableName,entityTable);
    }

    /**
     * 更新表信息
     */
    public void getModifyTable(Class<?> clas,MySqlModifyMap baseTableMap){

    }

    /**
     * 获取表引擎类型
     */
    private MySqlEngineEnum getTableEngine(Class<?> clazz) {
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
     * 返回需要删除主键的字段
     * @param tableColumnList 表结构
     * @param allFieldList model中的所有字段
     * @return 需要删除主键的字段
     */
    private List<Object> getDropKey(List<MysqlTableColumnInfo> tableColumnList, List<Object> allFieldList) {
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
     * 获取主键
     */
    public List<String> getTableKeys(Class<?> clas){
        List<MySqlEntityColumn> entityColumns = MySqlInfoUtil.getEntityColumns(clas);
        return entityColumns.stream()
                .filter(MySqlEntityColumn::isKey)
                .map(MySqlEntityColumn::getColumnName)
                .collect(Collectors.toList());
    }
}
