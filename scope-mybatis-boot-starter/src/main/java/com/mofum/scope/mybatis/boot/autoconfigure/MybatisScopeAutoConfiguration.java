package com.mofum.scope.mybatis.boot.autoconfigure;

import cn.hutool.core.collection.CollectionUtil;
import com.mofum.common.utils.SQLCompatibleProcessor;
import com.mofum.common.utils.SQLRewriter;
import com.mofum.common.utils.impl.DefaultSQLCompatibleProcessor;
import com.mofum.common.utils.impl.DruidSQLRewriter;
import com.mofum.scope.orm.mybatis.ScopeInterceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ApplicationObjectSupport;

import javax.annotation.PostConstruct;
import java.util.Iterator;
import java.util.List;

/**
 * Mybatis 范围配置
 */
@Configuration
@ConditionalOnClass({SqlSessionFactory.class})
@EnableConfigurationProperties(AutoMybatisScopeProperties.class)
@ConditionalOnProperty(prefix = "mofum.scope.mybatis", name = "enable", matchIfMissing = true, havingValue = "true")
public class MybatisScopeAutoConfiguration extends ApplicationObjectSupport {

    /**
     * SQL会话工厂
     */
    @Autowired
    private List<SqlSessionFactory> sqlSessionFactoryList;

    /**
     * 不存在SQL重写器构建SQL重写器
     *
     * @param sqlCompatibleProcessor SQL兼容器
     * @return SQL重写器
     */
    @Bean
    @ConditionalOnMissingBean(SQLRewriter.class)
    public SQLRewriter sqlRewriter(SQLCompatibleProcessor sqlCompatibleProcessor) {
        DruidSQLRewriter sqlRewriter = new DruidSQLRewriter();
        sqlRewriter.setCompatibleProcessor(sqlCompatibleProcessor);
        return sqlRewriter;
    }

    /**
     * 不存在SQL兼容器，构建SQL兼容器
     *
     * @return SQL兼容器
     */
    @Bean
    @ConditionalOnMissingBean(SQLCompatibleProcessor.class)
    public SQLCompatibleProcessor sqlCompatibleProcessor() {
        return new DefaultSQLCompatibleProcessor();
    }

    /**
     * 注册范围SQL拦截器
     */
    @PostConstruct
    public void registerScopeInterceptor() {
        if (CollectionUtil.isEmpty(this.sqlSessionFactoryList)) {
            return;
        }
        ScopeInterceptor interceptor = new ScopeInterceptor();
        interceptor.setSqlRewriter(this.getApplicationContext().getBean(SQLRewriter.class));
        Iterator<SqlSessionFactory> iterator = this.sqlSessionFactoryList.iterator();
        while (iterator.hasNext()) {
            doRegisterScopeInterceptor(iterator.next(), interceptor);
        }
    }

    /**
     * 存在ScopeInterceptor 则不添加
     *
     * @param interceptor 拦截器
     */
    private void doRegisterScopeInterceptor(SqlSessionFactory sqlSessionFactory, ScopeInterceptor interceptor) {
        boolean exists = sqlSessionFactory.getConfiguration().getInterceptors().contains(interceptor);
        if (exists) {
            return;
        }
        sqlSessionFactory.getConfiguration().addInterceptor(interceptor);
    }

}
