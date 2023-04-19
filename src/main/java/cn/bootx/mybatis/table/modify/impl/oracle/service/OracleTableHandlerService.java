package cn.bootx.mybatis.table.modify.impl.oracle.service;

import cn.bootx.mybatis.table.modify.constants.DatabaseType;
import cn.bootx.mybatis.table.modify.handler.TableHandlerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 项目启动时自动扫描配置的目录中的entity，根据配置的规则自动创建或更新表 该逻辑只适用于mysql，其他数据库尚且需要另外扩展，因为sql的语法不同
 *
 * @author sunchenbin
 * @version 2016年6月23日 下午5:58:12
 */
@Slf4j
@Component
@Transactional
@RequiredArgsConstructor
public class OracleTableHandlerService implements TableHandlerService {

    /**
     * 处理器类型
     */
    @Override
    public DatabaseType getDatabaseType() {
        return DatabaseType.ORACLE;
    }

    /**
     * 开始对表进行处理
     */
    @Override
    public void startModifyTable() {

    }
}
