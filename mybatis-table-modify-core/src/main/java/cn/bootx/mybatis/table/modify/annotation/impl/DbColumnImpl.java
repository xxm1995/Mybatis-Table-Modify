package cn.bootx.mybatis.table.modify.annotation.impl;

import cn.bootx.mybatis.table.modify.annotation.DbColumn;
import cn.bootx.mybatis.table.modify.utils.ColumnUtils;

import java.lang.annotation.Annotation;

/**
 * 表字段注解默认实现
 * @author chenbin
 * @date 2020/12/7
 */
public class DbColumnImpl implements DbColumn {

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
        return "";
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
