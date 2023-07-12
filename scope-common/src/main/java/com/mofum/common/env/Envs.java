package com.mofum.common.env;

import cn.hutool.core.bean.BeanUtil;
import com.mofum.common.meta.SID;
import com.mofum.common.meta.SSID;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 环境帮助类
 */
public final class Envs {

    public static final String DEFAULT_DB_TYPE = "mysql";
    /**
     * 环境实例
     */
    private static final Env ENV = new Env();

    /**
     * 无法构造实例
     */
    private Envs() {
        throw new RuntimeException("This is static class.");
    }

    /**
     * 收集来的范围集合
     *
     * @return 范围集合
     */
    public static List<SID> scopeCollections() {
        return Optional.ofNullable(Runtime.THREAD_ENV.get().getSids()).orElse(new ArrayList<>());
    }

    /**
     * 收集来的业务ID集合
     *
     * @return 业务ID集合
     */
    public static List<SSID> serviceIdCollections() {
        return Optional.ofNullable(Runtime.THREAD_ENV.get().getSsids()).orElse(new ArrayList<>());
    }


    /**
     * 添加范围ID
     *
     * @param sid 范围ID
     */
    public static void addSID(SID... sid) {
        List<SID> sids = scopeCollections();
        sids.addAll(Arrays.asList(sid));
        Runtime.THREAD_ENV.get().setSids(sids);
    }

    /**
     * 添加多个范围ID
     *
     * @param sid 范围ID
     */
    public static void addAllSID(List<SID> sid) {
        List<SID> sids = scopeCollections();
        sids.addAll(sid);
        Runtime.THREAD_ENV.get().setSids(sids);
    }

    /**
     * 环境实例
     *
     * @return 环境
     */
    public static Env getInstance() {
        //如果没有数据库，则设置为Mysql为默认数据库
        if (ENV.dbType == null) {
            ENV.dbType = DEFAULT_DB_TYPE;
        }
        return ENV;
    }

    /**
     * 清除环境变量中所有的范围集合
     */
    public static void clear() {
        Envs.scopeCollections().clear();
        Envs.serviceIdCollections().clear();
        EnvsRule.rules().clear();
        EnvsOperation.ignoreExtractTypeCollections().clear();
    }

    /**
     * 添加业务ID
     *
     * @param ssid 业务ID
     */
    public static void addSSID(SSID... ssid) {
        List<SSID> ssids = serviceIdCollections();
        ssids.addAll(Arrays.asList(ssid));
        Runtime.THREAD_ENV.get().setSsids(ssids);
    }

    public static void addAllSSID(List<SSID> ssid) {
        List<SSID> ssids = serviceIdCollections();
        ssids.addAll(ssid);
        Runtime.THREAD_ENV.get().setSsids(ssids);
    }

    /**
     * 打平value中非基础类型的数据
     */
    public static void flatSids() {
        scopeCollections().stream()
                .filter(e -> e != null)
                .filter(e -> e.getValue() != null && Collection.class.isAssignableFrom(e.getValue().getClass()))
                .collect(Collectors.toList())
                .forEach(e -> flatScopeCollection(e));
    }

    /**
     * 打平数据范围集合
     *
     * @param origSid 源数据
     */
    private static void flatScopeCollection(SID origSid) {
        scopeCollections().remove(origSid);
        Collection<Object> collection = (Collection) origSid.getValue();
        collection.stream().forEach(e -> {
            SID sid = new SID();
            BeanUtil.copyProperties(origSid, sid, "value");
            sid.setValue(e);
            Envs.addSID(sid);
        });
    }

    /**
     * 打平value中非基础类型的数据
     */
    public static void flatServiceIds() {
        serviceIdCollections().stream()
                .filter(e -> e != null)
                .filter(e -> e.getValue() != null && Collection.class.isAssignableFrom(e.getValue().getClass()))
                .collect(Collectors.toList())
                .forEach(e -> flatServiceCollection(e));
    }

    /**
     * 打平数据范围集合
     *
     * @param origSsid 源数据
     */
    private static void flatServiceCollection(SSID origSsid) {
        serviceIdCollections().remove(origSsid);
        Collection<Object> collection = (Collection) origSsid.getValue();
        collection.stream().forEach(e -> {
            SSID sid = new SSID();
            BeanUtil.copyProperties(origSsid, sid, "value");
            sid.setValue(e);
            Envs.addSSID(sid);
        });
    }
}
