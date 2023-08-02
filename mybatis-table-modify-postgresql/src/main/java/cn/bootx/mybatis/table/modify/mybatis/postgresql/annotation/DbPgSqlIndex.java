package cn.bootx.mybatis.table.modify.mybatis.postgresql.annotation;

import java.lang.annotation.*;

/**
 *
 * @author xxm
 * @since 2023/8/2
 */
@Target({ElementType.TYPE,ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(value = DbPgSqlIndexes.class)
@Documented
public @interface DbPgSqlIndex {
}
