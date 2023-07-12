package com.mofum.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 范围ID
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface SID {

    /**
     * 列名
     *
     * @return
     */
    String value() default "";

    /**
     * 列别名
     *
     * @return
     */
    String alias() default "";

    /**
     * scope列
     *
     * @return
     */
    String scope() default "";

    /**
     * 表信息
     *
     * @return
     */
    Schema schema() default @Schema();

    /**
     * 归属表信息
     *
     * @return
     */
    Schema attribution() default @Schema();
}
