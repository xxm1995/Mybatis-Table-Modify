package cn.bootx.mybatis.table.modify.properties;

import cn.bootx.mybatis.table.modify.constants.DatabaseType;
import cn.bootx.mybatis.table.modify.constants.UpdateType;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.Delegate;

/**
 * @author xxm
 * @date 2023/1/16
 */
@Data
@Accessors(chain = true)
public class MybatisTableModifyProperties {

    /**
     * 数据库类型
     */
    @Delegate
    private DatabaseType databaseType = DatabaseType.MYSQL;

    /**
     * 更新模式
     */
    private UpdateType updateType = UpdateType.NONE;

    /**
     * 扫描包路径, 可以用 ,和 ; 分隔
     */
    private String scanPackage;

}
