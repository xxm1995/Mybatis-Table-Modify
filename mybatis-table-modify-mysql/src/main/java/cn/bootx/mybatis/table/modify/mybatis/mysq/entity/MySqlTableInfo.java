package cn.bootx.mybatis.table.modify.mybatis.mysq.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 表信息
 *
 * @author sunchenbin
 * @date 2020/11/11
 */
@Getter
@Setter
public class MySqlTableInfo {

    /** 字符集的后缀 */
    public static final String TABLE_COLLATION_SUFFIX = "_general_ci";

    /** 字符集 */
    public static final String TABLE_COLLATION_KEY = "table_collation";

    /** 注释 */
    public static final String TABLE_COMMENT_KEY = "table_comment";

    /** 引擎 */
    public static final String TABLE_ENGINE_KEY = "engine";

    /** 数据表登记目录 */
    private String tableCatalog;

    /** 数据表所属的数据库名 */
    private String tableSchema;

    /** 表名称 */
    private String tableName;

    /** 表类型[system view|base table] */
    private String tableType;

    /** 使用的数据库引擎[MyISAM|CSV|InnoDB] */
    private String engine;

    /** 版本，默认值10 */
    private Long version;

    /** 行格式[Compact|Dynamic|Fixed] */
    private String rowFormat;

    /** 表里所存多少行数据 */
    private Long tableRows;

    /** 平均行长度 */
    private Long avgRowLength;

    /** 数据长度 */
    private Long dataLength;

    /** 最大数据长度 */
    private Long maxDataLength;

    /** 索引长度 */
    private Long indexLength;

    /** 空间碎片 */
    private Long dataFree;

    /** 做自增主键的自动增量当前值 */
    private Long autoIncrement;

    /** 表的创建时间 */
    private Date createTime;

    /** 表的更新时间 */
    private Date updateTime;

    /** 表的检查时间 */
    private Date checkTime;

    /** 表的字符校验编码集 */
    private String tableCollation;

    /** 校验和 */
    private Long checksum;

    /** 创建选项 */
    private String createOptions;

    /** 表的注释、备注 */
    private String tableComment;

}
