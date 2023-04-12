package cn.bootx.mybatis.table.modify.impl.mysql.entity;

import cn.bootx.mybatis.table.modify.impl.mysql.constants.MySqlIndexType;
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
public class MySqlEntityIndexInfo {

    /** 类型 */
    private MySqlIndexType type;

    /** 名称 */
    private String name;

    /** 字段名称 */
    private List<String> columns;

    /** 注释 */
    private String comment;
}
