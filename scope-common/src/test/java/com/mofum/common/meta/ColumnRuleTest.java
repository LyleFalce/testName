package com.mofum.common.meta;

import com.mofum.common.env.EnvsRule;
import com.mofum.common.meta.reflect.MethodInfo;
import com.mofum.common.utils.ResultFilterHelper;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class ColumnRuleTest {

    @Test
    public void testRule() {
        ColumnRule columnRule = new ColumnRule();
        columnRule.parse("col-qu");
        Assert.assertEquals(columnRule.deleteAccess(), false);
        Assert.assertEquals(columnRule.insertAccess(), false);
        Assert.assertEquals(columnRule.updateAccess(), true);
        Assert.assertEquals(columnRule.queryAccess(), true);
        Assert.assertEquals(columnRule.getColumn(), "col");
    }

    @Test
    public void testRule2() {
        ColumnRule columnRule = new ColumnRule();
        columnRule.parse("col");
        Assert.assertEquals(columnRule.deleteAccess(), false);
        Assert.assertEquals(columnRule.insertAccess(), false);
        Assert.assertEquals(columnRule.updateAccess(), false);
        Assert.assertEquals(columnRule.queryAccess(), false);
        Assert.assertEquals(columnRule.getColumn(), null);
    }

    @Test
    public void testRule3() {
        ColumnRule columnRule = new ColumnRule();
        columnRule.parse("col-quiddcvf");
        Assert.assertEquals(columnRule.deleteAccess(), true);
        Assert.assertEquals(columnRule.insertAccess(), true);
        Assert.assertEquals(columnRule.updateAccess(), true);
        Assert.assertEquals(columnRule.queryAccess(), true);
        Assert.assertEquals(columnRule.getColumn(), "col");
    }

    @Test
    public void testRule4() {
        ColumnRule columnRule = new ColumnRule();
        columnRule.parse("col-quiddcvf-2334");
        Assert.assertEquals(columnRule.deleteAccess(), false);
        Assert.assertEquals(columnRule.insertAccess(), false);
        Assert.assertEquals(columnRule.updateAccess(), false);
        Assert.assertEquals(columnRule.queryAccess(), false);
        Assert.assertEquals(columnRule.getColumn(), null);
    }

    @Data
    @NoArgsConstructor
    public static class ExampleData {
        private String col;
        private String col2;

        public ExampleData(String col, String col2) {
            this.col = col;
            this.col2 = col2;
        }
    }

    @Test
    public void testRule5() {
        MethodInfo methodInfo = new MethodInfo();
        methodInfo.setReturnValue(new ExampleData("1", "2"));
        EnvsRule.addPropertiesRule("col-n");
        ResultFilterHelper.doFilter(methodInfo);
        System.out.println(methodInfo.getReturnValue());
    }

    @Test
    public void testRule6() {
        MethodInfo methodInfo = new MethodInfo();
        List<ExampleData> list = new ArrayList<>();
        list.add(new ExampleData("1", "1"));
        list.add(new ExampleData("2", "2"));
        list.add(new ExampleData("3", "3"));
        list.add(new ExampleData("4", "4"));
        methodInfo.setReturnValue(list);
        EnvsRule.addPropertiesRule("col-n");
        ResultFilterHelper.doFilter(methodInfo);
        System.out.println(methodInfo.getReturnValue());
    }
}
