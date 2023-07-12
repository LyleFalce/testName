package com.mofum.common.env;

import com.mofum.common.meta.ScopeColumnRule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 环境过滤规则类
 */
public final class EnvsRule {
    private EnvsRule() {
        throw new RuntimeException("This is static class.");
    }

    /**
     * 收集来的范围列规则
     *
     * @return 范围列规则集合
     */
    public static List<ScopeColumnRule> rules() {
        return Optional.ofNullable(Runtime.THREAD_ENV.get().getScopeFilterRules()).orElse(new ArrayList<>());
    }

    /**
     * 列规则集合
     *
     * @return 列规则集合
     */
    public static List<ScopeColumnRule> columnRules() {
        return rules().stream().filter(ScopeColumnRule::isColumn).collect(Collectors.toList());
    }

    /**
     * 属性规则集合
     *
     * @return 属性规则集合
     */
    public static List<ScopeColumnRule> propertiesRules() {
        return rules().stream().filter(ScopeColumnRule::isProperties).collect(Collectors.toList());
    }

    /**
     * 添加范围列规则
     *
     * @param rule 范围列规则
     */
    public static void addColumnRule(String... rule) {
        List<ScopeColumnRule> ruleList = Arrays.stream(rule).map(e -> new ScopeColumnRule(e, ScopeColumnRule.TYPE_COLUMN)).collect(Collectors.toList());
        addAllRule(ruleList);
    }

    /**
     * 添加范围列规则
     *
     * @param rule 范围列规则
     */
    public static void addPropertiesRule(String... rule) {
        List<ScopeColumnRule> ruleList = Arrays.stream(rule).map(e -> new ScopeColumnRule(e, ScopeColumnRule.TYPE_PROPERTIES)).collect(Collectors.toList());
        addAllRule(ruleList);
    }

    /**
     * 添加范围列规则
     *
     * @param rule 范围列规则
     */
    public static void addRule(ScopeColumnRule... rule) {
        List<ScopeColumnRule> rules = rules();
        rules.addAll(Arrays.asList(rule));
        Runtime.THREAD_ENV.get().setScopeFilterRules(rules);
    }

    /**
     * 添加多个范围列规则
     *
     * @param rule 范围列规则
     */
    public static void addAllRule(List<ScopeColumnRule> rule) {
        List<ScopeColumnRule> rules = rules();
        rules.addAll(rule);
        Runtime.THREAD_ENV.get().setScopeFilterRules(rules);
    }
}
