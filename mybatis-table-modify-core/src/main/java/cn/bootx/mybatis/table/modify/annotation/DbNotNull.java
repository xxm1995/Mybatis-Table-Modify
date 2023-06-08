package cn.bootx.mybatis.table.modify.annotation;

import java.lang.annotation.*;

/**
 * 字段不可为空
 * @author xxm
 * @date 2023/6/8
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DbNotNull {
}
