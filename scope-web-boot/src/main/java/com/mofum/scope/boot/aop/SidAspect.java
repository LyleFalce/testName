package com.mofum.scope.boot.aop;

import cn.hutool.aop.aspects.SimpleAspect;
import com.mofum.common.annotation.SID;
import com.mofum.common.env.Envs;
import com.mofum.common.meta.reflect.ClassInfo;
import com.mofum.common.meta.reflect.MethodInfo;
import com.mofum.common.utils.SIDHelper;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

/**
 * 权限切面
 */
@Slf4j
public class SidAspect extends SimpleAspect {
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
        if (target.getClass().isAnnotationPresent(SID.class) || methodInfo.hasAnnotation(SID.class)) {
            this.wrapperSID(methodInfo, target);
            return true;
        }
        return false;
    }

    /**
     * 包装范围ID信息
     *
     * @param methodInfo 方法信息
     * @param target     目标对象
     */
    private void wrapperSID(MethodInfo methodInfo, Object target) {
        SID globalSid = (SID) new ClassInfo(target.getClass()).getAnnotation(SID.class);
        SID sid = (SID) methodInfo.getAnnotation(SID.class);
        com.mofum.common.meta.SID sidInfo = SIDHelper.valueOf(sid);
        if (sidInfo == null) {
            sidInfo = SIDHelper.valueOf(globalSid);
        }
        if (sidInfo == null) {
            sidInfo = new com.mofum.common.meta.SID();
        }
        if (sid != null) {
            sidInfo.setAlias(sid.alias());
        }
        com.mofum.common.meta.SID finalSidInfo = sidInfo;
        Envs.scopeCollections().stream().forEach(e -> {
            e.replaceSelfBySource(finalSidInfo);
        });
    }
}
