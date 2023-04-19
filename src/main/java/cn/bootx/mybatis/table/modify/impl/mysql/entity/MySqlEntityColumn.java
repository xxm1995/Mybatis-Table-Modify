package cn.bootx.mybatis.table.modify.impl.mysql.entity;

import cn.hutool.core.util.StrUtil;
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
    private String name;

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

    /** 长度 */
    private Integer length;

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

    /**
     * 建表的字段语句
     *  id` bigint(20) NOT NULL COMMENT '角色ID',
     * `name` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '名称',
     * `age` int(5) NOT NULL COMMENT '年龄',
     * `vip` bit(1) NULL DEFAULT NULL COMMENT '是否vip',
     * `qa` decimal(255, 12) NULL,
     */
    public String toColumn(){
        // 字段名
        StringBuilder sb = new StringBuilder("`").append(getName()).append("`");
        // 数据类型
        sb.append(" ").append(getFieldType());
        // 字段长度
        if (getParamCount() == 1){
            sb.append("(").append(getLength()).append(")");
        }
        // 小数位数
        if (getParamCount() == 2){
            sb.append("(")
                    .append(getLength())
                    .append(", ")
                    .append(getDecimalLength())
                    .append(")");
        }
        // 是否可以为空
        if (isFieldIsNull()){
            sb.append(" NULL");
        } else {
            sb.append(" NOT NULL");
        }
        // 默认值
        if (StrUtil.isNotBlank(getDefaultValue())){
            sb.append(" DEFAULT ").append(getDefaultValue());
        }

        // 自增
        if (isAutoIncrement()){
            sb.append(" AUTO_INCREMENT");
        }
        // 无符号 待补充
        // 填充零 待补充
        return sb.toString();
    }

}
