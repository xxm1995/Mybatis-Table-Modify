package cn.bootx.table.modify.annotation;

import java.lang.annotation.*;

/**
 * 字段排序 数字小的在前面,大的在后面
 * @author xxm
 * @date 2023/6/8
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DbOrder {
    /** 排序值 */
    int value();
}
