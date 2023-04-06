package cn.bootx.mybatis.actable.impl.mysql.entity;

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
public class MySqlTypeAndLength {

    /** 长度数 */
    private Integer lengthCount;

    /** 长度 */
    private Integer length;

    /** 小数长度 */
    private Integer decimalLength;

    /** 类型 */
    private String type;

}
