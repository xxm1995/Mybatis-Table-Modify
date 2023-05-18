package cn.bootx.mybatis.table.modify.mybatis.mysq.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 索引类型
 * @author xxm
 * @date 2023/4/10
 */
@Getter
@AllArgsConstructor
public enum MySqlIndexType {
    /**
     * 普通索引
     */
    NORMAL("INDEX","index_","USING BTREE"),
    /**
     * 空间索引
     */
    SPATIAL("SPATIAL","spa_",null),
    /**
     * 全文索引
     */
    FULLTEXT("FULLTEXT","full_",null),
    /**
     * 唯一索引
     */
    UNIQUE("UNIQUE INDEX","uni_","USING BTREE");

    /** 索引名称 */
    private final String name;
    /** 索引名称前缀 */
    private final String prefix;
    /** 所使用的索引方式 */
    private final String using;
}
