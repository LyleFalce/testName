package com.mofum.common.utils;

/**
 * 转换器
 */
public interface Converter {

    /**
     * 转换器
     *
     * @param serviceIdValue 业务ID值
     * @return 范围ID值
     */
    Object convert(Object serviceIdValue);
}