package cn.bootx.mybatis.table.modify.mybatis.mysq.annotation;

import cn.bootx.mybatis.table.modify.mybatis.mysq.constants.MySqlIndexType;

import java.lang.annotation.*;

/**
 * MYSQL字段索引
 *
 * @author sunchenbin
 * @version 2019年6月14日 下午6:12:48
 */
@Target({ElementType.TYPE,ElementType.FIELD})
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
     * 要建立索引的数据库字段名，可设置多个建立联合索引{"login_mobile","login_name"}, 只可以在类上使用, 字段上使用不生效
     */
    String[] columns() default {};

    /**
     * 要建立索引的实体类字段名，会自动转换为数据库字段名称，
     * 可设置多个建立联合索引{"login_mobile","login_name"}, 只可以在类上使用, 字段上使用不生效
     * 如果 {columns} 同时配置的话，本配置不生效
     */
    String[] fields() default {};

    /**
     * 索引注释
     */
    String comment() default "";


}
