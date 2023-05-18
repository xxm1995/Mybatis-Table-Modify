package cn.bootx.mybatis.table.modify.constants;

/**
 * 更新模式
 *
 * @author xxm
 * @date 2023/1/16
 */
public enum UpdateType {

    /** 不进行任何操作 */
    NONE,
    /** 创建和更新表结构 */
    UPDATE,
    /** 创建表, 不做删除和修改操作, 只会添加新的字段和索引, 不太推荐使用*/
    CREATE,
    /** 删除后重新创建 */
    DROP_CREATE;

}
