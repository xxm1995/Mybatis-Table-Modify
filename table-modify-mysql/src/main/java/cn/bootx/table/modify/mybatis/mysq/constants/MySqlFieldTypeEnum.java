package cn.bootx.table.modify.mybatis.mysq.constants;

import cn.bootx.table.modify.mybatis.mysq.entity.MySqlTypeAndLength;
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

    /**
     * 字段类型参数个数:
     * 0. 不需要长度参数, 比如date类型
     * 1. 需要一个长度参数, 比如 int/datetime/varchar等
     * 2. 需要小数位数的, 比如 decimal/float等
     */
    private final int paramCount;

    /** 默认长度 */
    private final Integer lengthDefault;

    /** 默认小数长度 */
    private final Integer decimalLengthDefault;

    /**
     * 获取类型
     * @return MySQL类型信息
     */
    public MySqlTypeAndLength getTypeAndLength(){
        return new MySqlTypeAndLength(
                this.toString().toLowerCase(),
                this.getParamCount(),
                this.getLengthDefault(),
                this.getDecimalLengthDefault()
        );
    }

}
