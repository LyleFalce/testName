package com.mofum.scope.mybatis.boot.autoconfigure;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 运行Sql脚本
 * sql脚本放在resources下的sql文件夹下
 */
@RunWith(SpringRunner.class)
public final class RunSqlScript {

    public static void run(DataSource dataSource, String sqlFilename) {
        try {
            Connection conn = dataSource.getConnection();
            // 创建ScriptRunner，用于执行SQL脚本
            ScriptRunner runner = new ScriptRunner(conn);
            runner.setErrorLogWriter(null);
            runner.setLogWriter(null);
            // 执行SQL脚本
            runner.runScript(Resources.getResourceAsReader("sql/" + sqlFilename));

            // 关闭连接
            conn.close();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }
}