package cn.bootx.table.modify.mybatis.mysq.util;

import cn.bootx.table.modify.annotation.DbColumn;
import cn.bootx.table.modify.mybatis.mysq.annotation.DbMySqlFieldType;
import cn.bootx.table.modify.mybatis.mysq.annotation.DbMySqlIndex;
import cn.bootx.table.modify.mybatis.mysq.constants.MySql4JavaType;
import cn.bootx.table.modify.mybatis.mysq.entity.MySqlEntityColumn;
import cn.bootx.table.modify.mybatis.mysq.entity.MySqlTypeAndLength;
import cn.bootx.table.modify.utils.ClassUtils;
import cn.bootx.table.modify.utils.ColumnUtils;
import cn.hutool.core.util.StrUtil;
import lombok.experimental.UtilityClass;

import java.lang.reflect.Field;
import java.util.*;

/**
 *
 * @author xxm
 * @date 2023/4/14
 */
@UtilityClass
public class MySqlInfoUtil {
    /**
     * 迭代出所有entity的所有fields
     * @param clas 准备做为创建表依据的class
     * @return 表的全部字段
     */
    public List<MySqlEntityColumn> getEntityColumns(Class<?> clas) {
        List<MySqlEntityColumn> entityColumns = new ArrayList<>();
        Field[] fields = clas.getDeclaredFields();

        // 判断是否有父类，如果有拉取父类的field，这里支持多层继承
        fields = ClassUtils.recursionParents(clas, fields);

        // 遍历字段
        for (Field field : fields) {
            // 判断方法中是否有指定注解类型的注解
            if (ColumnUtils.hasColumn(field, clas)) {
                // 长度
                MySqlTypeAndLength mySqlTypeAndLength = getTypeAndLength(field, clas);
                // 注释 mysql 为空时转换为空字符串
                String comment = ColumnUtils.getComment(field, clas);
                comment = Objects.isNull(comment)?"":comment;

                MySqlEntityColumn entityColumn = new MySqlEntityColumn()
                        .setName(ColumnUtils.getColumnName(field, clas))
                        .setOrder(ColumnUtils.getColumnOrder(field, clas))
                        .setFieldType(mySqlTypeAndLength.getType().toLowerCase())
                        .setParamCount(mySqlTypeAndLength.getParamCount())
                        .setFieldIsNull(ColumnUtils.isNull(field, clas))
                        .setKey(ColumnUtils.isKey(field, clas))
                        .setAutoIncrement(ColumnUtils.isAutoIncrement(field, clas))
                        .setDefaultValue(ColumnUtils.getDefaultValue(field, clas))
                        .setDelete(ColumnUtils.isDelete(field,clas))
                        .setComment(comment);
                // 长度需要配置
                entityColumn.setLength(mySqlTypeAndLength.getLength());
                if (mySqlTypeAndLength.getParamCount() == 1) {
                    entityColumn.setLength(mySqlTypeAndLength.getLength());
                }
                // 小数也需要配置
                else if (mySqlTypeAndLength.getParamCount() == 2) {
                    entityColumn.setLength(mySqlTypeAndLength.getLength())
                            .setDecimalLength(mySqlTypeAndLength.getDecimalLength());
                }
                entityColumns.add(entityColumn);
            }
        }
        // 进行排序
        entityColumns.sort(Comparator.comparingInt(MySqlEntityColumn::getOrder));
        return new ArrayList<>(entityColumns);
    }

    /**
     * Mysql 类型和长度
     */
    private MySqlTypeAndLength getTypeAndLength(Field field, Class<?> clazz) {
        if (!ColumnUtils.hasColumn(field, clazz)) {
            throw new RuntimeException(clazz.getName()+" 中的字段名：" + field.getName() + "没有字段标识的注解，异常抛出！");
        }
        // 根据字段注解获取类型和长度配置
        DbMySqlFieldType dbMySqlFieldType = field.getAnnotation(DbMySqlFieldType.class);
        MySqlTypeAndLength typeAndLength;
        if (Objects.isNull(dbMySqlFieldType)){
            typeAndLength = MySql4JavaType.getTypeAndLength(field.getGenericType().toString());
        } else {
            typeAndLength = dbMySqlFieldType.value().getTypeAndLength();
        }

        if (Objects.isNull(typeAndLength)) {
            throw new RuntimeException(clazz.getName()+" 字段名：" + field.getName() + "不支持" + field.getGenericType()
                    + "类型转换到mysql类型，仅支持JavaToMysqlType类中的类型默认转换，异常抛出！");
        }

        // 处理字段注解是否有自定义的长度配置
        DbColumn column = ColumnUtils.getDbColumnAnno(field, clazz);
        if (Objects.isNull(column)) {
            typeAndLengthHandler(typeAndLength,255, 0);
        } else {
            typeAndLengthHandler(typeAndLength, column.length(),column.decimalLength() );
        }
        return typeAndLength;
    }

    /**
     * 构建 Mysql 类型和长度
     */
    private static void typeAndLengthHandler(MySqlTypeAndLength typeAndLength, int length, int decimalLength) {
        if (length != 255) {
            typeAndLength.setLength(length);
        }
        if (decimalLength != 0) {
            typeAndLength.setDecimalLength(decimalLength);
        }
    }

    /**
     * 构建括号参数
     * @param args 参数列表
     * @return (`x1`,`x2`)
     */
    public static String buildBracketParams(List<String> args){
        StringBuilder sb = new StringBuilder("(");
        for (String arg : args) {
            sb.append("`").append(arg).append("`,");
        }
        sb.delete(sb.length()-1,sb.length());
        sb.append(")");
        return sb.toString();
    }

    /**
     * 获取索引的名称，不设置则默认为索引类型+用_分隔的字段名计划
     */
    public static String getIndexName(DbMySqlIndex dbMySqlIndex, List<String> columns){
        if (StrUtil.isNotBlank(dbMySqlIndex.name())){
            return dbMySqlIndex.name();
        }
        return dbMySqlIndex.type().getPrefix()+String.join("_", columns);
    }
}
