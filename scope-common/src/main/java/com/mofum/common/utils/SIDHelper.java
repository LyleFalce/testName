package com.mofum.common.utils;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.mofum.common.env.Envs;
import com.mofum.common.meta.SID;
import com.mofum.common.meta.SSID;
import com.mofum.common.meta.SchemaInfo;

/**
 * SID帮助类
 */
public class SIDHelper {
    /**
     * 根据业务ID注解获得范围信息
     *
     * @param value 业务ID值
     * @param ssid  业务ID注解
     * @return 范围ID
     */
    public static SID valueOfRunConvert(Object value, com.mofum.common.annotation.SSID ssid) {
        SID sid = valueOf(ssid);
        if (ssid == null) {
            return sid;
        }
        sid.setValue(value);
        valueConvertByType(ssid, sid, value);
        valueConvertByEnvName(ssid, sid, value);
        return sid;
    }

    /**
     * 转换值根据环境变量名
     *
     * @param ssid  业务ID注解
     * @param sid   范围ID
     * @param value 业务ID值
     */
    private static void valueConvertByEnvName(com.mofum.common.annotation.SSID ssid, SID sid, Object value) {
        if (StrUtil.isBlank(ssid.sidConvert())) {
            return;
        }
        Object convertValue = Envs.getInstance().doConvert(ssid.sidConvert(), value);
        sid.setValue(convertValue);
    }

    /**
     * 转换值根据类型
     *
     * @param ssid  业务ID注解
     * @param sid   范围ID
     * @param value 业务ID值
     */
    private static void valueConvertByType(com.mofum.common.annotation.SSID ssid, SID sid, Object value) {
        if (ssid.sidConvertClass().equals(Void.class)) {
            return;
        }
        Converter converter = (Converter) ReflectUtil.newInstance(ssid.sidConvertClass());
        if (converter == null) {
            return;
        }
        Object convertValue = converter.convert(value);
        sid.setValue(convertValue);
    }

    /**
     * 根据业务ID注解获得范围信息
     *
     * @param ssid 业务ID注解
     * @return 范围ID
     */
    public static SID valueOf(com.mofum.common.annotation.SSID ssid) {
        return valueOf(ssid, null);
    }

    /**
     * 根据业务ID注解获得范围信息
     *
     * @param ssid 业务ID注解
     * @return 范围ID
     */
    public static SID valueOf(com.mofum.common.annotation.SSID ssid, Object value) {
        SID sid = new SID();
        if (ssid.sid() != null) {
            sid = SIDHelper.valueOf(ssid.sid());
        }
        SSID ssidInfo = SSID.valueOf(value, ssid);
        sid.setSsid(ssidInfo);
        return sid;
    }

    /**
     * 根据范围ID注解构建范围ID信息
     *
     * @param sid 范围ID注解
     * @return 范围信息
     */
    public static SID valueOf(com.mofum.common.annotation.SID sid) {
        if (sid == null) {
            return null;
        }
        SID sidInfo = new SID();
        if (StrUtil.isNotBlank(sid.scope())) {
            sidInfo.setScope(sid.scope());
        }
        if (StrUtil.isNotBlank(sid.value())) {
            sidInfo.setColumn(sid.value());
        }
        if (StrUtil.isNotBlank(sid.alias())) {
            sidInfo.setAlias(sid.alias());
        }
        if (sid.schema() != null) {
            sidInfo.setSchema(SchemaInfo.valueOf(sid.schema()));
        }
        if (sid.attribution() != null) {
            sidInfo.setAttribution(SchemaInfo.valueOf(sid.attribution()));
        }
        return sidInfo;
    }

}
