package com.mofum.common.utils.impl;

import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSubqueryTableSource;
import com.mofum.common.utils.SQLCompatibleProcessor;

/**
 * DruidSQL兼容处理器
 */
public class DruidSQLCompatibleProcessor extends DefaultSQLCompatibleProcessor implements SQLCompatibleProcessor {
    /**
     * 兼容处理SQL
     *
     * @param sql sql or sql对象
     * @param <S> 任意类型
     * @return 处理后的SQL
     */
    @Override
    public <S> S compatible(S sql) {
        if (sql instanceof SQLSelect) {
            return (S) this.compatibleSQLSelect((SQLSelect) sql);
        }
        return super.compatible(sql);
    }

    /**
     * 兼容处理SQL快
     *
     * @param sql SQL信息
     * @return SQL信息
     */
    private SQLSelect compatibleSQLSelect(SQLSelect sql) {
        SQLSelectQueryBlock queryBlock = sql.getQueryBlock();
        if (queryBlock == null) {
            return sql;
        }
        return this.doProcessSQLSubQueryTableSource(sql);
    }

    /**
     * 提取SQL信息中的子查询引用
     *
     * @param sql SQL信息
     * @return 子查询信息引用
     */
    private SQLSelect doProcessSQLSubQueryTableSource(SQLSelect sql) {
        SQLSelectQueryBlock queryBlock = sql.getQueryBlock();
        if (!(queryBlock.getFrom() instanceof SQLSubqueryTableSource)) {
            return sql;
        }
        SQLSubqueryTableSource sqlSubQuery = ((SQLSubqueryTableSource) queryBlock.getFrom());
        SQLSelect select = sqlSubQuery.getSelect();
        if (select == null) {
            return sql;
        }
        return this.doProcessSQLSubQueryTableSource(select);
    }
}
