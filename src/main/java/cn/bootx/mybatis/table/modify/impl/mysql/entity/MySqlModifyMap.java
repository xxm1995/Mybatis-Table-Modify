package cn.bootx.mybatis.table.modify.impl.mysql.entity;

import cn.bootx.mybatis.table.modify.domain.TableConfig;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用于存储各种操作MySQL表结构的容器
 * @author xxm
 * @date 2023/4/7
 */
@Getter
public class MySqlModifyMap {

    /** 用于存需要创建的表名+（字段结构/表信息） */
    private final Map<String, TableConfig> createTables = new HashMap<>();
    /** 用于存需要更新字段类型等的表名+结构 */
    private final Map<String, TableConfig> updateColumns = new HashMap<>();
    /** 用于存需要增加字段的表名+结构 */
    private final Map<String, TableConfig> addColumns = new HashMap<>();
    /** 用于存需要删除字段的表名+结构 */
    private final Map<String, TableConfig> removeColumns = new HashMap<>();
    /** 用于存需要删除主键的表名+结构 */
    private final Map<String, TableConfig> dropKeys = new HashMap<>();
    /** 用于存需要增加的表名+索引列表 */
    private final Map<String, List<MySqlEntityIndexInfo>> addIndexes = new HashMap<>();
    /** 用于存需要更新的表名+索引列表 */
    private final Map<String, List<MySqlEntityIndexInfo>> updateIndexes = new HashMap<>();
    /** 用于存需要删除的表名+索引列表 */
    private final Map<String, List<String>> dropIndexes = new HashMap<>();
    /** 更新表注释 */
    private final Map<String, String> modifyComments = new HashMap<>();
}
