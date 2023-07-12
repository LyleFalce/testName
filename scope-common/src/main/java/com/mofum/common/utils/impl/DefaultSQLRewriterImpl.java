package com.mofum.common.utils.impl;

import com.mofum.common.utils.SQLRewriter;

import java.util.regex.Pattern;

/**
 * 默认SQL重写器
 */
public class DefaultSQLRewriterImpl implements SQLRewriter {
    /**
     * clean SQL中的注释信息
     *
     * @param sql SQL语句
     * @return 无注释信息的SQL
     */
    @Override
    public String rewrite(final String sql) {
        Pattern p = Pattern.compile("(?ms)('(?:''|[^'])*')|--.*?$|/\\*.*?\\*/|#.*?$|");
        String newResult = p.matcher(sql).replaceAll("$1");
        return newResult.replaceAll("(?s)<\\!\\-\\-.+?\\-\\->", "");
    }
}
