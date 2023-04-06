package cn.bootx.mybatis.actable.handler;

import cn.bootx.mybatis.actable.constants.DatabaseType;

/**
 * 表结构信息修改
 * @author xxm
 * @date 2023/4/6
 */
public interface TableHandlerService {
    /**
     * 处理器类型
     */
    DatabaseType getDatabaseType();


    /**
     * 开始对表进行处理
     */
    void startModifyTable();
}
