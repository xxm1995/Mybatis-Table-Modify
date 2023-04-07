package com.gitee.sunchenbin.model;

import cn.bootx.mybatis.table.modify.annotation.*;
import cn.bootx.mybatis.table.modify.impl.mysql.constants.MySqlFieldTypeEnum;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.util.Date;

/**
 *
 */
@TableName("t_mybatisplus_advance")
public class MybatisPlusAdvance {

    @TableId
    private Long id;

    @TableField
    @ColumnType(length = 50)
    @Index(value = "idx_name_shop", columns = { "name", "shop" })
    private String name;

    @TableField("create_time")
    @ColumnType(MySqlFieldTypeEnum.TIMESTAMP)
    private Date createTime;

    @TableField
    @DefaultValue("1")
    private Boolean isTrue;

    @TableField
    @ColumnType(value = MySqlFieldTypeEnum.INT, length = 3)
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
