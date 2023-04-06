package cn.bootx.mybatis.actable.impl.mysql.constants;

import java.util.HashMap;
import java.util.Map;

/**
 * Mysql和Java类型映射关系
 *
 * @author sunchenbin
 * @date 2020/5/27
 */
public class MySql4JavaType {

    private static final Map<String, MySqlFieldType> TYPE_MAP = new HashMap<>();
    static {
        TYPE_MAP.put("class java.lang.String", MySqlFieldType.VARCHAR);
        TYPE_MAP.put("class java.lang.Long", MySqlFieldType.BIGINT);
        TYPE_MAP.put("class java.lang.Integer", MySqlFieldType.INT);
        TYPE_MAP.put("class java.lang.Boolean", MySqlFieldType.BIT);
        TYPE_MAP.put("class java.math.BigInteger", MySqlFieldType.BIGINT);
        TYPE_MAP.put("class java.lang.Float", MySqlFieldType.FLOAT);
        TYPE_MAP.put("class java.lang.Double", MySqlFieldType.DOUBLE);
        TYPE_MAP.put("class java.lang.Short", MySqlFieldType.SMALLINT);
        TYPE_MAP.put("class java.math.BigDecimal", MySqlFieldType.DECIMAL);
        TYPE_MAP.put("class java.sql.Date", MySqlFieldType.DATE);
        TYPE_MAP.put("class java.util.Date", MySqlFieldType.DATE);
        TYPE_MAP.put("class java.sql.Timestamp", MySqlFieldType.DATETIME);
        TYPE_MAP.put("class java.sql.Time", MySqlFieldType.TIME);
        TYPE_MAP.put("class java.time.LocalDateTime", MySqlFieldType.DATETIME);
        TYPE_MAP.put("class java.time.LocalDate", MySqlFieldType.DATE);
        TYPE_MAP.put("class java.time.LocalTime", MySqlFieldType.TIME);
        TYPE_MAP.put("long", MySqlFieldType.BIGINT);
        TYPE_MAP.put("int", MySqlFieldType.INT);
        TYPE_MAP.put("boolean", MySqlFieldType.BIT);
        TYPE_MAP.put("float", MySqlFieldType.FLOAT);
        TYPE_MAP.put("double", MySqlFieldType.DOUBLE);
        TYPE_MAP.put("short", MySqlFieldType.SMALLINT);
        TYPE_MAP.put("char", MySqlFieldType.VARCHAR);
    }

    /**
     * 获取类型
     * @param className 类名称
     * @return MySQL类型信息
     */
    public static MySqlFieldType get(String className) {
        return TYPE_MAP.get(className);
    }

}
