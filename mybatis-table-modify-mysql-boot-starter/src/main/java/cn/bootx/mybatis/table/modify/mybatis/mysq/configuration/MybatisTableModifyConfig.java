package cn.bootx.mybatis.table.modify.mybatis.mysq.configuration;

import cn.bootx.mybatis.table.modify.mybatis.mysq.handler.MySqlTableHandlerService;
import cn.bootx.mybatis.table.modify.mybatis.mysq.handler.MysqlStartUpHandler;
import cn.bootx.mybatis.table.modify.mybatis.mysq.mapper.MySqlTableModifyMapper;
import cn.bootx.mybatis.table.modify.mybatis.mysq.service.*;
import cn.bootx.mybatis.table.modify.properties.MybatisTableModifyProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
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
@RequiredArgsConstructor
public class MybatisTableModifyConfig {

    @Bean
    @ConditionalOnMissingBean
    @ConfigurationProperties(prefix = "mybatis-table")
    public MybatisTableModifyProperties tableModifyProperties(){
        return new MybatisTableModifyProperties();
    }

    /**
     * 创建Bean并执行
     */
    @Bean
    @ConditionalOnMissingBean
    public MysqlStartUpHandler startUpHandler(
            MySqlTableHandlerService mySqlTableHandlerService,
            MybatisTableModifyProperties mybatisTableModifyProperties
    ) {
        MysqlStartUpHandler mysqlStartUpHandler = new MysqlStartUpHandler(mySqlTableHandlerService, mybatisTableModifyProperties);
        mysqlStartUpHandler.startHandler();
        return mysqlStartUpHandler;
    }
}
