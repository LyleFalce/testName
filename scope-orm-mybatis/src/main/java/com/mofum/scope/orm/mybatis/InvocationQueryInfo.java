package com.mofum.scope.orm.mybatis;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.sql.SQLException;
import java.util.List;

/**
 * SQL查询执行信息
 */
@Data
@AllArgsConstructor
public class InvocationQueryInfo {

    /**
     * 查询语句
     */
    private MappedStatement statement;

    /**
     * 参数信息
     */
    private Object parameter;

    /**
     * 行边界
     */
    private RowBounds rowBounds;

    /**
     * 结果处理器
     */
    private ResultHandler resultHandler;

    /**
     * 执行器
     */
    private Executor executor;

    /**
     * 缓存键
     */
    private CacheKey cacheKey;

    /**
     * SQL
     */
    private BoundSql boundSql;

    public InvocationQueryInfo() {
    }

    /**
     * 根据执行信息构建Query查询信息
     *
     * @param invocation 执行信息
     * @return Query查询信息
     */
    public static InvocationQueryInfo valueOf(Invocation invocation) {
        Object[] args = invocation.getArgs();
        MappedStatement ms = (MappedStatement) args[0];
        RowBounds rowBounds = (RowBounds) args[2];
        ResultHandler resultHandler = (ResultHandler) args[3];
        Executor executor = (Executor) invocation.getTarget();
        CacheKey cacheKey;
        BoundSql boundSql;
        Object parameter = args[1];
        if (args.length == 4) {
            boundSql = ms.getBoundSql(parameter);
            cacheKey = executor.createCacheKey(ms, parameter, rowBounds, boundSql);
        } else {
            cacheKey = (CacheKey) args[4];
            boundSql = (BoundSql) args[5];
        }
        return new InvocationQueryInfo(ms, parameter, rowBounds, resultHandler, executor, cacheKey, boundSql);
    }

    /**
     * 执行SQL获得结果
     *
     * @return 数据集
     * @throws SQLException SQL异常
     */
    public List<Object> execute() throws SQLException {
        return executor.query(statement, parameter, rowBounds, resultHandler, cacheKey, boundSql);
    }
}
