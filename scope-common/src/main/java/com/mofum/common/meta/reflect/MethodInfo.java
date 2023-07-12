package com.mofum.common.meta.reflect;

import cn.hutool.core.annotation.AnnotationUtil;
import com.mofum.common.meta.SID;
import com.mofum.common.meta.SchemaInfo;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 方法信息
 */
@Data
@NoArgsConstructor
public class MethodInfo {

    /**
     * 目标对象
     */
    private Object target;
    /**
     * 方法
     */
    private Method method;

    /**
     * 参数信息
     */
    private List<ParameterInfo> parameters;

    /**
     * 参数返回值
     */
    private Object returnValue;

    /**
     * 范围类型
     */
    private String scope;

    /**
     * 表信息
     */
    private SchemaInfo schema;

    /**
     * 根据方法、参数信息构建方法信息
     *
     * @param method     方法
     * @param parameters 参数信息
     */
    public MethodInfo(Method method, List<ParameterInfo> parameters) {
        this.method = method;
        this.parameters = parameters;
    }

    /**
     * 根据方法信息 生成方法信息
     *
     * @param method 方法信息
     * @return 方法信息
     */
    public static MethodInfo valueOf(Method method) {
        int paramsCount = method.getParameterCount();
        Annotation[][] annotations = method.getParameterAnnotations();
        Class<?>[] parameterTypes = method.getParameterTypes();
        Parameter[] parameters = method.getParameters();

        List<ParameterInfo> list = new ArrayList<>();
        for (int i = 0; i < paramsCount; i++) {
            ParameterInfo parameterInfo = new ParameterInfo(annotations[i], parameterTypes[i], parameters[i], i);
            list.add(parameterInfo);
        }
        return new MethodInfo(method, list);
    }

    /**
     * 构建方法信息
     *
     * @param method    方法
     * @param args      参数
     * @param returnVal 返回值
     * @return 方法信息
     */
    public static MethodInfo valueOf(Method method, Object[] args, Object returnVal) {
        MethodInfo methodInfo = MethodInfo.valueOf(method);
        methodInfo.setReturnValue(returnVal);
        if (args == null) {
            return methodInfo;
        }
        for (int index = 0; index < args.length; index++) {
            methodInfo.updateParameterValue(index, args[index]);
        }
        return methodInfo;
    }

    /**
     * 更新序号参数的值
     *
     * @param paramIndex 参数序号
     * @param value      值
     */
    public void updateParameterValue(Integer paramIndex, Object value) {
        parameters.stream().filter(e -> e.getIndex().equals(paramIndex)).forEach(e -> e.setValue(value));
    }

    /**
     * 更新序号参数的范围类型
     *
     * @param paramIndex 参数序号
     * @param scope      范围类型
     */
    public void updateParameterScope(Integer paramIndex, String scope) {
        parameters.stream().filter(e -> e.getIndex().equals(paramIndex)).forEach(e -> e.setScope(scope));
    }

    /**
     * 更新序号参数的表信息
     *
     * @param paramIndex 参数序号
     * @param schema     表信息
     */
    public void updateParameterSchema(Integer paramIndex, SchemaInfo schema) {
        parameters.stream().filter(e -> e.getIndex().equals(paramIndex)).forEach(e -> e.setSchema(schema));
    }

    /**
     * 根据参数序号获取某个参数的值
     *
     * @param paramIndex 参数序号
     * @return 参数值
     */
    public Object getParameterValue(Integer paramIndex) {
        return parameters.stream().filter(e -> e.getIndex().equals(paramIndex)).findFirst().orElse(null);
    }

    /**
     * 获取方法名
     *
     * @return 方法名
     */
    public String getName() {
        return method.getName();
    }

    /**
     * 是否拥有某个注解
     *
     * @param annotationClass 注解类型
     * @return true 有 false 无
     */
    public boolean hasAnnotation(Class<? extends Annotation> annotationClass) {
        return AnnotationUtil.hasAnnotation(this.getMethod(), annotationClass);
    }

    /**
     * 获得注解，如果存在则获取，没有则为空
     *
     * @param annotationClass 注解类型
     * @return 注解类型
     */
    public Annotation getAnnotation(Class<? extends Annotation> annotationClass) {
        if (hasAnnotation(annotationClass)) {
            return AnnotationUtil.getAnnotation(this.getMethod(), annotationClass);
        }
        return null;
    }

    @Data
    public static class ParameterInfo {
        /**
         * 注解信息
         */
        private Annotation[] annotations;

        /**
         * 参数类型
         */
        private Class<?> parameterType;

        /**
         * 参数
         */
        private Parameter parameter;

        /**
         * 参数序号
         */
        private Integer index;

        /**
         * 参数值
         */
        private Object value;

        /**
         * 表信息
         */
        private SchemaInfo schema;

        /**
         * 范围类型
         */
        private String scope;

        /**
         * 范围ID信息
         */
        private SID sid;

        /**
         * 根据注解、参数类型、 参数、 参数序号构造参数信息
         *
         * @param annotations   注解
         * @param parameterType 参数类型
         * @param parameter     参数
         * @param index         参数序号
         */
        public ParameterInfo(Annotation[] annotations, Class<?> parameterType, Parameter parameter, Integer index) {
            this.annotations = annotations;
            this.parameterType = parameterType;
            this.parameter = parameter;
            this.index = index;
        }

        /**
         * 是否拥有某个注解
         *
         * @param annotationClass 注解类型
         * @return true 有 false 无
         */
        public boolean hasAnnotation(Class<? extends Annotation> annotationClass) {
            long size = Arrays.stream(annotations).filter(e -> e.annotationType().equals(annotationClass)).count();
            return size > 0;
        }

        /**
         * 获得注解，如果存在则获取，没有则为空
         *
         * @param annotationClass 注解类型
         * @return 注解类型
         */
        public Annotation getAnnotation(Class<? extends Annotation> annotationClass) {
            if (hasAnnotation(annotationClass)) {
                return Arrays.stream(annotations).filter(e -> e.annotationType().equals(annotationClass)).findFirst().orElse(null);
            }
            return null;
        }
    }
}
