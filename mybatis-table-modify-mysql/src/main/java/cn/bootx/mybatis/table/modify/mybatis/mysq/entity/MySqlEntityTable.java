package cn.bootx.mybatis.table.modify.mybatis.mysq.entity;

import cn.bootx.mybatis.table.modify.constants.TableCharset;
import cn.bootx.mybatis.table.modify.mybatis.mysq.constants.MySqlEngineEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 数据表信息
 * @author xxm
 * @date 2023/4/14
 */
@Data
@Accessors(chain = true)
public class MySqlEntityTable {

    /** 表名称 */
    private String name;

    /** 表注释 */
    private String comment;

    /** 字符集 */
    private TableCharset charset;

    /** 存储引擎 */
    private MySqlEngineEnum engine;

    /** 主键列表 */
    private List<String> keys;

}
