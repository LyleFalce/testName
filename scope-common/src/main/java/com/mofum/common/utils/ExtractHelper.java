package com.mofum.common.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.mofum.common.annotation.SSID;
import com.mofum.common.annotation.Scope;
import com.mofum.common.meta.SID;
import com.mofum.common.meta.SchemaInfo;
import com.mofum.common.meta.reflect.ClassInfo;
import com.mofum.common.meta.reflect.MethodInfo;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 提取帮助类
 */
public final class ExtractHelper {

    /**
     * 提取参数中的范围ID
     *
     * @param parameterInfo 参数信息
     * @return 范围ID
     */
    public static List<SID> doExtract(MethodInfo.ParameterInfo parameterInfo) {
        return doExtract(parameterInfo, null);
    }

    /**
     * 提取参数中的范围ID
     *
     * @param parameterInfo  参数信息
     * @param extractHandler 提取处理器
     * @return 范围ID
     */
    public static List<SID> doExtract(MethodInfo.ParameterInfo parameterInfo, ExtractHandler extractHandler) {
        SSID ssid = (SSID) parameterInfo.getAnnotation(SSID.class);
        List<SID> sids = new ArrayList<>();
        ExtractHelper.doExtract(new ExtractConfig(parameterInfo.getValue(), sids, ssid, extractHandler, parameterInfo));
        return sids;
    }

    /**
     * 提取操作
     *
     * @param config 提取配置
     */
    private static void doExtract(final ExtractConfig config) {
        Object value = config.getValue();
        if (value == null) {
            return;
        }
        ExtractHelper.extractBasicType(config);
        ExtractHelper.extractArrayType(config);
        ExtractHelper.extractJSONObjectType(config);
        ExtractHelper.extractJSONArrayType(config);
        ExtractHelper.extractBaseCollectionType(config);
        ExtractHelper.extractObjectCollectionType(config);
        ExtractHelper.extractCompositeCollectionType(config);
        ExtractHelper.extractMapType(config);
        ExtractHelper.extractMapEntryType(config);
        ExtractHelper.extractObjectType(config);
    }

    /**
     * 提取Object类型中的范围ID
     *
     * @param config 提取配置
     */
    private static void extractObjectType(ExtractConfig config) {
        Object value = config.getValue();
        ClassInfo classInfo = new ClassInfo(value.getClass());
        if (!classInfo.hasAnnotation(Scope.class)) {
            return;
        }
        Scope scope = (Scope) classInfo.getAnnotation(Scope.class);
        if (!StrUtil.isBlank(scope.value())) {
            config.getParameterInfo().setScope(scope.value());
        }

        Field[] fields = ReflectUtil.getFields(value.getClass());
        Arrays.stream(fields).filter(field -> field.isAnnotationPresent(SSID.class)).forEach(e -> {
            SSID ssidField = e.getAnnotation(SSID.class);
            config.setValue(ReflectUtil.getFieldValue(value, e));
            config.setSsid(ssidField);
            ExtractHelper.doExtract(config);
        });
    }

    /**
     * 提取MapEntry类型中的范围ID
     *
     * @param config 提取配置
     */
    private static void extractMapEntryType(ExtractConfig config) {
        Object value = config.getValue();
        if (Map.Entry.class.isAssignableFrom(config.getValue().getClass())) {
            config.setValue(((Map.Entry<?, ?>) value).getValue());
            ExtractHelper.doExtract(config);
        }
    }

    /**
     * 提取集合类型中的范围ID
     *
     * @param config 提取配置
     */
    private static void extractCompositeCollectionType(ExtractConfig config) {
        Object value = config.getValue();
        if (ExtractConfigUtils.isCompositeCollection(config)) {
            List<Object> baseSids = new ArrayList<>();
            List<Object> beanSids = new ArrayList<>();
            ((Collection) value).stream().forEach(e -> {
                if (ObjectUtil.isBasicType(e)) {
                    baseSids.add(e);
                    return;
                }
                beanSids.add(e);
            });
            config.setValue(baseSids);
            ExtractHelper.doExtract(config);
            config.setValue(beanSids);
            ExtractHelper.doExtract(config);
        }
    }

    private static void extractObjectCollectionType(ExtractConfig config) {
        Object value = config.getValue();
        SSID ssid = config.getSsid();
        if (ExtractConfigUtils.isObjectCollection(config)) {
            ((Collection) value).stream().forEach(e -> {
                config.setValue(e);
                config.setSsid(ssid);
                ExtractHelper.doExtract(config);
            });
        }
    }

