package com.mofum.scope.boot.aop;

import cn.hutool.aop.aspects.Aspect;
import cn.hutool.aop.aspects.SimpleAspect;
import cn.hutool.core.convert.Converter;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.mofum.common.annotation.SID;
import com.mofum.common.annotation.SSID;
import com.mofum.common.annotation.Schema;
import com.mofum.common.annotation.Scope;
import com.mofum.common.env.Envs;
import com.mofum.common.env.EnvsOperation;
import com.mofum.common.env.EnvsRule;
import com.mofum.common.exception.AuthenticationException;
import com.mofum.common.meta.SchemaInfo;
import com.mofum.common.meta.reflect.ClassInfo;
import com.mofum.common.meta.reflect.MethodInfo;
import com.mofum.common.utils.Authenticator;
import com.mofum.common.utils.AuthorizeHandler;
import com.mofum.common.utils.ExtractHandler;
import com.mofum.common.utils.ExtractHelper;
import com.mofum.common.utils.ResultFilterHelper;
import com.mofum.common.utils.SIDHelper;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 启动切面
 */
@Slf4j
public class BootAspect extends SimpleAspect {
    /**
     * 提取参数中的范围ID
     *
     * @param parameterInfo 参数信息
     */
    public static void extract(MethodInfo.ParameterInfo parameterInfo) {
        if (!parameterInfo.hasAnnotation(SSID.class)) {
            return;
        }
        SSID ssid = (SSID) parameterInfo.getAnnotation(SSID.class);
        parameterInfo.setSid(SIDHelper.valueOf(ssid));
        ExtractHandler handler = buildSpringExtractHandler();
        convert2Sid(parameterInfo, handler);
    }

    /**
     * Spring 容器的转换
     *
     * @param sid   范围ID信息
     * @param value 范围值
     * @param ssid  业务ID注解
     * @return 范围ID
     */
    private static com.mofum.common.meta.SID doSpringConvert(com.mofum.common.meta.SID sid, Object value, SSID ssid) {
        if (sid == null) {
            return doSpringConvert(value, ssid);
        }
        if (StrUtil.isBlank(ssid.sidConvert())) {
            return sid;
        }
        Converter<Object> converter = SpringUtil.getBean(ssid.sidConvert());
        if (converter != null) {
            sid.setValue(converter.convert(value, null));
        }
        return sid;
    }

    /**
     * Spring 容器的转换
     *
     * @param value 范围值
     * @param ssid  业务ID注解
     * @return 范围ID
     */
    private static com.mofum.common.meta.SID doSpringConvert(Object value, SSID ssid) {
        com.mofum.common.meta.SID sid = SIDHelper.valueOf(ssid);
        Converter<Object> converter = SpringUtil.getBean(ssid.sidConvert());
        if (converter != null) {
            sid.setValue(converter.convert(value, null));
        }
        return sid;
    }

    /**
     * Env 容器的转换
     *
     * @param value 范围值
     * @param ssid  业务ID注解
     * @return 范围ID
     */
    private static com.mofum.common.meta.SID doEnvConvert(Object value, SSID ssid) {
        return SIDHelper.valueOfRunConvert(value, ssid);
    }

    /**
     * 转换为范围ID
     *
     * @param parameterInfo 参数信息
     * @param handler       操作
     */
    private static void convert2Sid(MethodInfo.ParameterInfo parameterInfo, ExtractHandler handler) {
        List<com.mofum.common.meta.SID> sids = ExtractHelper.doExtract(parameterInfo, handler);
        if (sids != null) {
            sids = sids.stream().filter(e -> e.getValue() != null).collect(Collectors.toList());
        }
        Envs.addAllSID(sids);
        EnvsOperation.authorize(buildSpringAuthorizeHandler());
        Envs.flatSids();
        Envs.flatServiceIds();
    }

    /**
     * 构建认证器操作
     *
     * @return 认证器操作
     */
    private static AuthorizeHandler buildSpringAuthorizeHandler() {
        return sid -> {
            //全局环境中的认证
            EnvsOperation.doAuthorize(sid);
            //Spring环境中的认证
            doSpringAuthorize(sid);
        };
    }

