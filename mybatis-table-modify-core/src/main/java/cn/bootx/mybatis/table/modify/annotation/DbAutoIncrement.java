package cn.bootx.mybatis.table.modify.annotation;

import java.lang.annotation.*;

/**
 * 数据自增
 * @author xxm
 * @date 2023/6/8
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DbAutoIncrement {
}
