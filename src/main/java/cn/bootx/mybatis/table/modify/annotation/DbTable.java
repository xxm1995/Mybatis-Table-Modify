package cn.bootx.mybatis.table.modify.annotation;

import cn.bootx.mybatis.table.modify.constants.TableCharset;

import java.lang.annotation.*;

/**
 * 创建表时的表名
 *
 * @author sunchenbin
 * @version 2016年6月23日 下午6:13:37
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DbTable {

    /**
     * 表名
     */
    String name() default "";

    /**
     * 表名
     */
    String value() default "";

    /**
     * 表注释，也可以使用
     */
    String comment() default "";

    /**
     * 表字符集，也可以使用
     * @see cn.bootx.mybatis.table.modify.constants.TableCharset
     */
    TableCharset charset() default TableCharset.DEFAULT;

    /**
     * 是否开启simple模式配置，默认不开启，开启后Field不写注解@Column也可以采用默认的驼峰转换法创建字段
     */
    boolean isSimple() default true;

    /**
     * 需要排除的属性名，排除掉的属性不参与建表, 也可以在字段上配置忽略该行注解
     */
    String[] excludeFields() default { "serialVersionUID" };

}
