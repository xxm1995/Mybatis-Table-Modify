package cn.bootx.mybatis.table.modify.annotation;

import cn.bootx.mybatis.table.modify.impl.mysql.constants.MySqlFieldTypeEnum;

import java.lang.annotation.*;

/**
 * 字段的类型
 *
 * @author sunchenbin
 * @version 2020年11月09日 下午6:13:37
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ColumnType {

    /**
     * 字段的类型 仅支持com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant中的枚举数据类型
     * @return 字段的类型
     */
    MySqlFieldTypeEnum value() default MySqlFieldTypeEnum.DEFAULT;

    /**
     * 字段长度，默认是255 类型默认长度：com.gitee.sunchenbin.mybatis.actable.constants.MySqlFieldTypeEnum
     * @return 字段长度，默认是255
     */
    int length() default 255;

    /**
     * 小数点长度，默认是0 类型默认长度：com.gitee.sunchenbin.mybatis.actable.constants.MySqlFieldTypeEnum
     * @return 小数点长度，默认是0
     */
    int decimalLength() default 0;

}
