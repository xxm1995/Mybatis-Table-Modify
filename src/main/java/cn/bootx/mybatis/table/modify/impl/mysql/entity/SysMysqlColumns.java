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
public class SysMysqlColumns {

    /**
     * 字段名
     */
    public static final String COLUMN_NAME_KEY = "column_name";

    /**
     * 默认值
     */
    public static final String COLUMN_DEFAULT_KEY = "column_default";

    /**
     * 是否可为null，值：(YES,NO)
     */
    public static final String IS_NULLABLE_KEY = "is_nullable";

    /**
     * 数据类型
     */
    public static final String DATA_TYPE_KEY = "data_type";

    /**
     * 长度，如果是0的话是null
     */
    public static final String NUMERIC_PRECISION_KEY = "numeric_precision";

    /**
     * 小数点数
     */
    public static final String NUMERIC_SCALE_KEY = "numeric_scale";

    /**
     * 是否为主键，是的话是PRI
     */
    public static final String COLUMN_KEY_KEY = "column_key";

    /**
     * 是否为自动增长，是的话为auto_increment
     */
    public static final String EXTRA_KEY = "extra";

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

    private String characterMaximumLength;

    private String characterOctetLength;

    /**
     * 长度
     */
    private String numericPrecision;

    /**
     * 小数点数
     */
    private String numericScale;

    private String characterSetName;

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

    private String privileges;

    private String columnComment;

}
