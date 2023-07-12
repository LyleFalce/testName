package com.mofum.scope.boot;

import cn.hutool.aop.ProxyUtil;
import com.mofum.common.annotation.SID;
import com.mofum.common.annotation.Schema;
import com.mofum.common.annotation.Scope;
import com.mofum.common.meta.reflect.ClassInfo;
import com.mofum.scope.boot.aop.BootAspect;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * 范围初始化处理器
 */
@Slf4j
public class ScopeInitializationPostProcessor implements BeanPostProcessor {

    /**
     * 后置处理Bean信息
     *
     * @param bean     bean信息
     * @param beanName bean名称
     * @return 新Bean信息
     * @throws BeansException Bean异常
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return new BeanProcessor(bean, beanName).process();
    }

    /**
     * Bean处理器
     */
    @Data
    @NoArgsConstructor
    private static class BeanProcessor {

        /**
         * bean 信息
         */
        private Object bean;

        /**
         * bean 名称
         */
        private Object name;

        /**
         * 根据bean、bean名称构建bean处理器
         *
         * @param bean bean信息
         * @param name bean名称
         */
        public BeanProcessor(Object bean, Object name) {
            this.bean = bean;
            this.name = name;
        }


        /**
         * 处理
         *
         * @return 处理后的bean
         */
        private Object process() {
            if (bean == null) {
                return null;
            }
            ClassInfo classInfo = new ClassInfo(bean.getClass());
            if (classInfo.hasAnnotation(Scope.class)
                    || classInfo.hasAnnotation(Schema.class)
                    || classInfo.hasAnnotation(SID.class)) {
                Object proxy = ProxyUtil.proxy(bean, BootAspect.class);
                return proxy;
            }
            return bean;
        }

    }
}
