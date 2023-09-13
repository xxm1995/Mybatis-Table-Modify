package cn.bootx.table.modify.mybatis.mysq.annotation;

import cn.bootx.table.modify.mybatis.mysq.constants.MySqlFieldTypeEnum;

import java.lang.annotation.*;

/**
 * MySQL数据库字段类型
 * @author xxm
 * @date 2023/4/7
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DbMySqlFieldType {
    /**
     * 类型
     */
    MySqlFieldTypeEnum value();
}
