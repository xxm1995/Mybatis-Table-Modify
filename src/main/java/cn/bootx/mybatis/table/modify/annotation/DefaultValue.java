package cn.bootx.mybatis.table.modify.annotation;

import java.lang.annotation.*;

/**
 * 字段的默认值
 *
 * @author sunchenbin
 * @version 2020年11月09日 下午6:13:37
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DefaultValue {

    /**
     * 字段的默认值
     * @return 字段的默认值
     */
    String value();

}
