package cn.bootx.mybatis.table.modify.mybatis.mysq.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * Mysql 索引信息表(statistics)
 * @author xxm
 * @date 2023/4/10
 */
@Getter
@Setter
public class MySqlTableIndex {

    /** 是否不能重复 */
    private boolean nonUnique;

    /** 索引的名称 */
    private String indexName;

    /** 列名称 */
    private String columnName;

    /** 索引类型（BTREE，FULLTEXT，HASH，RTREE） */
    private String indexType;

    /** 索引注释 */
    private String indexComment;

    /** 联合索引中的列序列号 以1开头 */
    private Integer seqInIndex;
}
