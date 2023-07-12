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
@SpringBootTest(classes = PermissionTest.class)
@EnableScope
@Slf4j
@ComponentScan
public class PermissionTest {

    @Autowired
    public PermissionController permissionController;

    @Test
    public void permissionTest() {
        Envs.clear();
        permissionController.test(new ScopeTest.ScopeObject("1"));
        Assert.assertEquals(EnvCheck.containValueSchema("1", "test"), true);
        Assert.assertEquals(EnvCheck.containValueSidColumn("1", "scopeId"), true);
    }

    @Test
    public void permissionTest2() {
        Envs.clear();
        permissionController.test2(new ScopeTest.ScopeObject("1"));
        Assert.assertEquals(EnvCheck.containValueSchema("1", "test"), true);
        Assert.assertEquals(EnvCheck.containValueSidColumn("1", "scope1"), true);
    }

    @Test
    public void permissionTest3() {
        Envs.clear();
        permissionController.test3(new ScopeTest.ScopeObject("1"));
        Assert.assertEquals(EnvCheck.containValueSchema("1", "test"), true);
        Assert.assertEquals(EnvCheck.containValueSidColumn("1", "scope1"), true);
    }

    @Test
    public void permissionTest4() {
        Envs.clear();
        permissionController.test4(new ScopeTest.ScopeObject("1"));
        Assert.assertEquals(EnvCheck.containValueSchema("1", "ccc"), true);
        Assert.assertEquals(EnvCheck.containValueSidColumn("1", "scope1"), true);
    }

    @Test
    public void permissionTest5() {
        Envs.clear();
        permissionController.test5(new ScopeTest.ScopeObject("1"));
        Assert.assertEquals(EnvCheck.containValueSchema("1", "test"), true);
        Assert.assertEquals(EnvCheck.containValueSidColumn("1", "scope1"), true);
    }

    @Test
    public void permissionTest6() {
        Envs.clear();
        permissionController.test6(new ScopeTest.ScopeObject("1"));
        Assert.assertEquals(EnvCheck.containValueSchema("1", "rbc"), true);
        Assert.assertEquals(EnvCheck.containValueSidColumn("1", "scope1"), true);
    }

    @Scope
    @Controller
    public static class PermissionController {

        @Autowired
        private PermissionService permissionService;

        @Autowired
        private PermissionService2 permissionService2;

        @Scope
        public String test(@SSID ScopeTest.ScopeObject object) {
            return permissionService.test(object);
        }

        @Scope
        public String test2(@SSID ScopeTest.ScopeObject object) {
            return permissionService.test2(object);
        }

        @Scope
        public String test3(@SSID ScopeTest.ScopeObject object) {
            return permissionService2.test2(object);
        }

        @Scope(schema = @Schema("ccc"))
        @SID("scope1")
        public String test4(@SSID ScopeTest.ScopeObject object) {
            return permissionService2.test3(object);
        }

        @Scope
        @SID("scope1")
        public String test5(@SSID ScopeTest.ScopeObject object) {
            return permissionService2.test4(object);
        }

        @Scope
        @SID("scope1")
        public String test6(@SSID ScopeTest.ScopeObject object) {
            return permissionService2.test5(object);
        }
    }

    @Scope
    @Data
    @NoArgsConstructor
    public static class ServiceObject {

        @SSID
        private String sid;

        public ServiceObject(String sid) {
            this.sid = sid;
        }

    }

    @Service
    @SID("scope1")
    public static class PermissionService {
        @SID("scopeId")
        @Schema("test")
        public String test(ScopeTest.ScopeObject object) {
            return "123456";
        }

        @Schema("test")
        public String test2(ScopeTest.ScopeObject object) {
            return "123456";
        }
    }

    @Service
    @Scope
    public static class PermissionService2 {
        @SID("scopeId")
        @Schema("test")
        public String test(ScopeTest.ScopeObject object) {
            return "123456";
        }

        @Schema("test")
        @SID("scope1")
        public String test2(ScopeTest.ScopeObject object) {
            return "123456";
        }

        @Schema("test")
        public String test3(ScopeTest.ScopeObject object) {
            return "123456";
        }

        @Schema("test")
        public String test4(ScopeTest.ScopeObject object) {
            return "123456";
        }

        @Schema("test")
        @SID(schema = @Schema("rbc"))
        public String test5(ScopeTest.ScopeObject object) {
            return "123456";
        }
    }
}
