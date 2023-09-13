package cn.bootx.table.modify.mybatis.postgresql.annotation;

import java.lang.annotation.*;

/**
 *
 * @author xxm
 * @since 2023/8/2
 */
@Target({ElementType.TYPE,ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface DbPgSqlIndexes {

    /**
     * PGSQL字段索引组
     */
    DbPgSqlIndex[] value();
}
