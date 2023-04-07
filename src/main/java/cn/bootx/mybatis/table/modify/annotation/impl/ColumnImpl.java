package cn.bootx.mybatis.table.modify.annotation.impl;

import cn.bootx.mybatis.table.modify.annotation.Column;
import cn.bootx.mybatis.table.modify.impl.mysql.constants.MySqlFieldTypeEnum;
import cn.bootx.mybatis.table.modify.utils.ColumnUtils;

import java.lang.annotation.Annotation;

/**
 * 表字段注解默认实现
 * @author chenbin
 * @date 2020/12/7
 */
public class ColumnImpl implements Column {

    @Override
    public String value() {
        return "";
    }

    @Override
    public String name() {
        return "";
    }

    @Override
    public int order() {
        return 0;
    }

    @Override
    public int length() {
        return 255;
    }

    @Override
    public int decimalLength() {
        return 0;
    }

    @Override
    public boolean isNull() {
        return true;
    }

    @Override
    public boolean isKey() {
        return false;
    }

    @Override
    public boolean isAutoIncrement() {
        return false;
    }

    @Override
    public String defaultValue() {
        return ColumnUtils.DEFAULT_VALUE;
    }

    @Override
    public String comment() {
        return "";
    }

    @Override
    public boolean ignore() {
        return false;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return null;
    }

}
