package com.mofum.scope.orm.mybatis;

import cn.hutool.core.collection.CollectionUtil;
import com.mofum.common.env.Envs;
import com.mofum.common.env.EnvsOperation;
import com.mofum.common.utils.SQLRewriter;
import com.mofum.common.utils.impl.DefaultSQLRewriterImpl;
import lombok.Data;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.util.List;
import java.util.Properties;

/**
 * 范围拦截器
 */
@Intercepts(value = {@Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}), @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class})})
@Data
public class ScopeInterceptor implements Interceptor {

    /**
     * SQL重写器
     */
    private SQLRewriter sqlRewriter;

    /**
     * 拦截执行信息
     *
     * @param invocation 执行信息
     * @return 数据结果
     * @throws Throwable 异常
     */
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        InvocationQueryInfo invocationInfo = InvocationQueryInfo.valueOf(invocation);
        if (sqlRewriter != null) {
            this.doRewrite(invocationInfo);
        }
        return invocationInfo.execute();
    }

    /**
     * 重写操作
     *
     * @param invocationInfo 执行信息
     */
    private void doRewrite(InvocationQueryInfo invocationInfo) {
        BoundSql boundSql = invocationInfo.getBoundSql();
        String newSQLStatement = boundSql.getSql();
        if (EnvsOperation.getCleanComment()) {
            newSQLStatement = new DefaultSQLRewriterImpl().rewrite(newSQLStatement);
        }
        if (CollectionUtil.isNotEmpty(Envs.scopeCollections())) {
            newSQLStatement = sqlRewriter.rewrite(boundSql.getSql());
        }
        List<ParameterMapping> oldParameterMappings = invocationInfo.getBoundSql().getParameterMappings();
        if (newSQLStatement == null) {
            return;
        }
        boundSql = new BoundSql(invocationInfo.getStatement().getConfiguration(), newSQLStatement, boundSql.getParameterMappings(), boundSql.getParameterObject());
        this.processParameters(oldParameterMappings, boundSql);
        invocationInfo.setBoundSql(boundSql);
    }

    /**
     * 处理参数
     *
     * @param oldParameterMappings 旧参数映射
     * @param boundSql             SQL
     */
    private void processParameters(List<ParameterMapping> oldParameterMappings, BoundSql boundSql) {
        if (CollectionUtil.isEmpty(oldParameterMappings)) {
            return;
        }
        if (boundSql == null) {
            return;
        }
        oldParameterMappings.stream().filter(mapping -> boundSql.hasAdditionalParameter(mapping.getProperty())).forEach(mapping -> boundSql.setAdditionalParameter(mapping.getProperty(), boundSql.getAdditionalParameter(mapping.getProperty())));
    }

    /**
     * 插件
     *
     * @param target 目标对象
     */
    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    /**
     * 设置插件的属性
     *
     * @param properties 属性
     */
    @Override
    public void setProperties(Properties properties) {

    }
}
