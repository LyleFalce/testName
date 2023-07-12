package com.mofum.scope.web.boot;

import com.mofum.common.annotation.SSID;
import com.mofum.common.annotation.Schema;
import com.mofum.common.annotation.Scope;
import com.mofum.common.env.EnvCheck;
import com.mofum.common.env.Envs;
import com.mofum.scope.boot.annotation.EnableScope;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SchemaTest.class)
@EnableScope
@Slf4j
@ComponentScan
public class SchemaTest {


    @Autowired
    private SchemaController schemaController;
    @Autowired
    private Schema2Controller schema2Controller;

    @Test
    public void schema() {
        Envs.clear();
        schemaController.test("223");
        Assert.assertEquals(EnvCheck.containValueSchema("223", "s2"), true);
    }

    @Test
    public void schema2() {
        Envs.clear();
        schemaController.test2("223");
        Assert.assertEquals(EnvCheck.containValueSchema("223", "schema"), true);
    }

    @Test
    public void schema3() {
        Envs.clear();
        schema2Controller.test("223");
        Assert.assertEquals(EnvCheck.containValueSchema("223", "s2"), true);
    }

    @Test
    public void schema4() {
        Envs.clear();
        schema2Controller.test2("223");
        Assert.assertEquals(EnvCheck.containValueSchema("223", "schema"), true);
    }

    @Test
    public void schema5() {
        Envs.clear();
        schema2Controller.test3("223");
        Assert.assertEquals(EnvCheck.containValueSchema("223", "o3"), true);
    }

    @Scope(value = "test", schema = @Schema("schema"))
    @Controller
    public static class SchemaController {

        @Scope(schema = @Schema("s2"))
        public String test(@SSID String sid) {
            return "123454";
        }

        @Scope
        public String test2(@SSID String sid) {
            return "123454";
        }
    }

    @Scope(value = "test")
    @Controller
    @Schema("schema")
    public static class Schema2Controller {

        @Scope(schema = @Schema("s2"))
        public String test(@SSID String sid) {
            return "123454";
        }

        @Scope
        public String test2(@SSID String sid) {
            return "123454";
        }

        @Scope
        @Schema("o3")
        public String test3(@SSID String sid) {
            return "123454";
        }
    }

}
