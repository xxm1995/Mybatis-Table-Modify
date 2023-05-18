package cn.bootx.mybatis.table.modify.mybatis.mysq.entity;

import cn.bootx.mybatis.table.modify.constants.TableCharset;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
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
    private List<String> columns = new ArrayList<>();

    /** 主键列表 */
    private String keys;

    /** 索引列表 */
    private List<String> indexes = new ArrayList<>();
}
