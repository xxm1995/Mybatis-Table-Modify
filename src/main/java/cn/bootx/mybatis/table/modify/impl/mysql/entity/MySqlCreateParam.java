package cn.bootx.mybatis.table.modify.impl.mysql.entity;

import cn.bootx.mybatis.table.modify.constants.TableCharset;
import cn.bootx.mybatis.table.modify.impl.mysql.constants.MySqlEngineEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 建表语句
 * @author xxm
 * @date 2023/4/18
 */
@Data
@Accessors(chain = true)
public class MySqlCreateParam {
    /** 表名称 */
    private String name;

    /** 表注释 */
    private String comment;

    /** 字符集 */
    private String charset;

    /** 存储引擎 */
    private String engine;

    /** 行列表 */
    private List<String> columns;

    /** 主键列表 */
    private String keys;

    /** 索引列表 */
    private List<String> indexes;
}