    /**
     * 做Spring环境的认证
     *
     * @param sid 范围ID信息
     */
    private static void doSpringAuthorize(com.mofum.common.meta.SID sid) {
        if (sid == null) {
            return;
        }
        com.mofum.common.meta.SSID ssid = sid.getSsid();
        if (sid.getSsid() == null) {
            return;
        }
        if (StrUtil.isBlank(ssid.getAuthenticator())) {
            return;
        }
        Authenticator authenticator = SpringUtil.getBean(ssid.getAuthenticator());
        boolean authorizedStatus = true;
        if (authenticator != null) {
            authorizedStatus = authenticator.authorize(sid.getValue(), ssid.getValue());
        }
        if (!authorizedStatus) {
            throw new AuthenticationException("Authentication failed.");
        }
    }

    /**
     * 构建spring 提取处理器
     *
     * @return 提取处理
     */
    private static ExtractHandler buildSpringExtractHandler() {
        return (value, ssid) -> {
            Envs.addSSID(com.mofum.common.meta.SSID.valueOf(value, ssid));
            //全局配置中的转换器
            com.mofum.common.meta.SID sid = doEnvConvert(value, ssid);
            //WebBean中定义的转换器
            sid = doSpringConvert(sid, value, ssid);
            return sid;
        };
    }

    /**
     * 拦截前置通知
     *
     * @param target 目标对象
     * @param method 方法
     * @param args   参数
     * @return true 拦截 false不拦截
     */
    @Override
    public boolean before(Object target, Method method, Object[] args) {
        MethodInfo methodInfo = MethodInfo.valueOf(method, args, null);
        boolean hasScope = target.getClass().isAnnotationPresent(Scope.class) || methodInfo.hasAnnotation(Scope.class);
        if (methodInfo.hasAnnotation(Scope.class)) {
            this.wrapperScope(methodInfo, target);
            methodInfo.getParameters().stream().forEach(BootAspect::extract);
        }

        boolean hasSchema = this.doSubAspect(Schema.class, new SchemaAspect(), target, method, args);
        boolean hasSid = this.doSubAspect(SID.class, new SidAspect(), target, method, args);
        return hasScope || hasSchema || hasSid;
    }

    /**
     * 执行子切面
     *
     * @param filter 注解过滤
     * @param aspect 切面
     * @param target 目标对象
     * @param method 方法
     * @param args   方法参数值
     * @return true 拦截 false 不拦截
     */
    private boolean doSubAspect(Class<? extends Annotation> filter, Aspect aspect, Object target, Method method, Object[] args) {
        MethodInfo methodInfo = MethodInfo.valueOf(method, args, null);
        if (target.getClass().isAnnotationPresent(filter)) {
            return aspect.before(target, method, args);
        }
        if (methodInfo.hasAnnotation(filter)) {
            return aspect.before(target, method, args);
        }
        return false;
    }

    /**
     * 包装范围信息
     *
     * @param methodInfo 方法信息
     * @param target     目标对象
     */
    private void wrapperScope(MethodInfo methodInfo, Object target) {
        Scope globalScope = (Scope) new ClassInfo(target.getClass()).getAnnotation(Scope.class);
        Scope scope = (Scope) methodInfo.getAnnotation(Scope.class);
        //处理Scope注解中的rule权限
        this.doProcessScopeRule(globalScope, scope);

        if (StrUtil.isBlank(scope.value())) {
            methodInfo.setScope(globalScope.value());
        } else {
            methodInfo.setScope(scope.value());
        }
        SchemaInfo schema = SchemaInfo.valueOf(scope.schema());
        if (schema == null) {
            schema = SchemaInfo.valueOf(globalScope.schema());
        }
        if (schema == null) {
            schema = new SchemaInfo();
        }
        schema.setAlias(scope.schema().alias());
        methodInfo.setSchema(schema);
        methodInfo.getParameters().forEach(e -> {
            e.setScope(methodInfo.getScope());
            e.setSchema(methodInfo.getSchema());
        });
    }

    private void doProcessScopeRule(Scope globalScope, Scope scope) {
        this.addScopeRule(globalScope);
        this.addScopeRule(scope);
    }

    private void addScopeRule(Scope scope) {
        if (scope == null) {
            return;
        }
        String[] propertiesRules = scope.propertiesRules();
        String[] columnRules = scope.columnRules();
        if (ArrayUtil.isNotEmpty(propertiesRules)) {
            EnvsRule.addPropertiesRule(propertiesRules);
        }
        if (ArrayUtil.isNotEmpty(columnRules)) {
            EnvsRule.addColumnRule(columnRules);
        }
    }

    @Override
    public boolean after(Object target, Method method, Object[] args, Object returnVal) {
        MethodInfo methodInfo = MethodInfo.valueOf(method, args, returnVal);
        ResultFilterHelper.doFilter(methodInfo);
        return super.after(target, method, args, methodInfo.getReturnValue());
    }
}
