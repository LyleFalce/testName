package com.mofum.common.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 业务范围ID
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
public @interface SSID {

    /**
     * 业务范围ID列名
     *
     * @return 范围ID
     */
    String value() default "";

    /**
     * 数据类型
     *
     * @return 数据类型
     */
    Class<?> dataType() default Void.class;

    /**
     * 业务ID转换器名称（环境变量）
     *
     * @return 业务ID转换器名称
     */
    String sidConvert() default "";

    /**
     * 静态业务ID转换器
     *
     * @return 业务ID转换器
     */
    Class<?> sidConvertClass() default Void.class;

    /**
     * 范围ID信息
     *
     * @return 范围ID信息
     */
    SID sid() default @SID;

    /**
     * 认证器名称（环境变量）
     *
     * @return 认证器名称
     */
    String authenticator() default "";

    /**
     * 静态认证器
     *
     * @return 静态认证器
     */
    Class<?> authenticatorClass() default Void.class;
}
