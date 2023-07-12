package com.mofum.common.env;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.mofum.common.exception.AuthenticationException;
import com.mofum.common.meta.SID;
import com.mofum.common.meta.SSID;
import com.mofum.common.utils.Authenticator;
import com.mofum.common.utils.AuthorizeHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * 环境操作类
 */
public final class EnvsOperation {
    private EnvsOperation() {
        throw new RuntimeException("This is static class.");
    }

    /**
     * 收集来的业务ID集合
     *
     * @return 业务ID集合
     */
    public static List<Class> ignoreExtractTypeCollections() {
        return Optional.ofNullable(Runtime.THREAD_ENV.get().getIgnoreExtractTypes()).orElse(new ArrayList<>());
    }

    /**
     * 获得环境的数据库类型
     *
     * @return 数据库类型
     */
    public static String getDbType() {
        return Optional.ofNullable(Runtime.THREAD_ENV.get().getDbType()).orElse(Envs.getInstance().dbType);
    }

    /**
     * 设置数据库类型
     *
     * @param dbType 数据库类型
     */
    public static void setDbType(String dbType) {
        Envs.getInstance().dbType = dbType;
    }


    /**
     * 获得清除注释变量
     *
     * @return 清除注释变量值
     */
    public static Boolean getCleanComment() {
        return Envs.getInstance().cleanComment;
    }

    /**
     * 设置清除注释变量习惯
     *
     * @param cleanComment 清除注释变量值
     */
    public static void setCleanComment(Boolean cleanComment) {
        Envs.getInstance().cleanComment = cleanComment;
    }

    /**
     * 打平value中非基础类型的数据
     */
    public static void authorize() throws AuthenticationException {
        authorize(null);
    }

    /**
     * 打平value中非基础类型的数据
     */
    public static void authorize(AuthorizeHandler authorizeHandler) throws AuthenticationException {
        for (SID sid : Envs.scopeCollections()) {
            if (authorizeHandler != null) {
                authorizeHandler.authorize(sid);
                continue;
            }
            doAuthorize(sid);
        }
    }

    /**
     * 做认证处理
     *
     * @return true 认证成功  false 认证失败
     * @throws AuthenticationException 认证异常
     */
    public static void doAuthorize(SID sid) throws AuthenticationException {
        if (sid == null) {
            return;
        }
        SSID ssid = sid.getSsid();
        if (ssid == null) {
            return;
        }
        if (StrUtil.isBlank(ssid.getAuthenticator())) {
            return;
        }
        doAuthorizeByType(sid, ssid);
        doAuthorizeByEnvName(sid, ssid);
    }

    /**
     * 认证操作根据环境变量名
     *
     * @param sid  范围ID
     * @param ssid 业务ID
     */
    private static void doAuthorizeByEnvName(SID sid, SSID ssid) {
        Authenticator authenticator = Envs.getInstance().getAuthenticators().get(ssid.getAuthenticator());
        if (authenticator == null) {
            return;
        }
        boolean authorizedStatus = authenticator.authorize(sid.getValue(), ssid.getValue());
        doAuthenticatorCheckResult(authorizedStatus);
    }

    /**
     * 认证操作根据类型
     *
     * @param sid  范围ID
     * @param ssid 业务ID
     */
    private static void doAuthorizeByType(SID sid, SSID ssid) {
        if (ssid.getAuthenticatorClass().equals(Void.class)) {
            return;
        }
        Authenticator authenticator = (Authenticator) ReflectUtil.newInstance(ssid.getAuthenticatorClass());
        if (authenticator == null) {
            return;
        }
        boolean authorizedStatus = authenticator.authorize(sid.getValue(), ssid.getValue());
        doAuthenticatorCheckResult(authorizedStatus);
    }

    /**
     * 认证结果
     *
     * @param authorizedStatus true 认证成功 false 认证失败
     */
    private static void doAuthenticatorCheckResult(boolean authorizedStatus) {
        if (!authorizedStatus) {
            throw new AuthenticationException("Authentication failed.");
        }
    }

    /**
     * 忽略解析类型
     *
     * @param object 对象
     * @return 忽略解析类型
     */
    public static boolean isIgnoreExtract(Object object) {
        if (object == null) {
            return false;
        }
        return ObjectUtil.isBasicType(object)
                || isIgnoreExtractType(object.getClass())
                || ignoreExtractTypeCollections().contains(object.getClass());
    }

    /**
     * 忽略解析类型
     *
     * @param object 对象
     * @return 忽略解析类型
     */
    public static boolean isIgnoreExtractType(Object object) {
        return Envs.getInstance().containsIgnoreExtractType(object);
    }

    /**
     * 添加忽略类型
     *
     * @param clazz 忽略类型
     */
    public static void addIgnoreExtractType(Class... clazz) {
        List<Class> extractTypes = ignoreExtractTypeCollections();
        extractTypes.addAll(Arrays.asList(clazz));
        Runtime.THREAD_ENV.get().setIgnoreExtractTypes(extractTypes);
    }

    /**
     * 添加忽略类型集合
     *
     * @param classes 忽略类型集合
     */
    public static void addAllIgnoreExtractType(List<Class> classes) {
        List<Class> extractTypes = ignoreExtractTypeCollections();
        extractTypes.addAll(classes);
        Runtime.THREAD_ENV.get().setIgnoreExtractTypes(extractTypes);
    }
}
