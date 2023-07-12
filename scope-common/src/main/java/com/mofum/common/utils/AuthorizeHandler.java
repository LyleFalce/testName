package com.mofum.common.utils;

import com.mofum.common.exception.AuthenticationException;
import com.mofum.common.meta.SID;

/**
 * 认证操作
 */
public interface AuthorizeHandler {
    /**
     * 认证操作
     *
     * @param sid 范围ID
     * @return 范围ID
     */
    void authorize(SID sid) throws AuthenticationException;
}
