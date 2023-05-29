package cn.bootx.mybatis.table.modify.annotation;

import java.lang.annotation.*;

/**
 * 注释内容注解
 * @author xxm
 * @date 2023/5/29
 */
@Target({ElementType.FIELD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DbComment {

    /**
     * 注释内容
     */
    String value();
}
