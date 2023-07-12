package com.mofum.common.utils;

import com.mofum.common.annotation.SSID;
import com.mofum.common.meta.SID;

/**
 * 业务ID提取器
 */
public interface ExtractHandler {
    /**
     * 从业务ID注解提取范围ID
     *
     * @param value 业务ID值
     * @param ssid  业务ID注解
     * @return 范围ID
     */
    SID extract(Object value, SSID ssid);
}
