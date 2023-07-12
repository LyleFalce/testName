package com.mofum.scope.web.boot;

import com.mofum.common.annotation.SSID;
import com.mofum.common.annotation.Scope;
import com.mofum.common.env.EnvCheck;
import com.mofum.common.env.Envs;
import com.mofum.scope.boot.annotation.EnableScope;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = ExtractTest.class)
@EnableScope
@Slf4j
@ComponentScan
public class ExtractTest {

    @Autowired
    private Controller controller;

    @Test
    public void test() {
        Envs.clear();
        controller.hello("23456");
        Assert.assertEquals(EnvCheck.containValue("23456"), true);
        Assert.assertEquals(Envs.scopeCollections().size(), 1);
    }

    @Test
    public void testObject() {
        Envs.clear();
        controller.scopeObject(new ScopeObject(Arrays.asList(new Long[]{1L, 3L})));
        Assert.assertEquals(EnvCheck.containValue(1L), true);
        Assert.assertEquals(EnvCheck.containValue(2L), false);
        Assert.assertEquals(EnvCheck.containValue(3L), true);
        Assert.assertEquals(Envs.scopeCollections().size(), 4);
    }

    @Test
    public void testObject2() {
        Envs.clear();
        ScopeObject s1 = new ScopeObject(Arrays.asList(new Long[]{1L, 3L}));
        ScopeObject s2 = new ScopeObject(Arrays.asList(new Long[]{5L, 4L}));
        List<ScopeObject> list = new ArrayList<>();
        list.add(s1);
        list.add(s2);
        controller.scopeObject2(new ScopeObj2(list));
        Assert.assertEquals(EnvCheck.containValue(1L), true);
        Assert.assertEquals(EnvCheck.containValue(2L), false);
        Assert.assertEquals(EnvCheck.containValue(3L), true);
        Assert.assertEquals(EnvCheck.containValue(4L), true);
        Assert.assertEquals(EnvCheck.containValue(5L), true);
        Assert.assertEquals(EnvCheck.containValue(6L), false);
        Assert.assertEquals(EnvCheck.containValue(7L), true);
        Assert.assertEquals(EnvCheck.containValue(8L), true);
        Assert.assertEquals(Envs.scopeCollections().size(), 8);
    }

    @Test
    public void testJson() {
        Envs.clear();
        controller.scopeJson("{\"sid\":1234}");
        Assert.assertEquals(EnvCheck.containValue(1234), true);
        Assert.assertEquals(Envs.scopeCollections().size(), 1);

    }

    @Test
    public void testJson2() {
        Envs.clear();
        controller.scopeJson("[{\"sid\":1},{\"sid\":345}]");
        Assert.assertEquals(EnvCheck.containValue(1), true);
        Assert.assertEquals(EnvCheck.containValue(345), true);
        Assert.assertEquals(Envs.scopeCollections().size(), 2);
    }

    @Test
    public void testJson3() {
        Envs.clear();
        controller.scopeJson("[1,3,4,5]");
        Assert.assertEquals(EnvCheck.containValue(1), true);
        Assert.assertEquals(EnvCheck.containValue(4), true);
        Assert.assertEquals(Envs.scopeCollections().size(), 4);
    }

    @Test
    public void testJson4() {
        Envs.clear();
        controller.scopeJsonObj("[{\"sid\":1},{\"sid\":345},5]");
        Assert.assertEquals(EnvCheck.containValue("1"), true);
        Assert.assertEquals(EnvCheck.containValue("345"), true);
        Assert.assertEquals(EnvCheck.containValue(5), true);
        Assert.assertEquals(Envs.scopeCollections().size(), 3);
    }

    @Test
    public void testJson5() {
        Envs.clear();
        controller.scopeJsonObj("[{\"sid\":1},{\"sid\":345,\"sid2\":12}]");
        Assert.assertEquals(EnvCheck.containValue("1"), true);
        Assert.assertEquals(EnvCheck.containValue("345"), true);
        Assert.assertEquals(EnvCheck.containValue(1), false);
        Assert.assertEquals(EnvCheck.containValue(345), false);
        Assert.assertEquals(EnvCheck.containValue("12"), false);
        Assert.assertEquals(EnvCheck.containValue(12), false);
        Assert.assertEquals(Envs.scopeCollections().size(), 2);
    }

    @Scope("test")
    @Component
    public static class Controller {

        @Scope
        public String hello(@SSID String serviceId) {
            return "123456";
        }

        @Scope
        public String scopeObject(@SSID ScopeObject scopeObject) {
            return "123456";
        }

        @Scope
        public String scopeObject2(@SSID ScopeObj2 scopeObj2) {
            return "123456";
        }

        @Scope
        public String scopeJson(@SSID(dataType = Map.class) String json) {
            return "123456";
        }

        @Scope
        public String scopeJsonObj(@SSID(dataType = JSONVo.class) String json) {
            return "123456";
        }
    }

    @Scope
    @Data
    public static class JSONVo {
        @SSID
        private String sid;

    }

    @Scope("obj")
    @Data
    @NoArgsConstructor
    public static class ScopeObject {

        @SSID
        private List<Long> sids;

        @SSID
        private ScopeObj4 scopeObj4 = new ScopeObj4(Arrays.asList(new ScopeObject3(Arrays.asList(new Long[]{7L, 8L}))));

        public ScopeObject(List<Long> sids) {
            this.sids = sids;
        }
    }

    @Scope("obj")
    @Data
    @NoArgsConstructor
    public static class ScopeObj2 {

        @SSID
        private List<ScopeObject> sids;

        public ScopeObj2(List<ScopeObject> sids) {
            this.sids = sids;
        }
    }

    @Scope("obj")
    @Data
    @NoArgsConstructor
    public static class ScopeObject3 {

        @SSID
        private List<Long> sids;

        public ScopeObject3(List<Long> sids) {
            this.sids = sids;
        }
    }

    @Scope("obj")
    @Data
    @NoArgsConstructor
    public static class ScopeObj4 {

        @SSID
        private List<ScopeObject3> sids;

        public ScopeObj4(List<ScopeObject3> sids) {
            this.sids = sids;
        }
    }
}
