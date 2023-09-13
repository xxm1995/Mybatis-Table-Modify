package cn.bootx.table.modify.mybatis.mysq.entity;

import cn.bootx.table.modify.mybatis.mysq.constants.MySqlIndexType;
import cn.bootx.table.modify.mybatis.mysq.util.MySqlInfoUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 索引信息
 * @author xxm
 * @date 2023/4/10
 */
@Getter
@Setter
@Accessors(chain = true)
public class MySqlEntityIndex {

    /** 类型 */
    private MySqlIndexType type;

    /** 名称 */
    private String name;

    /** 字段名称 */
    private List<String> columns;

    /** 注释 */
    private String comment;

    /**
     * 转换成索引语句
     * UNIQUE INDEX `a`(`id`) USING BTREE COMMENT '1',
     * INDEX `b`(`remark`, `age`) USING BTREE COMMENT '2',
     * FULLTEXT INDEX `c`(`name`) COMMENT '3',
     * SPATIAL INDEX `d`(`age`)
     */
    public String toIndex(){
        StringBuilder sb = new StringBuilder(getType().getName()).append(" ");
        // 索引名称
        sb.append("`").append(getName()).append("`");
        // 索引字段
        sb.append(MySqlInfoUtil.buildBracketParams(getColumns()));
        // 所使用的索引类型
        if (StrUtil.isNotBlank(getType().getUsing())){
            sb.append(" ").append(getType().getUsing());
        }
        // 注释
        if (StrUtil.isNotBlank(getComment())){
            sb.append(" COMMENT '").append(getComment()).append("'");
        }
        return sb.toString();
    }
}
