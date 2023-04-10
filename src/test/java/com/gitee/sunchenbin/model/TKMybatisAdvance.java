package com.gitee.sunchenbin.model;

import cn.bootx.mybatis.table.modify.impl.mysql.annotation.MySqlIndex;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 使用tk.mybatis的Table，DbColumn，Id三个注解，其他的是actable的注解
 */
@Table(name = "t_tkmybatis_advance")
public class TKMybatisAdvance {

    @Id
    private Long id;

    @Column(length = 50, nullable = false)
    @MySqlIndex(value = "idx_name_shop", columns = { "name", "shop" })
    private String name;

    @Column(name = "create_time")
    private Date createTime;

    @Column
    private Boolean isTrue;

    @Column
    private Integer age;

    @Column(length = 10, scale = 4)
    private BigDecimal price;

    @Column
    private String identitycard;

    @Column
    private String shop;

}
