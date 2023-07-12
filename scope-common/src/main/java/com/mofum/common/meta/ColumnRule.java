package com.mofum.common.meta;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 标准数据过滤器实现
 */
@Data
@NoArgsConstructor
public class ColumnRule implements DataFilterRule {

    /**
     * token 连接
     */
    public static final String RULE_TOKEN_EMPTY = "";
    /**
     * token 连接
     */
    public static final String RULE_TOKEN_APPEND = "-";
    /**
     * token 查询
     */
    public static final String RULE_TOKEN_QUERY = "q";

    /**
     * token 新增
     */
    public static final String RULE_TOKEN_INSERT = "i";
    /**
     * token 删除
     */
    public static final String RULE_TOKEN_DELETE = "d";
    /**
     * token 更新
     */
    public static final String RULE_TOKEN_UPDATE = "u";

    /**
     * 列名
     */
    private String column;

    /**
     * 查询访问
     */
    private Boolean queryAccess = Boolean.FALSE;

    /**
     * 更新访问
     */
    private Boolean updateAccess = Boolean.FALSE;

    /**
     * 删除访问
     */
    private Boolean deleteAccess = Boolean.FALSE;

    /**
     * 新增插入访问
     */
    private Boolean insertAccess = Boolean.FALSE;

    public ColumnRule(String ruleContent) {
        this.parse(ruleContent);
    }

    @Override
    public void parse(String ruleContent) {
        if (StrUtil.isBlank(ruleContent)) {
            this.init();
            return;
        }
        List<String> tokens = StrUtil.split(ruleContent, RULE_TOKEN_APPEND);
        if (CollectionUtil.isEmpty(tokens) || tokens.size() == 1) {
            this.init();
            return;
        }
        doParseTokens(tokens);
    }

    private void doParseTokens(List<String> tokens) {
        if (tokens.size() > 2) {
            this.init();
            return;
        }
        this.column = tokens.stream().findFirst().get();
        String permission = tokens.stream().skip(1).findFirst().get();
        this.queryAccess = permission.contains(RULE_TOKEN_QUERY);
        this.insertAccess = permission.contains(RULE_TOKEN_INSERT);
        this.deleteAccess = permission.contains(RULE_TOKEN_DELETE);
        this.updateAccess = permission.contains(RULE_TOKEN_UPDATE);
    }

    private void init() {
        this.insertAccess = false;
        this.deleteAccess = false;
        this.updateAccess = false;
        this.queryAccess = false;
    }

    @Override
    public String ruleContent() {
        return column + RULE_TOKEN_APPEND + insertToken() + deleteToken() + updateToken() + queryToken();
    }

    private String insertToken() {
        return insertAccess ? RULE_TOKEN_INSERT : RULE_TOKEN_EMPTY;
    }

    private String updateToken() {
        return updateAccess ? RULE_TOKEN_UPDATE : RULE_TOKEN_EMPTY;
    }

    private String deleteToken() {
        return deleteAccess ? RULE_TOKEN_DELETE : RULE_TOKEN_EMPTY;
    }

    private String queryToken() {
        return queryAccess ? RULE_TOKEN_QUERY : RULE_TOKEN_EMPTY;
    }


    @Override
    public boolean updateAccess() {
        return updateAccess;
    }

    @Override
    public boolean queryAccess() {
        return queryAccess;
    }

    @Override
    public boolean deleteAccess() {
        return deleteAccess;
    }

    @Override
    public boolean insertAccess() {
        return insertAccess;
    }

    @Override
    public boolean access() {
        return insertAccess || deleteAccess || updateAccess || queryAccess;
    }
}
