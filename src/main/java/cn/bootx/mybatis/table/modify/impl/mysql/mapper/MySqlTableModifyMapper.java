package cn.bootx.mybatis.table.modify.impl.mysql.mapper;

import java.util.List;
import java.util.Map;

import cn.bootx.mybatis.table.modify.impl.mysql.entity.MySqlTableIndexInfo;
import cn.bootx.mybatis.table.modify.impl.mysql.entity.MySqlTableInfo;
import cn.bootx.mybatis.table.modify.impl.mysql.entity.MysqlTableColumnInfo;
import cn.bootx.mybatis.table.modify.domain.TableConfig;
import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import org.apache.ibatis.annotations.Param;

/**
 * 创建更新表结构的Mapper
 *
 * @author sunchenbin
 *
 */
@InterceptorIgnore(tenantLine = "true")
public interface MySqlTableModifyMapper {

    /**
     * 根据结构注解解析出来的信息创建表
     * @param tableMap 表结构的map
     */
    void createTable(@Param("tableMap") Map<String, TableConfig> tableMap);

    /**
     * 根据表名查询表在库中是否存在
     * @param tableName 表结构的map
     * @return 是否存在
     */
    boolean existsByTableName(@Param("tableName") String tableName);

    /**
     * 查据表名询表信息
     * @param tableName 表结构的map
     * @return MySqlTableInfo
     */
    MySqlTableInfo findTableByTableName(@Param("tableName") String tableName);

    /**
     * 根据表名查询库中该表的字段结构等信息
     * @param tableName 表结构的map
     * @return 表的字段结构等信息
     */
    List<MysqlTableColumnInfo> findColumnByTableName(@Param("tableName") String tableName);

    /**
     * 查询当前表存在的索引(除了主键索引primary)
     * @param tableName 表名
     * @return 索引名列表
     */
    List<MySqlTableIndexInfo> findIndexByTableName(@Param("tableName") String tableName);

    /**
     * 增加字段
     * @param tableMap 表结构的map
     */
    void addTableField(@Param("tableMap") Map<String, Object> tableMap);

    /**
     * 删除字段
     * @param tableMap 表结构的map
     */
    void removeTableField(@Param("tableMap") Map<String, Object> tableMap);

    /**
     * 更新表属性
     * @param tableMap 表结构的map
     */
    void modifyTableProperty(@Param("tableMap") Map<String, TableConfig> tableMap);

    /**
     * 修改字段
     * @param tableMap 表结构的map
     */
    void modifyTableField(@Param("tableMap") Map<String, Object> tableMap);

    /**
     * 删除主键约束，附带修改其他字段属性功能
     * @param tableMap 表结构的map
     */
    void dropKeyTableField(@Param("tableMap") Map<String, Object> tableMap);

    /**
     * 根据表名删除表
     * @param tableName 表名
     */
    void dropTableByName(@Param("tableName") String tableName);

    /**
     * 删除表索引
     * @param tableMap
     */
    void dropTableIndex(@Param("tableMap") Map<String, Object> tableMap);

    /**
     * 创建索引
     * @param tableMap
     */
    void addTableIndex(@Param("tableMap") Map<String, Object> tableMap);

    /**
     * 创建唯一约束
     * @param tableMap
     */
    void addTableUnique(@Param("tableMap") Map<String, Object> tableMap);

}
