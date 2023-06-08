package cn.bootx.mybatis.table.modify.annotation;

import java.lang.annotation.*;

/**
 * 忽略行字段注解
 * @author xxm
 * @date 2023/4/23
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DbIgnore {
}
