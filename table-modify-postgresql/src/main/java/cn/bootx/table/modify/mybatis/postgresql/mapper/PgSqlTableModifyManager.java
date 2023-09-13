package cn.bootx.table.modify.mybatis.postgresql.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 *
 * @author xxm
 * @since 2023/8/3
 */
@Repository
@RequiredArgsConstructor
public class PgSqlTableModifyManager {
    private final JdbcTemplate jdbcTemplate;


}
