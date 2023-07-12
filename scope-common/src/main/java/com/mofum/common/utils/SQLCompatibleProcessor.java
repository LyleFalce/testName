package com.mofum.common.utils;

/**
 * SQL兼容处理器
 */
public interface SQLCompatibleProcessor {
    /**
     * SQL兼容处理器
     *
     * @param sql sql or sql对象
     * @param <S> 任意SQL类型
     * @return sql对象
     */
    <S> S compatible(S sql);
}
