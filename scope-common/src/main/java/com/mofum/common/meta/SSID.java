package com.mofum.common.meta;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 业务ID信息
 */
@Data
@AllArgsConstructor
public class SSID {
    /**
     * 业务ID值
     */
    private Object value;
    /**
     * 业务ID名称
     */
    private String name;
    /**
     * 业务ID数据类型
     */
    private Class<?> dataType;

    /**
     * sid转换器
     */
    private Class<?> sidConvertClass;

    /**
     * Sid转换器名称
     */
    private String sidConvert;

    /**
     * 认证器名称（环境变量）
     *
     * @return 认证器名称
     */
    private String authenticator;

    /**
     * 静态认证器
     *
     * @return 静态认证器
     */
    private Class<?> authenticatorClass;

    public SSID() {
    }

    public static SSID valueOf(Object value, com.mofum.common.annotation.SSID ssid) {
        if (ssid == null) {
            return null;
        }
        SSID instance = new SSID();
        instance.setValue(value);
        instance.setName(ssid.value());
        instance.setDataType(ssid.dataType());
        instance.setSidConvertClass(ssid.sidConvertClass());
        instance.setSidConvert(ssid.sidConvert());
        instance.setAuthenticator(ssid.authenticator());
        instance.setAuthenticatorClass(ssid.authenticatorClass());
        return instance;
    }

}
