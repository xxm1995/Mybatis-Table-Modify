package cn.bootx.mybatis.table.modify.impl.mysql.annotation;

import java.lang.annotation.*;

/**
 * MYSQL字段索引组
 * @author xxm
 * @date 2023/4/10
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface MySqlIndexes {

    /**
     * MYSQL字段索引组
     */
    MySqlIndex[] value();
}
