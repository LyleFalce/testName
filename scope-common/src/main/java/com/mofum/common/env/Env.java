package com.mofum.common.env;

import com.mofum.common.utils.Authenticator;
import com.mofum.common.utils.Converter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 环境
 */
public final class Env extends HashMap<String, Object> {

    /**
     * 数据库类型
     */
    String dbType;
    /**
     * 是否清除掉SQL中的编程注释内容
     */
    Boolean cleanComment = Boolean.TRUE;
    /**
     * SID转换器
     */
    private Map<String, Converter> converters = new HashMap<>();

    /**
     * SSID认证器
     */
    private Map<String, Authenticator> authenticators = new HashMap<>();

    /**
     * 忽略提取类型
     */
    private List<Class<?>> ignoreExtractClasses = new ArrayList<>();

    /**
     * HashMap 方法
     *
     * @param initialCapacity 初始容量
     * @param loadFactor      负载系数
     */
    protected Env(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    /**
     * HashMap 方法
     *
     * @param initialCapacity 初始化容量
     */
    protected Env(int initialCapacity) {
        super(initialCapacity);
    }

    /**
     * 无参HashMap
     */
    protected Env() {
    }

    /**
     * HashMap
     *
     * @param m
     */
    protected Env(Map m) {
        super(m);
    }

    /**
     * 获得SID转换器集合
     *
     * @return 转换器
     */
    public Map<String, Converter> getConverters() {
        return converters;
    }

    /**
     * 重设SID转换器容器
     *
     * @param converters 转换器容器
     */
    public void setConverters(Map<String, Converter> converters) {
        this.converters = converters;
        this.put("converters", this.converters.entrySet().stream().map(Entry::getValue).collect(Collectors.toList()));
    }

    /**
     * 注册全局范围转换器
     *
     * @param key       键
     * @param converter 范围转换器
     */
    public void registerConverter(String key, Converter converter) {
        if (this.converters.containsKey(key)) {
            throw new RuntimeException("This key was defined.");
        }
        if (converter == null) {
            throw new NullPointerException("This converter cannot be null.");
        }
        this.converters.put(key, converter);
        this.put("converters", this.converters.entrySet().stream().map(Entry::getValue).collect(Collectors.toList()));
    }

    /**
     * 获得SID认证器容器
     *
     * @return 认证器容器
     */
    public Map<String, Authenticator> getAuthenticators() {
        return authenticators;
    }

    /**
     * 重设SID认证器集合
     *
     * @param authenticators 认证器
     */
    public void setAuthenticators(Map<String, Authenticator> authenticators) {
        this.authenticators = authenticators;
        this.put("converters", this.authenticators.entrySet().stream().map(Entry::getValue).collect(Collectors.toList()));
    }

    /**
     * 注册全局范围转换器
     *
     * @param key           键
     * @param authenticator 认证器
     */
    public void registerAuthenticator(String key, Authenticator authenticator) {
        if (this.authenticators.containsKey(key)) {
            throw new RuntimeException("This key was defined.");
        }
        if (authenticator == null) {
            throw new NullPointerException("This converter cannot be null.");
        }
        this.authenticators.put(key, authenticator);
        this.put("converters", this.authenticators.entrySet().stream().map(Entry::getValue).collect(Collectors.toList()));
    }

    /**
     * 转换业务ID并获得范围ID值
     *
     * @param key   转换类型
     * @param value 值
     * @return 范围ID值
     */
    public Object doConvert(String key, Object value) {
        if (this.getConverters().containsKey(key)) {
            Object sid = this.getConverters().get(key).convert(value);
            return sid;
        }
        return null;
    }

    /**
     * 注册忽略解析的类型
     *
     * @param clazz 类型
     */
    public void registerIgnoreExtractType(Class<?> clazz) {
        if (!ignoreExtractClasses.contains(clazz)) {
            ignoreExtractClasses.add(clazz);
        }
    }

    /**
     * 注册忽略解析的类型
     *
     * @param obj 对象
     */
    public boolean containsIgnoreExtractType(Object obj) {
        if (obj == null) {
            return false;
        }
        return containsIgnoreExtractType(obj.getClass());
    }

    /**
     * 注册忽略解析的类型
     *
     * @param clazz 类型
     */
    public boolean containsIgnoreExtractType(Class<?> clazz) {
        return ignoreExtractClasses.contains(clazz);
    }
}
