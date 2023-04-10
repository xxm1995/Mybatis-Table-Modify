package com.gitee.sunchenbin.model;

import cn.bootx.mybatis.table.modify.impl.mysql.annotation.MySqlIndex;
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
    @MySqlIndex(value = "idx_name_shop", columns = { "name", "shop" })
    private String name;

    @TableField("create_time")
    private Date createTime;

    @TableField
    private Boolean isTrue;

    @TableField
    private Integer age;

    @TableField
    private BigDecimal price;

    @TableField
    private String identitycard;

    @TableField
    private String shop;

    @TableField(exist = false)
    private String notExsit;

}
