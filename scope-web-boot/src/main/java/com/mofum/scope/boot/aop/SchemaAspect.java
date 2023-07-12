package com.mofum.scope.boot.aop;

import cn.hutool.aop.aspects.SimpleAspect;
import cn.hutool.core.util.StrUtil;
import com.mofum.common.annotation.Schema;
import com.mofum.common.env.Envs;
import com.mofum.common.meta.SchemaInfo;
import com.mofum.common.meta.reflect.ClassInfo;
import com.mofum.common.meta.reflect.MethodInfo;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

/**
 * 表切面
 */
@Slf4j
public class SchemaAspect extends SimpleAspect {

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
        if (target.getClass().isAnnotationPresent(Schema.class) || methodInfo.hasAnnotation(Schema.class)) {
            this.wrapperSchema(methodInfo, target);
            return true;
        }
        return false;
    }

    /**
     * 包装表信息
     *
     * @param methodInfo 方法信息
     * @param target     目标对象
     */
    private void wrapperSchema(MethodInfo methodInfo, Object target) {
        Schema globalSchema = (Schema) new ClassInfo(target.getClass()).getAnnotation(Schema.class);
        Schema schema = (Schema) methodInfo.getAnnotation(Schema.class);
        SchemaInfo schemaInfo = SchemaInfo.valueOf(schema);
        if (schemaInfo == null) {
            schemaInfo = SchemaInfo.valueOf(globalSchema);
        }
        if (schemaInfo == null) {
            schemaInfo = new SchemaInfo();
        }
        if (schema != null) {
            schemaInfo.setAlias(schema.alias());
        }
        methodInfo.setSchema(schemaInfo);
        Envs.scopeCollections().stream().filter(e -> e.getSchema() == null || StrUtil.isBlank(e.getSchema().getName())).forEach(e -> e.setSchema(methodInfo.getSchema()));
    }
}
