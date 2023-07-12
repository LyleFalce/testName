package com.mofum.common.meta;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.util.ReflectUtil;
import com.mofum.common.annotation.SSID;
import com.mofum.common.annotation.Scope;
import com.mofum.common.meta.reflect.MethodInfo;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ServiceObjectTest {

    @Test
    public void scopeName() {
        Scope scope = AnnotationUtil.getAnnotation(ServiceObject.class, Scope.class);
        Assert.assertEquals("service", scope.value());
    }

    @Test
    public void serviceId() {
        Method[] objectMethods = ReflectUtil.getMethods(Object.class);
        Method[] methods = ReflectUtil.getMethods(ServiceObject.class, (e) -> !Arrays.asList(objectMethods).contains(e));
        List<MethodInfo> methodInfos = Arrays.stream(methods).map(MethodInfo::valueOf).collect(Collectors.toList());
        Assert.assertEquals(methodInfos.get(0).hasAnnotation(Scope.class), true);
    }

    @Scope("service")
    public class ServiceObject {

        @com.mofum.common.annotation.SSID
        private String serviceId;

        @Scope("2")
        public void serviceId(@com.mofum.common.annotation.SSID String serviceId) {

        }

        @Scope("3")
        public void serviceIds(@SSID List<String> serviceIds) {

        }
    }

}
