package cn.bootx.table.modify.mybatis.mysq.mapper;

import cn.bootx.table.modify.mybatis.mysq.entity.*;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 *
 * @author xxm
 * @since 2023/8/3
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class MySqlTableModifyDao {
    private final JdbcTemplate jdbcTemplate;

    /**
     * 新建表
     */
    @SuppressWarnings("SqlSourceToSinkFlow")
    public void createTable(MySqlCreateParam param){
        StringBuilder sql = new StringBuilder();
        sql.append(tableName(param));
        sql.append("(");
        sql.append(column(param));
        sql.append(primaryKey(param));
        sql.append(index(param));
        // 去除最后的逗号
        sql.deleteCharAt(sql.length()-1);
        sql.append(")");
        sql.append(engine(param));
        sql.append(charset(param));
        sql.append(comment(param));
        log.info(sql.toString());
        jdbcTemplate.execute(sql.toString());
    }

    /**
     * 表名
     */
    private String tableName(MySqlCreateParam param){
        return StrUtil.format("create table `{}`",param.getName());
    }

    /**
     * 行
     */
    private String column(MySqlCreateParam param){
        return String.join(",", param.getColumns())+",";
    }
    /**
     * 主键索引
     */
    private String primaryKey(MySqlCreateParam param){
        if (StrUtil.isNotBlank(param.getKeys())){
            return StrUtil.format("PRIMARY KEY {} USING BTREE,",param.getKeys());
        }
        return "";
    }

    /**
     * 索引
     */
    private String index(MySqlCreateParam param){
        if (CollUtil.isNotEmpty(param.getIndexes())) {
            String string = String.join(",", param.getIndexes());
            return string + ",";
        }
        return "";
    }

    /**
     * 表信息 引擎
     */
    private String engine(MySqlCreateParam param){
        if (StrUtil.isNotBlank(param.getEngine())){
            return StrUtil.format("ENGINE = {} ",param.getEngine());
        }
        return "";
    }

    /**
     * 表信息 字符集
     */
    private String charset(MySqlCreateParam param){
        if (StrUtil.isNotBlank(param.getCharset())){
            return StrUtil.format("CHARSET = {} ",param.getCharset());
        }
        return "";
    }

    /**
     * 表信息 备注
     */
    private String comment(MySqlCreateParam param){
        if (StrUtil.isNotBlank(param.getComment())){
            return StrUtil.format("COMMENT = '{}' ",param.getComment());
        }
        return "";
    }

    /**
     * 更新表
     */
    @SuppressWarnings("SqlSourceToSinkFlow")
    public void modifyTable(MySqlModifyParam param){
        StringBuilder sql = new StringBuilder();
        sql.append(tableName(param));
        sql.append(dropColumn(param));
        sql.append(updateColumn(param));
        sql.append(addColumn(param));
        sql.append(primaryKey(param));
        sql.append(dropIndex(param));
        sql.append(addIndex(param));
        sql.append(engine(param));
        sql.append(charset(param));
        sql.append(comment(param));
        sql.deleteCharAt(sql.length()-1);
        sql.append(";");
        log.info(sql.toString());
        jdbcTemplate.execute(sql.toString());
    }

    /**
     * 表名
     */
    private String tableName(MySqlModifyParam param){
        return StrUtil.format("ALTER TABLE `{}`",param.getName());
    }

    /**
     * 删除行
     */
    private String dropColumn(MySqlModifyParam param){
        StringBuilder sb = new StringBuilder();
        for (String dropColumn : param.getDropColumns()) {
            sb.append(StrUtil.format("DROP COLUMN {}, ",dropColumn));
        }
        return sb.toString();
    }

    /**
     * 更新行
     */
    private String updateColumn(MySqlModifyParam param){
        StringBuilder sb = new StringBuilder();
        for (String column : param.getModifyColumns()) {
            sb.append(StrUtil.format("MODIFY COLUMN {}, ",column));
        }
        return sb.toString();
    }

    /**
     * 添加行
     */
    private String addColumn(MySqlModifyParam param){
        StringBuilder sb = new StringBuilder();
        for (String column : param.getAddColumns()) {
            sb.append(StrUtil.format("ADD COLUMN {},",column));
        }
        return sb.toString();
    }

    /**
     * 主键索引
     */
    private String primaryKey(MySqlModifyParam param){
        if (StrUtil.isNotBlank(param.getKeys())){
            return StrUtil.format("DROP PRIMARY KEY, " +
                    "PRIMARY KEY {} USING BTREE, ",param.getKeys());
        }
        return "";
    }

    /**
     * 删除索引
     */
    private String dropIndex(MySqlModifyParam param){
        StringBuilder sb = new StringBuilder();
        for (String dropIndex : param.getDropIndexes()) {
            sb.append(StrUtil.format("DROP INDEX `{}`, ",dropIndex));
        }
        return sb.toString();
    }

    /**
     * 新增索引
     */
    private String addIndex(MySqlModifyParam param){
        StringBuilder sb = new StringBuilder();
        for (String addIndex : param.getAddIndexes()) {
            sb.append(StrUtil.format("ADD {},",addIndex));
        }
        return sb.toString();
    }

    /**
     * 表信息 引擎
     */
    private String engine(MySqlModifyParam param){
        if (StrUtil.isNotBlank(param.getEngine())){
            return StrUtil.format("ENGINE = {}, ",param.getEngine());
        }
        return "";
    }

    /**
     * 表信息 字符集
     */
    private String charset(MySqlModifyParam param){
        if (StrUtil.isNotBlank(param.getCharset())){
            return StrUtil.format("CHARSET = {}, ",param.getCharset());
        }
        return "";
    }

    /**
     * 表信息 备注
     */
    private String comment(MySqlModifyParam param){
        if (StrUtil.isNotBlank(param.getComment())){
            return StrUtil.format("COMMENT = '{},' ",param.getComment());
        }
        return "";
    }

    /**
     * 查据表名询表信息
     * @param tableName 表结构的map
     * @return MySqlTableInfo
     */
    public boolean existsByTableName(String tableName){
        String sql = "select count(*) from information_schema.tables " +
                "   where table_name = ? and table_schema = (select database())";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, new SingleColumnRowMapper<>(Boolean.class), tableName));
    }


    /**
     * 查据表名询表信息
     * @param tableName 表结构的map
     * @return MySqlTableInfo
     */
    public MySqlTableInfo findTableByTableName(String tableName){
        String sql = "SELECT " +
                "    `table_catalog` as tableCatalog, " +
                "    `table_schema` as tableSchema, " +
                "    `table_name` as tableName, " +
                "    `table_type` as tableType, " +
                "    `engine` as engine, " +
                "    `version` as version, " +
                "    `row_format` as rowFormat, " +
                "    `table_rows` as tableRows, " +
                "    `avg_row_length` as avgRowLength, " +
                "    `data_length` as dataLength, " +
                "    `max_data_length` as maxDataLength, " +
                "    `index_length` as indexLength, " +
                "    `data_free` as dataFree, " +
                "    `auto_increment` as autoIncrement, " +
                "    `create_time` as createTime, " +
                "    `update_time` as updateTime, " +
                "    `check_time` as checkTime, " +
                "    `table_collation` as tableCollation, " +
                "    `checksum` as checksum, " +
                "    `create_options` as createOptions, " +
                "    `table_comment` as tableComment " +
                "FROM information_schema.TABLES " +
                "WHERE table_schema = (select database()) and table_name = ?";
        return jdbcTemplate.queryForObject(sql,new BeanPropertyRowMapper<>(MySqlTableInfo.class),tableName);
    }

    /**
     * 根据表名查询库中该表的字段结构等信息
     * @param tableName 表结构的map
     * @return 表的字段结构等信息
     */
    public List<MysqlTableColumn> findColumnByTableName(String tableName){
        String sql = "SELECT " +
                "    `table_schema` AS tableSchema, " +
                "    `table_name` as tableName, " +
                "    `column_name` as columnName, " +
                "    `ordinal_position` as ordinalPosition, " +
                "    `column_default` as columnDefault, " +
                "    `is_nullable` as isNullable, " +
                "    `data_type` as dataType, " +
                "    `character_maximum_length` as characterMaximumLength, " +
                "    `character_octet_length` as characterOctetLength, " +
                "    `numeric_precision` as numericPrecision, " +
                "    `numeric_scale` as numericScale, " +
                "    `character_set_name` as characterSetName, " +
                "    `collation_name` as collationName, " +
                "    `column_type` as columnType, " +
                "    `column_key` as columnKey, " +
                "    `extra` as extra, " +
                "    `privileges` as privileges, " +
                "    `column_comment` as columnComment " +
                "FROM information_schema.COLUMNS  " +
                "WHERE table_schema = (select database()) and table_name = ?";
        return jdbcTemplate.query(sql,new BeanPropertyRowMapper<>(MysqlTableColumn.class),tableName);
    }

    /**
     * 查询当前表存在的索引(除了主键索引primary)
     * @param tableName 表名
     * @return 索引名列表
     */
    public List<MySqlTableIndex> findIndexByTableName(String tableName){
        String sql = "SELECT " +
                "`index_name` as indexName, " +
                "`index_type` as indexType, " +
                "`index_comment` as indexComment, " +
                "`column_name` as columnName, " +
                "`non_unique` as nonUnique, " +
                "`seq_in_index` as seqInIndex  " +
                "FROM information_schema.statistics  " +
                "WHERE table_schema = ( SELECT DATABASE ())  " +
                "AND lower( index_name ) != 'primary'  " +
                "AND table_name = ?";
        return jdbcTemplate.query(sql,new BeanPropertyRowMapper<>(MySqlTableIndex.class),tableName);
    }


    /**
     * 查询当前表存在的主键索引
     * @param tableName 表名
     * @return 索引名列表
     */
    public List<MySqlTableIndex> findPrimaryIndexByTableName(String tableName){
        String sql = "SELECT " +
                "`index_name` as indexName, " +
                "`index_type` as indexType, " +
                "`index_comment` as indexComment, " +
                "`column_name` as columnName, " +
                "`non_unique` as nonUnique, " +
                "`seq_in_index` as seqInIndex  " +
                "FROM information_schema.statistics  " +
                "WHERE table_schema = ( SELECT DATABASE ())  " +
                "AND lower( index_name ) = 'primary'  " +
                "AND table_name = ?";
        return jdbcTemplate.query(sql,new BeanPropertyRowMapper<>(MySqlTableIndex.class),tableName);
    }

    /**
     * 根据表名删除表
     * @param tableName 表名
     */
    @SuppressWarnings("SqlSourceToSinkFlow")
    public void dropTableByName(String tableName){
        String sql = StrUtil.format("DROP TABLE IF EXISTS `{}`;",tableName);
        jdbcTemplate.execute(sql);
    }
}
