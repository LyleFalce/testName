package com.mofum.common.utils;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.mofum.common.env.Envs;
import com.mofum.common.env.EnvsOperation;
import com.mofum.common.env.EnvsRule;
import com.mofum.common.meta.SID;
import com.mofum.common.meta.SchemaInfo;
import com.mofum.common.utils.impl.DruidSQLRewriter;
import org.junit.Assert;
import org.junit.Test;

public class DruidSQLRewriterTest {

    public static DbType dbTypeValueOf(String name) {
        return DbType.valueOf(name);
    }

    @Test
    public void rewriteSQLTest() {
        Envs.clear();
        String sql = "select * from tb Order By col desc Limit 1";
        SQLRewriter rewriter = new DruidSQLRewriter();
        String newSql = rewriter.rewrite(sql);
        Assert.assertEquals(newSql, sql);
    }

    @Test
    public void rewriteSQLTest2() {
        Envs.clear();
        SID sid = new SID();
        sid.setColumn("test");
        sid.setValue("2334");
        SID sid2 = new SID();
        sid2.setColumn("test");
        sid2.setValue("2335");
        Envs.addSID(sid);
        Envs.addSID(sid2);
        String sql = "select * from tb Order By col desc Limit 1";
        DruidSQLRewriter rewriter = new DruidSQLRewriter();
        rewriter.getFormatOption().setPrettyFormat(false);
        rewriter.getFormatOption().setUppCase(false);
        String newSql = rewriter.rewrite(sql);
        Assert.assertEquals(newSql, "select * from tb where test in ('2334', '2335') order by col desc limit 1");
    }

    @Test
    public void rewriteSQLTest3() {
        Envs.clear();
        SID sid = new SID();
        sid.setColumn("test");
        sid.setValue("2334");
        SID sid2 = new SID();
        sid2.setColumn("test");
        sid2.setValue("2335");
        SID sidC = new SID();
        sidC.setColumn("c");
        sidC.setValue(1);
        Envs.addSID(sid);
        Envs.addSID(sid2);
        Envs.addSID(sidC);
        String sql = "select * from tb Order By col desc Limit 1";
        DruidSQLRewriter rewriter = new DruidSQLRewriter();
        rewriter.getFormatOption().setPrettyFormat(false);
        rewriter.getFormatOption().setUppCase(false);
        String newSql = rewriter.rewrite(sql);
        String targetSql = SQLUtils.format("select * from tb where c in ('1') and test in ('2334', '2335') order by col desc limit 1", dbTypeValueOf(EnvsOperation.getDbType()), rewriter.getFormatOption());
        Assert.assertEquals(newSql, targetSql);
    }

    @Test
    public void rewriteSQLTest4() {
        Envs.clear();
        SID sid = new SID();
        sid.setColumn("test");
        sid.setValue("2334");
        SID sid2 = new SID();
        sid2.setColumn("test");
        sid2.setValue("2335");
        SID sidC = new SID();
        sidC.setColumn("c");
        sidC.setValue(1);
        Envs.addSID(sid);
        Envs.addSID(sid2);
        Envs.addSID(sidC);
        String sql = "select * from tb where 1=1 Order By col desc Limit 1";
        DruidSQLRewriter rewriter = new DruidSQLRewriter();
        rewriter.getFormatOption().setPrettyFormat(false);
        rewriter.getFormatOption().setUppCase(false);
        String newSql = rewriter.rewrite(sql);
        String targetSql = SQLUtils.format("select * from tb where 1 = 1 and (c in ('1') and test in ('2334', '2335')) order by col desc limit 1", dbTypeValueOf(EnvsOperation.getDbType()), rewriter.getFormatOption());
        Assert.assertEquals(newSql, targetSql);
    }

    @Test
    public void rewriteSQLTest5() {
        Envs.clear();
        SID sid = new SID();
        sid.setColumn("test");
        sid.setValue("2334");
        SID sid2 = new SID();
        sid2.setColumn("test");
        sid2.setValue("2335");
        SID sidC = new SID();
        sidC.setColumn("c");
        sidC.setValue(1);
        Envs.addSID(sid);
        Envs.addSID(sid2);
        Envs.addSID(sidC);
        String sql = "(select id from tb where 1=1 ) union all (select id from rbc)";
        DruidSQLRewriter rewriter = new DruidSQLRewriter();
        rewriter.getFormatOption().setPrettyFormat(false);
        rewriter.getFormatOption().setUppCase(false);
        String newSql = rewriter.rewrite(sql);
        String targetSql = SQLUtils.format("(select id from tb where 1 = 1 and (c in ('1') and test in ('2334', '2335'))) union all (select id from rbc where c in ('1') and test in ('2334', '2335'))", dbTypeValueOf(EnvsOperation.getDbType()), rewriter.getFormatOption());
        Assert.assertEquals(newSql, targetSql);
    }

    @Test
    public void rewriteSQLTest6() {
        Envs.clear();
        SID sid = new SID();
        sid.setColumn("test");
        sid.setValue("2334");
        SID sid2 = new SID();
        sid2.setColumn("test");
        sid2.setValue("2335");
        SID sidC = new SID();
        sidC.setColumn("c");
        sidC.setValue(1);
        sidC.setAttribution(new SchemaInfo("tb"));
        Envs.addSID(sid);
        Envs.addSID(sid2);
        Envs.addSID(sidC);
        String sql = "(select id from tb where 1=1 ) union all (select id from rbc)";
        DruidSQLRewriter rewriter = new DruidSQLRewriter();
        rewriter.getFormatOption().setPrettyFormat(false);
        rewriter.getFormatOption().setUppCase(false);
        String newSql = rewriter.rewrite(sql);
        String targetSql = SQLUtils.format("(select id from tb where 1 = 1 and (c in ('1') and test in ('2334', '2335'))) union all (select id from rbc where test in ('2334', '2335'))", dbTypeValueOf(EnvsOperation.getDbType()), rewriter.getFormatOption());
        Assert.assertEquals(newSql, targetSql);
    }


    @Test
    public void rewriteSQLTest7() {
        Envs.clear();
        String sql = "select id,name,test from tb Order By col desc Limit 1";
        DruidSQLRewriter rewriter = new DruidSQLRewriter();
        rewriter.getFormatOption().setPrettyFormat(false);
        rewriter.getFormatOption().setUppCase(false);
        EnvsRule.addColumnRule("id-n");
        String newSql = rewriter.rewrite(sql);
        String targetSql = SQLUtils.format("select name, test from tb order by col desc limit 1", dbTypeValueOf(EnvsOperation.getDbType()), rewriter.getFormatOption());
        Assert.assertEquals(newSql, targetSql);
    }
}
