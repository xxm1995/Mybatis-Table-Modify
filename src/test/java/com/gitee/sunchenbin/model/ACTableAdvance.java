package com.gitee.sunchenbin.model;

import cn.bootx.mybatis.table.modify.annotation.*;
import cn.bootx.mybatis.table.modify.impl.mysql.constants.MySqlFieldTypeEnum;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 全部采用actable自有的注解
 */
@Table(value = "t_actable_advance", comment = "actable进阶配置")
public class ACTableAdvance {

    @IsKey
    private Long id;

    @Column
    @Index(value = "idx_name_shop", columns = { "name", "shop" })
    private String name;

    @Column(name = "create_time",  comment = "创建时间")
    private Date createTime;

    @Column
    @DefaultValue("true")
    private Boolean isTrue;

    @Column
    @ColumnType(value = MySqlFieldTypeEnum.INT, length = 3)
    private Integer age;

    @Column(length = 10, decimalLength = 4)
    private BigDecimal price;

    @Column
    @Unique("uni_identitycard")
    private String identitycard;

    @Column
    @Unique(columns = { "name", "shop" })
    private String shop;

}
