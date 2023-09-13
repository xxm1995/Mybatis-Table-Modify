package cn.bootx.table.modify.utils;

import cn.hutool.core.util.ClassUtil;
import lombok.experimental.UtilityClass;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 类工具类
 * @author xxm
 * @date 2023/5/30
 */
@UtilityClass
public class ClassUtils {


    /**
     * 获取字段属性
     */
    public Field getField(Class<?> clas,String fieldName){
        // 查询是否有该属性
        Field field = ClassUtil.getDeclaredField(clas, fieldName);
        if (Objects.nonNull(field)){
            return field;
        }
        //noinspection DataFlowIssue
        if (Objects.nonNull(clas.getSuperclass())){
            return getField(clas,fieldName);
        }
        return null;
    }

    /**
     * 递归扫描父类的fields
     * @param clas 类
     * @param fields 属性
     */
    public Field[] recursionParents(Class<?> clas, Field[] fields) {
        if (Objects.nonNull(clas.getSuperclass())) {
            Class<?> clsSup = clas.getSuperclass();
            List<Field> fieldList = new ArrayList<>(Arrays.asList(fields));
            // 获取当前class的所有fields的name列表
            List<String> fdNames = fieldList.stream().map(Field::getName).collect(Collectors.toList());
            for (Field pfd : clsSup.getDeclaredFields()) {
                // 避免重载属性
                if (fdNames.contains(pfd.getName())) {
                    continue;
                }
                fieldList.add(pfd);
            }
            fields = new Field[fieldList.size()];
            int i = 0;
            for (Object field : fieldList.toArray()) {
                fields[i] = (Field) field;
                i++;
            }
            fields = recursionParents(clsSup, fields);
        }
        return fields;
    }
}
