package cn.bootx.mybatis.table.modify.annotation;

import cn.bootx.mybatis.table.modify.constants.TableCharset;

import java.lang.annotation.*;

/**
 * 创建表时的表名
 *
 * @author sunchenbin
 * @version 2016年6月23日 下午6:13:37
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DbTable {

    /**
     * 表名
     */
    String name() default "";

    /**
     * 表名
     */
    String value() default "";

    /**
     * 表注释，也可以使用
     */
    String comment() default "";

    /**
     * 表字符集，也可以使用
     * @see TableCharset
     */
    TableCharset charset() default TableCharset.DEFAULT;

    /**
     * 是否开启simple模式配置，默认不开启，开启后Field不写注解@Column也可以采用默认的驼峰转换法创建字段
     */
    boolean isSimple() default true;

    /**
     * 追加模式, 通常应用在表已经创建，实体类上的注解也已经去掉后，要对表信息进行微调的场景，会有以下特征
     * 1. 字段需要添加@DbColumn注解才会触发更新或新增
     * 2. 表注释、字符集、引擎不配置或值为空不进行更新
     * 3. 不会删除索引
     * 4. 不处理主键更新
     */
    boolean isAppend() default false;

    /**
     * 需要排除的属性名，排除掉的属性不参与建表, 也可以在字段上配置忽略该行注解
     */
    String[] excludeFields() default { "serialVersionUID" };

}
