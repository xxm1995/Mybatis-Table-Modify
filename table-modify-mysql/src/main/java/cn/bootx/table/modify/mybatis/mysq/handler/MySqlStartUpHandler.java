package cn.bootx.table.modify.mybatis.mysq.handler;

import cn.bootx.table.modify.constants.UpdateType;
import cn.bootx.table.modify.properties.MybatisTableModifyProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * 启动时进行处理的实现类
 *
 * @author chenbin.sun
 *
 */
@Slf4j
@RequiredArgsConstructor
public class MySqlStartUpHandler {

    private final MySqlTableHandlerService tableHandlerService;

    private final MybatisTableModifyProperties mybatisTableModifyProperties;

    /**
     * 建表开始
     */
    public void startHandler() {
        // 执行mysql的处理方法
        // 自动创建模式：update表示更新，create表示删除原表重新创建
        UpdateType updateType = mybatisTableModifyProperties.getUpdateType();

        // 不做任何事情
        if (Objects.isNull(updateType)) {
            log.warn("配置mybatis.table.updateType错误无法识别，当前配置只支持[none/update/create/add]三种类型!");
            return;
        }

        // 不做任何事情
        if (updateType == UpdateType.NONE) {
            return;
        }
        // 开始处理
        tableHandlerService.startModifyTable();
    }

}
