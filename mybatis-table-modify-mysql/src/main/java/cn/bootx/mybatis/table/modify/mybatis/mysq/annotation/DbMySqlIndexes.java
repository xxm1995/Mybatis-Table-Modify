package cn.bootx.mybatis.table.modify.mybatis.mysq.annotation;

import java.lang.annotation.*;

/**
 * MYSQL字段索引组
 * @author xxm
 * @date 2023/4/10
 */
@Target({ElementType.TYPE,ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface DbMySqlIndexes {

    /**
     * MYSQL字段索引组
     */
    DbMySqlIndex[] value();
}
