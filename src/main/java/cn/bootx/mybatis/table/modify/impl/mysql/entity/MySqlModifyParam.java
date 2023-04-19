package cn.bootx.mybatis.table.modify.impl.mysql.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 更新表语句
 * @author xxm
 * @date 2023/4/19
 */
@Data
@Accessors(chain = true)
public class MySqlModifyParam {
    /** 表名称 */
    private String name;

    /** 表注释 */
    private String comment;

    /** 表注释是否需要更新 */
    private boolean commentUpdate;

    /** 字符集 */
    private String charset;

    /** 字符集是否需要更新 */
    private boolean charsetUpdate;

    /** 存储引擎 */
    private String engine;

    /** 存储引擎是否需要更新 */
    private boolean engineUpdate;

    /** 主键列表 */
    private String keys;

    /** 新增索引列表 */
    private List<String> addIndexes;

    /** 删除索引列表 */
    private List<String> dropIndexes;

    /** 新增字段列表 */
    private List<String> addColumns;

    /** 修改字段列表 */
    private List<String> modifyColumns;

    /** 删除字段列表 */
    private List<String> dropColumns;
}
