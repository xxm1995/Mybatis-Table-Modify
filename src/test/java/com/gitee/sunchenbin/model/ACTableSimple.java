package com.gitee.sunchenbin.model;

import cn.bootx.mybatis.table.modify.annotation.*;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 全部采用actable自有的注解
 */
@Table(comment = "actable简单配置")
public class ACTableSimple {

    @IsKey
    private Long id;

    @Column
    @Index
    private String name;

    @Column
    private Date createTime;

    @Column(defaultValue = "false")
    private Boolean isTrue;

    @Column
    private Integer age;

    @Column
    private BigDecimal price;

    @Column
    @Unique
    private String identitycard;

}
