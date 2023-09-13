package cn.bootx.table.modify.mybatis.postgresql.annotation;

import java.lang.annotation.*;

/**
 * PostgreSql数据库字段类型
 * @author xxm
 * @since 2023/8/2
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DbPgSqlFieldType {
}
