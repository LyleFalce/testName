package com.mofum.common.annotation;


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 范围类型
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Scope {
    /**
     * 范围名称
     *
     * @return 范围名称
     */
    String value() default "";

    /**
     * 表信息
     *
     * @return 表信息
     */
    Schema schema() default @Schema;

    /**
     * 属性规则(对象)
     * <p>
     * propertiesName-QUID(QUERY,UPDATE,INSERT,DELETE)
     *
     * @return 属性
     */
    String[] propertiesRules() default "";

    /**
     * 列规则(表)
     * <p>
     * columnName-QUID(QUERY,UPDATE,INSERT,DELETE)
     *
     * @return 列
     */
    String[] columnRules() default "";
}
