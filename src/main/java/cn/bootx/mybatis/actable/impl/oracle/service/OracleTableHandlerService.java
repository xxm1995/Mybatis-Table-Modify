package cn.bootx.mybatis.actable.impl.oracle.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import cn.bootx.mybatis.actable.constants.Constants;
import cn.bootx.mybatis.actable.utils.ConfigurationUtil;

/**
 * 项目启动时自动扫描配置的目录中的model，根据配置的规则自动创建或更新表 该逻辑只适用于mysql，其他数据库尚且需要另外扩展，因为sql的语法不同
 *
 * @author sunchenbin
 * @version 2016年6月23日 下午5:58:12
 */
@Slf4j
@Component
@Transactional
@RequiredArgsConstructor
public class OracleTableHandlerService {

    private final ConfigurationUtil springContextUtil;

    /**
     * 要扫描的model所在的pack
     */
    private String pack;

    /**
     * 自动创建模式：update表示更新，create表示删除原表重新创建
     */
    private String tableAuto;

    /**
     * 读取配置文件的三种状态（创建表、更新表、不做任何事情）
     */
    public void createOracleTable() {
        // 读取配置信息
        pack = springContextUtil.getConfig(Constants.MODEL_PACK_KEY);
        tableAuto = springContextUtil.getConfig(Constants.TABLE_AUTO_KEY);

        // 不做任何事情
        if ("none".equals(tableAuto)) {
            log.info("配置mybatis.table.auto=none，不需要做任何事情");
            return;
        }

        // TODO: 暂时还没有写
    }

}
