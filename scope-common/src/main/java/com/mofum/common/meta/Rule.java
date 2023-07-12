package com.mofum.common.meta;

/**
 * 规则
 */
public interface Rule {

    /**
     * 规则是否可访问
     *
     * @return true 可访问  false 不可访问
     */
    boolean access();
}
