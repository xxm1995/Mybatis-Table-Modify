package cn.bootx.mybatis.table.modify.impl.mysql.entity;

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
public class SysMySqlTableInfo {

    /** 字符集的后缀 */
    public static final String TABLE_COLLATION_SUFFIX = "_general_ci";

    /** 字符集 */
    public static final String TABLE_COLLATION_KEY = "table_collation";

    /** 注释 */
    public static final String TABLE_COMMENT_KEY = "table_comment";

    /** 引擎 */
    public static final String TABLE_ENGINE_KEY = "engine";

    private String tableCatalog;

    private String tableSchema;

    private String tableName;

    private String tableType;

    private String engine;

    private Long version;

    private String rowFormat;

    private Long tableRows;

    private Long avgRowLength;

    private Long dataLength;

    private Long maxDataLength;

    private Long indexLength;

    private Long dataFree;

    private Long autoIncrement;

    private Date createTime;

    private Date updateTime;

    private Date checkTime;

    private String tableCollation;

    private Long checksum;

    private String createOptions;

    private String tableComment;

}
