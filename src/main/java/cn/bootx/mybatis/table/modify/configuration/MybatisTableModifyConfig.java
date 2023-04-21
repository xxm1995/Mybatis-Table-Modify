package cn.bootx.mybatis.table.modify.configuration;

import cn.bootx.mybatis.table.modify.handler.StartUpHandler;
import lombok.RequiredArgsConstructor;
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
    private final StartUpHandler startUpHandler;

    @Bean
    public void startUpHandler$startHandler() {
        startUpHandler.startHandler();
    }
}
