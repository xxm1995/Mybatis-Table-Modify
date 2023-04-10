package cn.bootx.mybatis.table.modify.impl.mysql.constants;

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
    NORMAL("index_"),
    /**
     * 空间索引 暂时不支持
     */
    SPATIAL(""),
    /**
     * 全文索引
     */
    FULLTEXT("full_"),
    /**
     * 唯一索引
     */
    UNIQUE("uni_");

    /** 索引名称前缀 */
    private final String prefix;
}
