package com.gitee.sunchenbin.model;

import cn.bootx.mybatis.actable.annotation.*;
import cn.bootx.mybatis.actable.impl.mysql.constants.MySqlFieldType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import cn.bootx.mybatis.actable.impl.mysql.constants.MySqlCharset;
import cn.bootx.mybatis.actable.impl.mysql.constants.MySqlEngine;

import java.math.BigDecimal;
import java.util.Date;

/**
 *
 */
@TableName("t_mybatisplus_advance")
@TableComment("mybatisplus进阶版配置结合actable注解")
@TableCharset(MySqlCharset.UTF8)
@TableEngine(MySqlEngine.InnoDB)
public class MybatisPlusAdvance {

    @TableId
    @IsAutoIncrement
    private Long id;

    @TableField
    @ColumnType(length = 50)
    @IsNotNull
    @Index(value = "idx_name_shop", columns = { "name", "shop" })
    private String name;

    @TableField("create_time")
    @ColumnType(MySqlFieldType.TIMESTAMP)
    @ColumnComment("创建时间")
    private Date createTime;

    @TableField
    @DefaultValue("1")
    private Boolean isTrue;

    @TableField
    @ColumnComment("年龄")
    @ColumnType(value = MySqlFieldType.INT, length = 3)
    private Integer age;

    @TableField
    @ColumnType(length = 10, decimalLength = 4)
    private BigDecimal price;

    @TableField
    @Unique("uni_identitycard")
    private String identitycard;

    @TableField
    @Unique(columns = { "name", "shop" })
    private String shop;

    @TableField(exist = false)
    private String notExsit;

}
