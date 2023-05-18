package cn.bootx.mybatis.table.modify.mybatis.mysq.mapper;

import java.util.List;

import cn.bootx.mybatis.table.modify.impl.mysql.entity.*;
import cn.bootx.mybatis.table.modify.domain.TableConfig;
import cn.bootx.mybatis.table.modify.mybatis.mysq.entity.*;
import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 创建更新表结构的Mapper
 *
 * @author sunchenbin
 *
 */
@Mapper
@InterceptorIgnore(tenantLine = "true")
public interface MySqlTableModifyMapper {

    /**
     * 根据结构注解解析出来的信息创建表
     * @param table 创建表的内容
     */
    void createTable(@Param("table") MySqlCreateParam table);

    /**
     * 根据表结构和注解解析出来的信息进行更新表
     * @param table 创建表的内容
     */
    void modifyTable(@Param("table") MySqlModifyParam table);

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
    List<MysqlTableColumn> findColumnByTableName(@Param("tableName") String tableName);

    /**
     * 查询当前表存在的索引(除了主键索引primary)
     * @param tableName 表名
     * @return 索引名列表
     */
    List<MySqlTableIndex> findIndexByTableName(@Param("tableName") String tableName);

    /**
     * 查询当前表存在的主键索引
     * @param tableName 表名
     * @return 索引名列表
     */
    List<MySqlTableIndex> findPrimaryIndexByTableName(@Param("tableName") String tableName);


    /**
     * 根据表名删除表
     * @param tableName 表名
     */
    void dropTableByName(@Param("tableName") String tableName);

}
