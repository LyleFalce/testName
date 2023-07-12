package com.mofum.common.utils;

/**
 * SQL重写器
 */
public interface SQLRewriter {
    /**
     * 重写SQL
     *
     * @param sql sql语句
     * @return SQL语句
     */
    String rewrite(String sql);
}
