package cn.bootx.mybatis.table.modify.domain;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 化用于存储各种操作表结构的容器
 * @author xxm
 * @date 2023/4/7
 */
@Getter
public class BaseTableMap {

    /** 1.用于存需要创建的表名+（字段结构/表信息） */
    private final Map<String, TableConfig> newTable = new HashMap<>();
    /**  2.用于存需要更新字段类型等的表名+结构 */
    private final Map<String, TableConfig> modifyTable = new HashMap<>();
    /** 3.用于存需要增加字段的表名+结构 */
    private final Map<String, TableConfig> addTable = new HashMap<>();
    /** 4.用于存需要删除字段的表名+结构 */
    private final Map<String, TableConfig> removeTable = new HashMap<>();
    /** 5.用于存需要删除主键的表名+结构 */
    private final Map<String, TableConfig> dropKeyTable = new HashMap<>();
    /** 6.用于存需要删除唯一约束的表名+结构 */
    private final Map<String, TableConfig> dropIndexAndUniqueTable = new HashMap<>();
    /** 7.用于存需要增加的索引 */
    private final Map<String, TableConfig> addIndexTable = new HashMap<>();
    /** 8.用于存需要增加的唯一约束 */
    private final Map<String, TableConfig> addUniqueTable = new HashMap<>();
    /** 9.更新表注释 */
    private final Map<String, TableConfig> modifyTableProperty = new HashMap<>();
}
