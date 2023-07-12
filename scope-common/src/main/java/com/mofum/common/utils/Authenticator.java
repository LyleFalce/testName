package com.mofum.common.utils;

import com.mofum.common.exception.AuthenticationException;

/**
 * 认证器
 */
public interface Authenticator {

    /**
     * 授权批准
     *
     * @param sid  范围ID
     * @param ssid 业务ID
     * @return true 认证通过 false认证失败
     * @throws AuthenticationException
     */
    boolean authorize(Object sid, Object ssid) throws AuthenticationException;
}
