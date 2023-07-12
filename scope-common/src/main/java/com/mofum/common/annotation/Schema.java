package com.mofum.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 表信息
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Schema {

    /**
     * 表名
     *
     * @return 表名
     */
    String value() default "";

    /**
     * 表别名
     *
     * @return 表别名
     */
    String alias() default "";

}
