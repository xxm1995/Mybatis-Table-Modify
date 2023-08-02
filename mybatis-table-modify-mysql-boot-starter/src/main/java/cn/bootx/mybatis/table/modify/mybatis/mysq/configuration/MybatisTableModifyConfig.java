package cn.bootx.mybatis.table.modify.mybatis.mysq.configuration;

import cn.bootx.mybatis.table.modify.mybatis.mysq.handler.MySqlTableHandlerService;
import cn.bootx.mybatis.table.modify.mybatis.mysq.handler.MySqlStartUpHandler;
import cn.bootx.mybatis.table.modify.properties.MybatisTableModifyProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * 保证startUpHandler最优先执行
 * @author xxm
 * @date 2023/4/21
 */
@Order
@Configuration
@ConditionalOnProperty(prefix = "mybatis-table", value = "databaseType", havingValue = "postgresql")
public class MybatisTableModifyConfig {

    /**
     * 注入bean
     */
    @Bean
    @ConfigurationProperties(prefix = "mybatis-table")
    public MybatisTableModifyProperties tableModifyProperties(){
        return new MybatisTableModifyProperties();
    }

    /**
     * 创建Bean并执行
     */
    @Bean
    public MySqlStartUpHandler startUpHandler(
            MySqlTableHandlerService mySqlTableHandlerService,
            MybatisTableModifyProperties mybatisTableModifyProperties
    ) {
        MySqlStartUpHandler mysqlStartUpHandler = new MySqlStartUpHandler(mySqlTableHandlerService, mybatisTableModifyProperties);
        mysqlStartUpHandler.startHandler();
        return mysqlStartUpHandler;
    }
}
