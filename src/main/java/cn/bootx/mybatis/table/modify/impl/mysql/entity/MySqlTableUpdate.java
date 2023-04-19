package cn.bootx.mybatis.table.modify.impl.mysql.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 表所需要更新的信息
 * @author xxm
 * @date 2023/4/14
 */
@Data
@Accessors(chain = true)
public class MySqlTableUpdate {

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
    private List<String> keys;

    /** 字符集是否需要更新 */
    private boolean keysUpdate;
}
