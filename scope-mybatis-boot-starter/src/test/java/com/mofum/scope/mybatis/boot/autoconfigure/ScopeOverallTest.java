package com.mofum.scope.mybatis.boot.autoconfigure;

import com.mofum.common.annotation.SID;
import com.mofum.common.annotation.SSID;
import com.mofum.common.annotation.Schema;
import com.mofum.common.annotation.Scope;
import com.mofum.common.env.Envs;
import com.mofum.common.exception.AuthenticationException;
import com.mofum.common.utils.Authenticator;
import com.mofum.scope.boot.annotation.EnableScope;
import com.mofum.scope.domain.SysUser;
import com.mofum.scope.domain.mapper.SysUserMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.util.List;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = AutoConfigTest.class)
@EnableScope
@Slf4j
@ComponentScan
@MapperScan("com.mofum.**.domain")
@EnableAutoConfiguration
public class ScopeOverallTest {

    @Autowired
    private ScopeController scopeController;

    @Test
    public void test() {
        Envs.clear();
        List list = scopeController.userList2();
        Assert.assertEquals(list.size(), 6);
    }

    @Test
    public void test2() {
        Envs.clear();
        List list = scopeController.userList("103");
        Assert.assertEquals(list.size(), 3);
    }

    @Test
    public void test3() {
        Envs.clear();
        List list = scopeController.userList3();
    }

    @Autowired
    TestAuthenticator authenticator;

    @Test(expected = AuthenticationException.class)
    public void authenticatorTests() {
        Envs.clear();
        scopeController.userList4(10L, null);
    }

    @Test(expected = AuthenticationException.class)
    public void authenticatorTests2() {
        Envs.clear();
        scopeController.userList4(9L, null);
    }

    @Test
    public void authenticatorTests3() {
        Envs.clear();
        scopeController.userList4(8L, null);
    }


    @Configuration
    public static class ScopeInitConfig implements CommandLineRunner {

        @Autowired
        DataSource dataSource;

        @Override
        public void run(String... args) throws Exception {
            RunSqlScript.run(dataSource, "schema.sql");
        }
    }

    @Component("testAuthenticator")
    public static class TestAuthenticator implements Authenticator {

        @Override
        public boolean authorize(Object sid, Object ssid) throws AuthenticationException {
            if (sid.equals(10L)) {
                System.out.println("业务ID为10");
                throw new AuthenticationException("业务ID不能为10");
            }
            if (sid.equals(9L)) {
                System.out.println("业务ID为9");
                return false;
            }
            if(sid.equals(8L)){
                return true;
            }
            return false;
        }
    }

    @RestController
    @Scope
    @RequestMapping("/scope")
    public static class ScopeController {
        @Autowired
        ScopeService scopeService;

        @GetMapping("/user/list")
        @Scope
        public List<SysUser> userList(@SSID String deptId) {
            return scopeService.selectList();
        }

        @GetMapping("/user/list2")
        public List<SysUser> userList2() {
            return scopeService.selectList();
        }

        @GetMapping("/user/list3")
        public List<SysUser> userList3() {
            return scopeService.selectList2();
        }

        @GetMapping("/user/list4")
        @Scope
        public List<SysUser> userList4(@SSID(authenticator = "testAuthenticator") Long deptId, @SSID List<Long> ids) {
            return scopeService.selectList3();
        }


    }

    @Service
    @Schema(value = "sys_user", alias = "u")
    public static class ScopeService {

        @Autowired
        SysUserMapper mapper;

        @SID("dept_id")
        public List<SysUser> selectList() {
            return mapper.selectList();
        }

        public List<SysUser> selectList2() {
            return mapper.selectList2("1");
        }

        @SID("dept_id")
        public List<SysUser> selectList3() {
            return mapper.selectList();
        }

    }
}
