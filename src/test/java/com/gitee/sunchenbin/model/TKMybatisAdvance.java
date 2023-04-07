package com.gitee.sunchenbin.model;

import cn.bootx.mybatis.table.modify.annotation.*;
import cn.bootx.mybatis.table.modify.impl.mysql.constants.MySqlFieldTypeEnum;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 使用tk.mybatis的Table，Column，Id三个注解，其他的是actable的注解
 */
@Table(name = "t_tkmybatis_advance")
public class TKMybatisAdvance {

    @Id
    private Long id;

    @Column(length = 50, nullable = false)
    @Index(value = "idx_name_shop", columns = { "name", "shop" })
    private String name;

    @Column(name = "create_time")
    @ColumnType(MySqlFieldTypeEnum.TIMESTAMP)
    private Date createTime;

    @Column
    @DefaultValue("0")
    private Boolean isTrue;

    @Column
    @ColumnType(value = MySqlFieldTypeEnum.INT, length = 3)
    private Integer age;

    @Column(length = 10, scale = 4)
    private BigDecimal price;

    @Column
    @Unique("uni_identitycard")
    private String identitycard;

    @Column
    @Unique(columns = { "name", "shop" })
    private String shop;

}
