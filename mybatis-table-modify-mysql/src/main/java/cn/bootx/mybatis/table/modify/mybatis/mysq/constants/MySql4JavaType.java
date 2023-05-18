package cn.bootx.mybatis.table.modify.mybatis.mysq.constants;

import cn.bootx.mybatis.table.modify.mybatis.mysq.entity.MySqlTypeAndLength;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Mysql和Java类型映射关系
 *
 * @author sunchenbin
 * @date 2020/5/27
 */
public class MySql4JavaType {

    private static final Map<String, MySqlFieldTypeEnum> TYPE_MAP = new HashMap<>();
    static {
        TYPE_MAP.put("class java.lang.String", MySqlFieldTypeEnum.VARCHAR);
        TYPE_MAP.put("class java.lang.Long", MySqlFieldTypeEnum.BIGINT);
        TYPE_MAP.put("class java.lang.Integer", MySqlFieldTypeEnum.INT);
        TYPE_MAP.put("class java.lang.Boolean", MySqlFieldTypeEnum.BIT);
        TYPE_MAP.put("class java.math.BigInteger", MySqlFieldTypeEnum.BIGINT);
        TYPE_MAP.put("class java.lang.Float", MySqlFieldTypeEnum.FLOAT);
        TYPE_MAP.put("class java.lang.Double", MySqlFieldTypeEnum.DOUBLE);
        TYPE_MAP.put("class java.lang.Short", MySqlFieldTypeEnum.SMALLINT);
        TYPE_MAP.put("class java.math.BigDecimal", MySqlFieldTypeEnum.DECIMAL);
        TYPE_MAP.put("class java.sql.Date", MySqlFieldTypeEnum.DATE);
        TYPE_MAP.put("class java.util.Date", MySqlFieldTypeEnum.DATE);
        TYPE_MAP.put("class java.sql.Timestamp", MySqlFieldTypeEnum.DATETIME);
        TYPE_MAP.put("class java.sql.Time", MySqlFieldTypeEnum.TIME);
        TYPE_MAP.put("class java.time.LocalDateTime", MySqlFieldTypeEnum.DATETIME);
        TYPE_MAP.put("class java.time.LocalDate", MySqlFieldTypeEnum.DATE);
        TYPE_MAP.put("class java.time.LocalTime", MySqlFieldTypeEnum.TIME);
        TYPE_MAP.put("long", MySqlFieldTypeEnum.BIGINT);
        TYPE_MAP.put("int", MySqlFieldTypeEnum.INT);
        TYPE_MAP.put("boolean", MySqlFieldTypeEnum.BIT);
        TYPE_MAP.put("float", MySqlFieldTypeEnum.FLOAT);
        TYPE_MAP.put("double", MySqlFieldTypeEnum.DOUBLE);
        TYPE_MAP.put("short", MySqlFieldTypeEnum.SMALLINT);
        TYPE_MAP.put("char", MySqlFieldTypeEnum.VARCHAR);

    }

    /**
     * 获取类型
     * @param className 类型名称
     * @return MySQL类型信息
     */
    public static MySqlTypeAndLength getTypeAndLength(String className){
        MySqlFieldTypeEnum typeEnum = TYPE_MAP.get(className);
        if (Objects.isNull(typeEnum)){
            return null;
        }
        return new MySqlTypeAndLength(
                typeEnum.toString().toLowerCase(),
                typeEnum.getParamCount(),
                typeEnum.getLengthDefault(),
                typeEnum.getDecimalLengthDefault()
                );
    }

}
