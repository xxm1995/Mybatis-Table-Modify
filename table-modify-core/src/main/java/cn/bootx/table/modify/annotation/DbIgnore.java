package cn.bootx.table.modify.annotation;

import java.lang.annotation.*;

/**
 * 忽略表或行字段注解
 * @author xxm
 * @date 2023/4/23
 */
@Target({ElementType.FIELD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DbIgnore {
}
