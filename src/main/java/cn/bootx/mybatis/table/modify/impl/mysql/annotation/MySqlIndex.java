package cn.bootx.mybatis.table.modify.impl.mysql.annotation;

import cn.bootx.mybatis.table.modify.impl.mysql.constants.MySqlIndexType;

import java.lang.annotation.*;

/**
 * MYSQL字段索引
 *
 * @author sunchenbin
 * @version 2019年6月14日 下午6:12:48
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(value = MySqlIndexes.class)
@Documented
public @interface MySqlIndex {

    /**
     * 索引的类型
     */
    MySqlIndexType type() default MySqlIndexType.NORMAL;

    /**
     * 索引的名字, 不设置默认为索引类型+用_分隔的字段名计划
     */
    String name() default "";

    /**
     * 要建立索引的字段名，可设置多个建立联合索引{"login_mobile","login_name"}
     */
    String[] columns();

    /**
     * 索引注释
     */
    String comment() default "";


}
