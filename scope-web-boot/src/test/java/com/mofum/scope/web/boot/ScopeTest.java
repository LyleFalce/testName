package com.mofum.scope.web.boot;

import com.mofum.common.annotation.SID;
import com.mofum.common.annotation.SSID;
import com.mofum.common.annotation.Schema;
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
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ScopeTest.class)
@EnableScope
@Slf4j
@ComponentScan
public class ScopeTest {

    @Autowired
    private DemoController demoController;
    @Autowired
    private Demo2Controller demo2Controller;

    @Test
    public void scopeInfo() {
        Envs.clear();
        demoController.test("1");
        Assert.assertEquals(EnvCheck.containValue("1"), true);
    }

    @Test
    public void scope2Info() {
        Envs.clear();
        demo2Controller.test("1");
        Assert.assertEquals(EnvCheck.containValueScope("1", "test"), true);
    }

    @Test
    public void scope2Info2() {
        Envs.clear();
        demo2Controller.test2("1");
        Assert.assertEquals(EnvCheck.containValueScope("1", "test2"), true);
    }

    @Test
    public void scope2Info3() {
        Envs.clear();
        demo2Controller.test3(new ScopeObject(new ScopeObject2("1")), "2");
        Assert.assertEquals(EnvCheck.containValueScope("1", "a2"), true);
        Assert.assertEquals(EnvCheck.containValueScope("2", "test3"), true);
    }

    @Test
    public void scope2Info4() {
        Envs.clear();
        demo2Controller.test3(new ScopeObject("1"), "2");
        Assert.assertEquals(EnvCheck.containValueScope("1", "a1"), true);
        Assert.assertEquals(EnvCheck.containValueScope("2", "test3"), true);
    }


    @Test
    public void scopeTest1() {
        Envs.clear();
        demoController.test2(new MultiScopeObject(1L, "2L"));
        Assert.assertEquals(EnvCheck.containValueSchema(1L, "d", "d"), true);
        Assert.assertEquals(EnvCheck.containValueSchema("2L", "s"), true);
    }

    @Test
    public void scopeTest2() {
        Envs.clear();
        demoController.test3(new MultiScopeObject(1L, "2L", "new"));
        Assert.assertEquals(EnvCheck.containValueSchema(1L, "d", "d"), true);
        Assert.assertEquals(EnvCheck.containValueSchema("2L", "s"), true);
        Assert.assertEquals(EnvCheck.containValueSchema("new", "t", "r"), true);
    }

    @Test
    public void scopeTest3() {
        Envs.clear();
        demoController.test4(new MultiScopeObject(1L, "2L", "new"));
        Assert.assertEquals(EnvCheck.containValueSchema(1L, "d", "d"), true);
        Assert.assertEquals(EnvCheck.containValueSchema("2L", "s"), true);
        Assert.assertEquals(EnvCheck.containValueSchema("new", "t", "r"), true);
        Assert.assertEquals(EnvCheck.containValueSidColumn(1L, "col"), true);
        Assert.assertEquals(EnvCheck.containValueSidColumn("2L", "col2"), true);
        Assert.assertEquals(EnvCheck.containValueSidColumn("new", "c"), true);
    }

    @Test
    public void scopeTest4() {
        Envs.clear();
        demoController.test5(new MultiScopeObject(1L, "2L", "new"));
        Assert.assertEquals(EnvCheck.containValueSchema(1L, "d", "d"), true);
        Assert.assertEquals(EnvCheck.containValueSchema("2L", "s"), true);
        Assert.assertEquals(EnvCheck.containValueSchema("new", "on"), true);
        Assert.assertEquals(EnvCheck.containValueSidColumn(1L, "col"), true);
        Assert.assertEquals(EnvCheck.containValueSidColumn("2L", "col2"), true);
        Assert.assertEquals(EnvCheck.containValueSidColumn("new", "c"), true);
    }

    @Scope
    @Data
    public static class MultiScopeObject {

        @SSID(sid = @SID(value = "col", schema = @Schema(value = "d", alias = "d")))
        private Long scopeId;

        @SSID(sid = @SID(value = "col2", schema = @Schema(value = "s")))
        private String scopeId2;

        @SSID
        private String scopeId3;

        public MultiScopeObject(Long scopeId, String scopeId2) {
            this.scopeId = scopeId;
            this.scopeId2 = scopeId2;
        }

        public MultiScopeObject(Long scopeId, String scopeId2, String scopeId3) {
            this.scopeId = scopeId;
            this.scopeId2 = scopeId2;
            this.scopeId3 = scopeId3;
        }
    }

    @Service
    @SID(value = "scopeId", alias = "si", schema = @Schema(value = "demo", alias = "d"))
    public static class SchemaDataService {
        @SID
        public void test(String id) {

        }
    }

    @Scope
    @Controller
    public static class DemoController {

        @Autowired
        private SchemaDataService schemaDataService;

        @Scope
        public String test(@SSID String id) {
            return "123456";
        }

        @Scope
        public String test2(@SSID MultiScopeObject object) {
            return "21232345";
        }

        @Scope
        @Schema(value = "t", alias = "r")
        public String test3(@SSID MultiScopeObject object) {
            return "21232345";
        }

        @Scope
        @Schema(value = "t", alias = "r")
        public String test4(@SSID(sid = @SID("c")) MultiScopeObject object) {
            return "21232345";
        }

        @Scope
        @Schema(value = "t", alias = "r")
        public String test5(@SSID(sid = @SID(value = "c", schema = @Schema("on"))) MultiScopeObject object) {
            return "21232345";
        }
    }

    @Scope("test")
    @Controller
    public static class Demo2Controller {

        @Autowired
        private SchemaDataService schemaDataService;

        @Scope
        public String test(@SSID String id) {
            return "123456";
        }

        @Scope("test2")
        public String test2(@SSID String id) {
            return "123456";
        }

        @Scope("test3")
        public String test3(@SSID ScopeObject scopeObject, @SSID String ssid) {
            return "123456";
        }
    }

    @Scope("a1")
    @NoArgsConstructor
    public static class ScopeObject {

        @SSID
        private ScopeObject2 scopeObject2;

        @SSID
        private String sid;

        public ScopeObject(ScopeObject2 scopeObject2) {
            this.scopeObject2 = scopeObject2;
        }

        public ScopeObject(String sid) {
            this.sid = sid;
        }
    }

    @Scope("a2")
    @NoArgsConstructor
    public static class ScopeObject2 {
        @SSID
        private String sid;

        public ScopeObject2(String sid) {
            this.sid = sid;
        }
    }
}
