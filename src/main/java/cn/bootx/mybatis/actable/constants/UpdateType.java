package cn.bootx.mybatis.actable.constants;

/**
 * 更新模式
 *
 * @author xxm
 * @date 2023/1/16
 */
public enum UpdateType {

    /** 不进行任何操作 */
    NONE,
    /** 更新表结构 */
    UPDATE,
    /** 创建表, 已经创建的不再进行处理 */
    CREATE,
    /** 删除后重新创建 */
    DROP_CREATE;

}
