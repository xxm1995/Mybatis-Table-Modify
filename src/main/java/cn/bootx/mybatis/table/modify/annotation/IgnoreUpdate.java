package cn.bootx.mybatis.table.modify.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 作者：guoyzh 时间：2021/5/6 9:43 功能：忽略字段更新
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface IgnoreUpdate {

    // 是否忽略对当前字段的更新操作
    boolean value() default true;

}
