package com.mofum.common.env;

import cn.hutool.core.util.StrUtil;
import com.mofum.common.meta.SID;

import java.util.stream.Collectors;

/**
 * 环境检测
 */
public class EnvCheck {
    /**
     * 判断集合中是否有其值
     *
     * @param value 值
     * @return true有 false无
     */
    public static boolean containValue(Object value) {
        return Envs.scopeCollections().stream().map(SID::getValue).collect(Collectors.toList()).contains(value);
    }

    /**
     * 判断集合中是否拥有值和范围类型
     *
     * @param value 值
     * @param scope 范围类型
     * @return true有 false无
     */
    public static boolean containValueScope(Object value, String scope) {
        return Envs.scopeCollections().stream().filter(e -> e.getScope() != null).filter(e -> e.getValue().equals(value) && e.getScope().equals(scope)).collect(Collectors.toList()).size() > 0;
    }

    /**
     * 判断集合中是否拥有值和表名
     *
     * @param value  值
     * @param schema 表名
     * @return true有 false无
     */
    public static boolean containValueSchema(Object value, String schema) {
        return Envs.scopeCollections().stream().filter(e -> e != null).filter(e -> e.getValue() != null && e.getValue().equals(value)).filter(e -> e.getSchema() != null && e.getSchema().getName() != null).filter(e -> e.getSchema().getName().equals(schema)).collect(Collectors.toList()).size() > 0;
    }

    /**
     * 判断集合中是否拥有值和表名和表别名
     *
     * @param value  值
     * @param schema 表名
     * @param alias  表别名
     * @return true有 false无
     */
    public static boolean containValueSchema(Object value, String schema, String alias) {
        return Envs.scopeCollections().stream().filter(e -> e != null).filter(e -> e.getSchema() != null).filter(e -> e.getSchema().getName() != null && e.getSchema().getAlias() != null).filter(e -> e.getValue() != null && e.getValue().equals(value)).filter(e -> e.getSchema().getName().equals(schema) && e.getSchema().getAlias().equals(alias)).collect(Collectors.toList()).size() > 0;
    }

    /**
     * 判断集合中是否拥有值和列名
     *
     * @param value  值
     * @param column 列名
     * @return true有 false无
     */
    public static boolean containValueSidColumn(Object value, String column) {
        return Envs.scopeCollections().stream().filter(e -> StrUtil.isNotBlank(e.getColumn())).filter(e -> e != null).filter(e -> e.getValue() != null && e.getValue().equals(value)).filter(e -> e.getColumn() != null && e.getColumn().equals(column)).collect(Collectors.toList()).size() > 0;
    }
}
