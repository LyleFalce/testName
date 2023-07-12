package com.mofum.common.meta;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 范围列数据过滤器实现
 */
@Data
@NoArgsConstructor
public class ScopeColumnRule extends ColumnRule {

    /**
     * 数据库属性
     */
    public static final String TYPE_PROPERTIES = "properties";

    /**
     * 数据库列
     */
    public static final String TYPE_COLUMN = "column";

    /**
     * 列类型
     */
    private String type;

    public static boolean isColumn(ScopeColumnRule rule) {
        if (rule == null) {
            return false;
        }
        return TYPE_COLUMN.equalsIgnoreCase(rule.getType());
    }

    public static boolean isProperties(ScopeColumnRule rule) {
        if (rule == null) {
            return false;
        }
        return TYPE_PROPERTIES.equalsIgnoreCase(rule.getType());
    }

    public ScopeColumnRule(String ruleContent, String type) {
        super(ruleContent);
        this.type = type;
    }
}
