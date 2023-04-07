package cn.bootx.mybatis.table.modify.impl.mysql.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用于配置Mysql数据库中类型，并且该类型需要设置几个长度 这里配置多少个类型决定了，创建表能使用多少类型 例如：varchar(1) decimal(5,2)
 * datetime
 *
 * @author sunchenbin
 * @version 2016年6月23日 下午5:59:33
 */
@Getter
@AllArgsConstructor
public enum MySqlFieldTypeEnum {

    /** DEFAULT */
    DEFAULT(null, null, null),
    /** INT */
    INT(1, 11, null),
    /** VARCHAR */
    VARCHAR(1, 255, null),
    /** BINARY */
    BINARY(1, 1, null),
    /** CHAR */
    CHAR(1, 255, null),
    /** BIGINT */
    BIGINT(1, 20, null),
    /** BIT */
    BIT(1, 1, null),
    /** TINYINT */
    TINYINT(1, 4, null),
    /** SMALLINT */
    SMALLINT(1, 6, null),
    /** MEDIUMINT */
    MEDIUMINT(1, 9, null),
    /** DECIMAL */
    DECIMAL(2, 10, 2),
    /** DOUBLE */
    DOUBLE(2, 10, 0),
    /** TEXT */
    TEXT(0, null, null),
    /** MEDIUMTEXT */
    MEDIUMTEXT(0, null, null),
    /** LONGTEXT */
    LONGTEXT(0, null, null),
    /** DATETIME */
    DATETIME(0, null, null),
    /** TIMESTAMP */
    TIMESTAMP(0, null, null),
    /** DATE */
    DATE(0, null, null),
    /** TIME */
    TIME(0, null, null),
    /** FLOAT */
    FLOAT(2, 10, 0),
    /** YEAR */
    YEAR(0, null, null),
    /** BLOB */
    BLOB(0, null, null),
    /** LONGBLOB */
    LONGBLOB(0, null, null),
    /** MEDIUMBLOB */
    MEDIUMBLOB(0, null, null),
    /** TINYTEXT */
    TINYTEXT(0, null, null),
    /** TINYBLOB */
    TINYBLOB(0, null, null),
    /** JSON */
    JSON(0, null, null);

    /** 长度数 */
    private final Integer lengthCount;

    /** 默认长度 */
    private final Integer lengthDefault;

    /** 默认小数长度 */
    private final Integer decimalLengthDefault;

}
