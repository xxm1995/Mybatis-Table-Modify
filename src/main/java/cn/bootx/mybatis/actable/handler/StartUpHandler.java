package cn.bootx.mybatis.actable.handler;

import javax.annotation.PostConstruct;

import cn.bootx.mybatis.actable.configuration.ActableProperties;
import cn.bootx.mybatis.actable.constants.DatabaseType;
import cn.bootx.mybatis.actable.impl.mysql.service.MySqlTableHandlerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 启动时进行处理的实现类
 *
 * @author chenbin.sun
 *
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StartUpHandler {

    private final ActableProperties actableProperties;

    private final List<TableHandlerService> tableHandlerServices;

    /**
     * 建表开始
     */
    @PostConstruct
    public void startHandler() {
        // 获取配置信息
        DatabaseType databaseType = actableProperties.getDatabaseType();
        // 执行mysql的处理方法
        for (TableHandlerService tableHandlerService : tableHandlerServices) {
            if (tableHandlerService.getDatabaseType() == databaseType){
                tableHandlerService.startModifyTable();
            }
        }
    }

}
