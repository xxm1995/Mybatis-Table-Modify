package cn.bootx.mybatis.table.modify.impl.mysql.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Mysql 类型和长度
 *
 * @author sunchenbin
 * @date 2020/5/27
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MySqlTypeAndLength implements Cloneable{

    /** 字段类型(MySQL类型) */
    private String type;

    /** 字段参数长度 */
    private int paramCount;

    /** 长度 */
    private Integer length;

    /** 小数长度 */
    private Integer decimalLength;

    @Override
    public MySqlTypeAndLength clone() {
        try {
            return (MySqlTypeAndLength) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
