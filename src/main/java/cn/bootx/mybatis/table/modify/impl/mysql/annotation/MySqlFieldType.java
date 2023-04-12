package cn.bootx.mybatis.table.modify.impl.mysql.annotation;

import cn.bootx.mybatis.table.modify.impl.mysql.constants.MySqlFieldTypeEnum;

import java.lang.annotation.*;

import static cn.bootx.mybatis.table.modify.impl.mysql.constants.MySqlFieldTypeEnum.DEFAULT;

/**
 * MySQL数据库字段类型
 * @author xxm
 * @date 2023/4/7
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MySqlFieldType {
    /**
     * 类型
     */
    MySqlFieldTypeEnum value();
}
