package com.mofum.common.meta.reflect;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.util.ReflectUtil;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 类信息
 */
@Data
@NoArgsConstructor
public class ClassInfo {

    /**
     * 目标类
     */
    private Object target;

    /**
     * 字段
     */
    private List<FieldInfo> fields;

    /**
     * 类型
     */
    private Class<?> clazz;

    /**
     * 根据类型构造类型信息
     *
     * @param clazz 类型
     */
    public ClassInfo(Class<?> clazz) {
        this.clazz = clazz;
        this.fields = Arrays.stream(ReflectUtil.getFields(clazz)).map(e -> new FieldInfo(e)).collect(Collectors.toList());
    }

    /**
     * 是否拥有某个注解
     *
     * @param annotationClass 注解类型
     * @return true 有 false 无
     */
    public boolean hasAnnotation(Class<? extends Annotation> annotationClass) {
        return AnnotationUtil.hasAnnotation(this.getClazz(), annotationClass);
    }

    /**
     * 根据注解类型过滤保留字段信息
     *
     * @param filterClass 注解类型
     * @return 字段信息
     */
    public List<FieldInfo> annotationFilter(Class<? extends Annotation> filterClass) {
        return getFields().stream().filter(e -> e.hasAnnotation(filterClass)).collect(Collectors.toList());
    }

    /**
     * 获得注解，如果存在则获取，没有则为空
     *
     * @param annotationClass 注解类型
     * @return 注解类型
     */
    public Annotation getAnnotation(Class<? extends Annotation> annotationClass) {
        if (hasAnnotation(annotationClass)) {
            return AnnotationUtil.getAnnotation(this.getClazz(), annotationClass);
        }
        return null;
    }

    /**
     * 字段信息
     */
    @Data
    @NoArgsConstructor
    public static class FieldInfo {
        /**
         * 字段
         */
        private Field field;

        /**
         * 根据字段构建字段信息
         *
         * @param field 字段
         */
        public FieldInfo(Field field) {
            this.field = field;
        }

        /**
         * 获得字段名称
         *
         * @return 字段名称
         */
        public String getFieldName() {
            return field.getName();
        }

        /**
         * 是否拥有某个注解
         *
         * @param annotationClass 注解类型
         * @return true 有 false 无
         */
        public boolean hasAnnotation(Class<? extends Annotation> annotationClass) {
            return AnnotationUtil.hasAnnotation(this.getField(), annotationClass);
        }
    }
}
