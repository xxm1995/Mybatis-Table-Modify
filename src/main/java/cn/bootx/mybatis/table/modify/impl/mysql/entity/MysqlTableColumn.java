package cn.bootx.mybatis.table.modify.impl.mysql.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * 用于查询表中字段结构详细信息
 *
 * @author sunchenbin
 * @version 2016年6月23日 下午6:10:56
 */
@Getter
@Setter
public class MysqlTableColumn {


    /**
     * catalog
     */
    private String tableCatalog;

    /**
     * 库名
     */
    private String tableSchema;

    /**
     * 表名
     */
    private String tableName;

    /**
     * 字段名
     */
    private String columnName;

    /**
     * 字段位置的排序
     */
    private String ordinalPosition;

    /**
     * 字段默认值
     */
    private String columnDefault;

    /**
     * 是否可以为null
     */
    private String isNullable;

    /**
     * 字段类型
     */
    private String dataType;

    /**
     * 字符最大长度
     */
    private String characterMaximumLength;

    /**
     * 字符八进制长度
     */
    private String characterOctetLength;

    /**
     * 长度
     */
    private String numericPrecision;

    /**
     * 小数点数
     */
    private String numericScale;

    /**
     * 字符集名称
     */
    private String characterSetName;

    /**
     * 排序规则名称
     */
    private String collationName;

    /**
     * 类型加长度拼接的字符串，例如varchar(100)
     */
    private String columnType;

    /**
     * 主键:PRI；唯一键:UNI
     */
    private String columnKey;

    /**
     * 是否为自动增长，是的话为auto_increment
     */
    private String extra;

    /**
     * 可以操作的权限
     */
    private String privileges;

    /**
     * 行备注
     */
    private String columnComment;

}
