package cn.bootx.mybatis.table.modify.impl.mysql.entity;

import cn.bootx.mybatis.table.modify.impl.mysql.constants.MySqlEngineEnum;
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
public class MySqlEntityTableInfo {

    /** 表注释 */
    private String comment;

    /** 字符集 */
    private String charset;

    /** 存储引擎 */
    private MySqlEngineEnum engine;

    /** 主键列表 */
    private List<String> keys;

}
