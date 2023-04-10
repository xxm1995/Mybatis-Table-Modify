package com.gitee.sunchenbin.model;

import cn.bootx.mybatis.table.modify.annotation.*;
import cn.bootx.mybatis.table.modify.impl.mysql.annotation.MySqlIndex;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 全部采用actable自有的注解
 */
@DbTable(comment = "actable简单配置")
public class ACTableSimple {

    @IsKey
    private Long id;

    @DbColumn
    @MySqlIndex
    private String name;

    @DbColumn
    private Date createTime;

    @DbColumn(defaultValue = "false")
    private Boolean isTrue;

    @DbColumn
    private Integer age;

    @DbColumn
    private BigDecimal price;

    @DbColumn
    private String identitycard;

}
