package com.mofum.common.utils.impl;

import com.mofum.common.exception.AuthenticationException;
import com.mofum.common.utils.Authenticator;

/**
 * 认证器实现
 */
public class DefaultAuthenticatorImpl implements Authenticator {
    @Override
    public boolean authorize(Object sid, Object ssid) throws AuthenticationException {
        return false;
    }
}
