package cn.bootx.mybatis.table.modify.mybatis.mysq.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 引擎类型
 * @author xxm
 * @date 2023/4/18
 */
@Getter
@AllArgsConstructor
public enum MySqlEngineEnum {
    DEFAULT(null),
    ARCHIVE("ARCHIVE"),
    BLACKHOLE("BLACKHOLE"),
    CSV("CSV"),
    InnoDB("InnoDB"),
    MEMORY("MEMORY"),
    MRG_MYISAM("MRG_MYISAM"),
    MyISAM("MyISAM"),
    PERFORMANCE_SCHEMA("PERFORMANCE_SCHEMA");

    private final String value;

}
