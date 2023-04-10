package cn.bootx.mybatis.table.modify.domain;

import java.util.List;

public class IndexParam {


    /**
     * 唯一约束名称
     */
    @Deprecated
    private String filedUniqueName;

    /**
     * 唯一约束列表
     */
    @Deprecated
    private List<String> filedUniqueValue;

    /**
     * 索引名称
     */
    @Deprecated
    private String filedIndexName;

    /**
     * 所有字段列表
     */
    @Deprecated
    private List<String> filedIndexValue;
}