    private static void extractBaseCollectionType(ExtractConfig config) {
        Object value = config.getValue();
        if (ExtractConfigUtils.isIgnoreExtractTypeCollection(config)) {
            List<Object> list = new ArrayList<>();
            ((Collection) value).stream().forEach(e -> {
                list.add(e);
            });
            config.setValue(list);
            ExtractHelper.extractSid(config);
        }
    }

    /**
     * 提取Map类型中的范围ID
     *
     * @param config 提取配置
     */
    private static void extractMapType(ExtractConfig config) {
        Object value = config.getValue();
        SSID ssid = config.getSsid();
        if (ExtractConfigUtils.isNeedConvert(config)) {
            Object obj = BeanUtil.mapToBean(((Map) value), ssid.dataType(), true, CopyOptions.create());
            config.setValue(obj);
            ExtractHelper.doExtract(config);
            return;
        }
        if (ExtractConfigUtils.isSimpleMap(config)) {
            Map<Object, Object> map = (Map) value;
            map.entrySet().stream().map(Map.Entry::getValue).forEach(e -> {
                config.setValue(e);
                config.setSsid(ssid);
                ExtractHelper.doExtract(config);
            });
        }
    }

    /**
     * 提取Json数组类型中的范围ID
     *
     * @param config 提取配置
     */
    private static void extractJSONArrayType(ExtractConfig config) {
        Object value = config.getValue();
        if (String.class.isAssignableFrom(value.getClass()) && JSONUtil.isTypeJSONArray((String) value)) {
            List<Object> array = JSONUtil.parseArray(value);
            config.setValue(array);
        }
    }

    /**
     * 提取JSON Object类型中的范围ID
     *
     * @param config 提取配置
     */
    private static void extractJSONObjectType(ExtractConfig config) {
        Object value = config.getValue();
        if (String.class.isAssignableFrom(value.getClass()) && JSONUtil.isTypeJSONObject((String) value)) {
            Map<String, Object> collection = JSONUtil.parseObj(value);
            config.setValue(collection);
        }
    }

    /**
     * 提取数组类型中的范围ID
     *
     * @param config 提取配置
     */
    private static void extractArrayType(ExtractConfig config) {
        Object value = config.getValue();
        if (ArrayUtil.isArray(value)) {
            config.setValue(Arrays.asList(value));
        }
    }

    /**
     * 提取基础类型中的范围ID
     *
     * @param config 提取配置
     */
    private static void extractBasicType(ExtractConfig config) {
        Object value = config.getValue();
        SSID ssid = config.getSsid();
        boolean isBasicType = ObjectUtil.isBasicType(value)
                || (CharSequence.class.isAssignableFrom(value.getClass()) && Void.class.equals(ssid.dataType()));
        if (isBasicType) {
            ExtractHelper.extractSid(config);
        }
    }

    /**
     * 提取SID
     *
     * @param config 提取配置
     */
    private static void extractSid(ExtractConfig config) {
        Object value = config.getValue();
        SSID ssid = config.getSsid();
        SID sid = null;
        if (config.getExtractHandler() == null) {
            sid = SIDHelper.valueOfRunConvert(value, ssid);

        } else {
            sid = config.getExtractHandler().extract(value, ssid);
        }
        if (sid == null) {
            return;
        }
        MethodInfo.ParameterInfo parameterInfo = config.getParameterInfo();
        if (parameterInfo == null) {
            return;
        }
        extractSidParameterInfo(sid, parameterInfo);
        config.getSids().add(sid);
    }

    /**
     * 提取范围信息
     *
     * @param sid           范围信息
     * @param parameterInfo 参数信息
     */
    private static void extractSidParameterInfo(SID sid, MethodInfo.ParameterInfo parameterInfo) {
        if (StrUtil.isNotBlank(parameterInfo.getScope())) {
            sid.setScope(parameterInfo.getScope());
        }
        if (SchemaInfo.isNotEmpty(parameterInfo.getSchema()) && SchemaInfo.isEmpty(sid.getSchema())) {
            sid.setSchema(parameterInfo.getSchema());
        }
        if (parameterInfo.getSid() != null) {
            sid.fillSelfEmptyBySource(parameterInfo.getSid());
        }
    }

}
