package com.mofum.common.utils.impl;

import com.mofum.common.utils.SQLCompatibleProcessor;

/**
 * 默认SQL兼容处理器
 */
public class DefaultSQLCompatibleProcessor implements SQLCompatibleProcessor {
    /**
     * 兼容处理SQL
     *
     * @param sql sql or sql对象
     * @param <S> 任意类型
     * @return 处理后的SQL
     */
    @Override
    public <S> S compatible(S sql) {
        return sql;
    }
}
