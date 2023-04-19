package cn.bootx.mybatis.table.modify.impl.mysql.entity;

import lombok.Getter;

import java.util.ArrayList;
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
    private final List<MySqlEntityTable> createTables = new ArrayList<>();
    /** 用于存需要更新的表名+（字段结构/表信息） */
    private final List<MySqlTableUpdate> updateTables = new ArrayList<>();
    /** 用于存需要增加的表名+字段列表 */
    private final Map<String, List<MySqlEntityColumn>> addColumns = new HashMap<>();
    /** 用于存需要修改的表名+字段列表 */
    private final Map<String, List<MySqlEntityColumn>> updateColumns = new HashMap<>();
    /** 用于存需要删除的表名+字段列表 */
    private final Map<String, List<String>> dropColumns = new HashMap<>();
    /** 用于存需要增加的表名+索引列表 */
    private final Map<String, List<MySqlEntityIndex>> addIndexes = new HashMap<>();
    /** 用于存需要更新的表名+索引列表 */
    private final Map<String, List<MySqlEntityIndex>> updateIndexes = new HashMap<>();
    /** 用于存需要删除的表名+索引列表 */
    private final Map<String, List<String>> dropIndexes = new HashMap<>();
}
