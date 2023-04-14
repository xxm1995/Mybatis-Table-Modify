package cn.bootx.mybatis.table.modify.impl.mysql.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 用于存放创建表的字段信息
 *
 * @author sunchenbin, Spet
 * @version 2019/07/06
 */
@Getter
@Setter
@Accessors(chain = true)
public class MySqlEntityColumn implements Cloneable {

    /** 字段名 */
    private String columnName;

    /** 排序 */
    private int order;

    /** 字段类型 */
    private String fieldType;

    /**
     * 字段类型参数个数:
     * 0. 不需要长度参数, 比如date类型
     * 1. 需要一个长度参数, 比如 int/datetime/varchar等
     * 2. 需要小数位数的, 比如 decimal/float等
     */
    private int paramCount;

    /** 小数类型的浮点长度 */
    private int decimalLength;

    /** 字段是否非空 */
    private boolean fieldIsNull;

    /** 字段是否是主键 */
    private boolean key;

    /** 主键是否自增 */
    private boolean autoIncrement;

    /** 字段默认值 */
    private String defaultValue;

    /** 字段默认值是否原生，原生使用$,非原生使用# */
    private boolean defaultValueNative;

    /** 字段的备注 */
    private String comment;

    /** 是否忽略更新 */
    private boolean ignoreUpdate;

    @Override
    public MySqlEntityColumn clone() {
        MySqlEntityColumn columnParam = null;
        try {
            columnParam = (MySqlEntityColumn) super.clone();
        }
        catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return columnParam;
    }

}
