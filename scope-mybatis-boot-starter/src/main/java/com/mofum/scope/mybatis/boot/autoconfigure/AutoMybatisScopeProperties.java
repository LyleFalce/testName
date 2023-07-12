package com.mofum.scope.mybatis.boot.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 自动配置Mybatis权限范围属性
 *
 * @since 2019-03-20
 **/
@ConfigurationProperties(prefix = "mofum.scope.mybatis")
@Data
public class AutoMybatisScopeProperties {

    /**
     * 需要Mybatis
     */
    private boolean enable = true;
}
