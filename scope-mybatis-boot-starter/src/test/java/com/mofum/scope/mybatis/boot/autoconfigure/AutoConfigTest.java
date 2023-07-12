package com.mofum.scope.mybatis.boot.autoconfigure;

import com.mofum.scope.boot.annotation.EnableScope;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AutoConfigTest.class)
@EnableScope
@Slf4j
@ComponentScan
@EnableAutoConfiguration
@MapperScan("com.mofum.**.domain")
public class AutoConfigTest {

    @Autowired
    Environment environment;
    @Autowired
    private AutoConfig autoConfig;
    @Autowired
    private MybatisScopeAutoConfiguration configuration;

    @Test
    public void test2() {
        Assert.assertEquals(autoConfig != null, true);
        Assert.assertEquals(autoConfig.check(), true);
    }

    @Test
    public void test3() {
        Assert.assertEquals(configuration != null, true);
    }

    @ConditionalOnProperty(prefix = "mofum.mybatis.scope", name = "needMybatis", matchIfMissing = true, havingValue = "true")
    @Component
    public static class AutoConfig {
        public boolean check() {
            return true;
        }
    }
}
