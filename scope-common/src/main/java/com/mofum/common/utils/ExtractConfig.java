package com.mofum.common.utils;

import com.mofum.common.annotation.SSID;
import com.mofum.common.meta.SID;
import com.mofum.common.meta.reflect.MethodInfo;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * 提取配置信息
 */
@Data
@AllArgsConstructor
final class ExtractConfig {

    /**
     * 值
     */
    private Object value;
    /**
     * 范围ID
     */
    private List<SID> sids;
    /**
     * 业务ID
     */
    private SSID ssid;
    /**
     * 提取处理器
     */
    private ExtractHandler extractHandler;
    /**
     * 参数信息
     */
    private MethodInfo.ParameterInfo parameterInfo;
}