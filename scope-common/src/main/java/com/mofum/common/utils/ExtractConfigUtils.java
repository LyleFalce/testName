package com.mofum.common.utils;

import cn.hutool.core.util.ObjectUtil;
import com.mofum.common.env.EnvsOperation;

import java.util.Collection;
import java.util.Map;

/**
 * 提取配置工具类
 */
final class ExtractConfigUtils {

    /**
     * 输出类型为空
     *
     * @param config 配置信息
     * @return true 为空 false 不为空
     */
    public static boolean isVoid(ExtractConfig config) {
        return config.getSsid().dataType().equals(Void.class);
    }

    /**
     * 输出类型不为空
     *
     * @param config 配置信息
     * @return true 不为空 false 为空
     */
    public static boolean isNotVoid(ExtractConfig config) {
        return !isVoid(config);
    }

    /**
     * 输出是否为Map
     *
     * @param config 配置信息
     * @return true 是 false 不是
     */
    public static boolean isNeedOutMap(ExtractConfig config) {
        return Map.class.isAssignableFrom(config.getSsid().dataType());
    }

    /**
     * 输出为Collection
     *
     * @param config 配置信息
     * @return true 是 false 不是
     */
    public static boolean isNeedOutCollection(ExtractConfig config) {
        return Collection.class.isAssignableFrom(config.getSsid().dataType());
    }

    /**
     * 输出不为collection
     *
     * @param config 配置信息
     * @return true 是  false 不是
     */
    public static boolean isNotNeedOutCollection(ExtractConfig config) {
        return !isNeedOutCollection(config);
    }

    /**
     * 输出不为Map
     *
     * @param config 配置信息
     * @return true 是 false 不是
     */
    public static boolean isNotNeedOutMap(ExtractConfig config) {
        return !isNeedOutMap(config);
    }

    /**
     * 是否需要转换
     *
     * @param config 配置信息
     * @return true 是  false 不是
     */
    public static boolean isNeedConvert(ExtractConfig config) {
        return isNotVoid(config) && isNotNeedOutMap(config) && isSimpleMap(config) && isNotNeedOutCollection(config);
    }

    /**
     * 是集合
     *
     * @param config 配置信息
     * @return true 是 false不是
     */
    public static boolean isCollection(ExtractConfig config) {
        boolean isCollection = Collection.class.isAssignableFrom(config.getValue().getClass());
        return isCollection;
    }

    /**
     * 是Map
     *
     * @param config 配置信息
     * @return true 是 false不是
     */
    public static boolean isMap(ExtractConfig config) {
        boolean isMap = Map.class.isAssignableFrom(config.getValue().getClass());
        return isMap;
    }

    /**
     * 简单Map，非集合
     *
     * @param config 配置信息
     * @return true 是 false不是
     */
    public static boolean isSimpleMap(ExtractConfig config) {
        return isMap(config) && !isCollection(config);
    }

    /**
     * 忽略类型集合，只有基础类型
     *
     * @param config 配置信息
     * @return true 是 false不是
     */
    public static boolean isIgnoreExtractTypeCollection(ExtractConfig config) {
        if (isCollection(config) && !isMap(config)) {
            Collection collection = (Collection) config.getValue();
            long basicCount = collection.stream().filter(e -> EnvsOperation.isIgnoreExtract(e)).count();
            return basicCount == collection.size();
        }
        return false;
    }

    /**
     * 复合集合，含有bean和忽略类型
     *
     * @param config 配置信息
     * @return true 是 false不是
     */
    public static boolean isCompositeCollection(ExtractConfig config) {
        if (isCollection(config) && !isMap(config)) {
            Collection collection = (Collection) config.getValue();
            long basicCount = collection.stream().filter(e -> EnvsOperation.isIgnoreExtract(e)).count();
            long notBasicCount = collection.stream().filter(e -> !EnvsOperation.isIgnoreExtract(e)).count();
            return basicCount > 0 && notBasicCount > 0;
        }
        return false;
    }

    /**
     * 非基础类型的集合
     *
     * @param config 配置信息
     * @return true 是 false不是
     */
    public static boolean isNotIgnoreExtractTypeCollection(ExtractConfig config) {
        if (isCollection(config) && !isMap(config)) {
            Collection collection = (Collection) config.getValue();
            return collection.stream().filter(e -> EnvsOperation.isIgnoreExtract(e)).count() == 0;
        }
        return false;
    }

    /**
     * 只有不忽略的对象类型
     *
     * @param config 配置信息
     * @return true 是 false不是
     */
    public static boolean isObjectCollection(ExtractConfig config) {
        if (isCollection(config) && !isMap(config)) {
            Collection collection = (Collection) config.getValue();
            long notBasic = collection.stream().filter(e -> !ObjectUtil.isBasicType(e)).count();
            return notBasic == collection.size() && isNotIgnoreExtractTypeCollection(config);
        }
        return false;
    }
}