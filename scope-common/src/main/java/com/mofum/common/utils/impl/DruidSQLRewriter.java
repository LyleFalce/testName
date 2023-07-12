package com.mofum.common.utils.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.antspark.visitor.AntsparkSchemaStatVisitor;
import com.alibaba.druid.sql.dialect.clickhouse.visitor.ClickSchemaStatVisitor;
import com.alibaba.druid.sql.dialect.db2.visitor.DB2SchemaStatVisitor;
import com.alibaba.druid.sql.dialect.h2.visitor.H2SchemaStatVisitor;
import com.alibaba.druid.sql.dialect.hive.visitor.HiveSchemaStatVisitor;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.sql.dialect.odps.visitor.OdpsSchemaStatVisitor;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.sql.dialect.phoenix.visitor.PhoenixSchemaStatVisitor;
import com.alibaba.druid.sql.dialect.postgresql.visitor.PGSchemaStatVisitor;
import com.alibaba.druid.sql.dialect.sqlserver.visitor.SQLServerSchemaStatVisitor;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.mofum.common.env.Envs;
import com.mofum.common.env.EnvsOperation;
import com.mofum.common.env.EnvsRule;
import com.mofum.common.meta.ColumnRule;
import com.mofum.common.meta.SID;
import com.mofum.common.utils.SQLCompatibleProcessor;
import com.mofum.common.utils.SQLRewriter;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Druid SQL重写器
 */
@Data
public class DruidSQLRewriter extends DefaultSQLRewriterImpl implements SQLRewriter {

    /**
     * 左括号
     */
    private static final String TOKEN_BRACKET_LEFT = " ( ";
    /**
     * 右括号
     */
    private static final String TOKEN_BRACKET_RIGHT = " ) ";
    /**
     * and
     */
    private static final String TOKEN_AND = " and ";
    /**
     * 值预留
     */
    private static final String TOKEN_STAR = "*";
    /**
     * 值预留
     */
    private static final String RESERVED_VALUE = "%s";
    /**
     * AND语句
     */
    private static final String AND_STATEMENT_TEMPLATE = TOKEN_AND + TOKEN_BRACKET_LEFT + RESERVED_VALUE + TOKEN_BRACKET_RIGHT;
    /**
     * 全局范围类型
     */
    private static final String GLOBAL_SCOPE = "";
    /**
     * SQL格式化类容
     */
    private SQLUtils.FormatOption formatOption = SQLUtils.DEFAULT_FORMAT_OPTION;
    /**
     * 兼容处理器
     */
    private SQLCompatibleProcessor compatibleProcessor;

    /**
     * 重写Where块
     *
     * @param select 查询块信息
     */
    private static void rewriteWhere(SQLSelectQueryBlock select) {
        String condition = rewriteWhereCondition(select);
        if (condition == null) {
            return;
        }
        if (select.getWhere() == null) {
            select.setWhere(SQLUtils.toSQLExpr(condition));
            return;
        }
        String where = SQLUtils.toSQLString(select.getWhere(), EnvsOperation.getDbType());
        select.setWhere(SQLUtils.toSQLExpr(StrUtil.builder().append(where).append(String.format(AND_STATEMENT_TEMPLATE, condition)).toString()));
    }

    /**
     * 重写Where 条件信息
     *
     * @param select 查询块信息
     * @return 条件字符串
     */
    private static String rewriteWhereCondition(SQLSelectQueryBlock select) {
        String tableName = lookupTableName(select);
        List<InTableExpression> tableInExprList = Envs.scopeCollections().stream().collect(Collectors.groupingBy(SID::toAttributionString)).entrySet().stream().map(InTableExpression::valueOf).collect(Collectors.toList());

        String condition = tableInExprList.stream().filter(e -> e.table.equals(tableName)).map(e -> e.toInExprString(tableName)).collect(Collectors.joining(TOKEN_AND));

        String globalCondition = tableInExprList.stream().filter(e -> e.table.equals(GLOBAL_SCOPE)).map(e -> e.toInExprString(GLOBAL_SCOPE)).collect(Collectors.joining(TOKEN_AND));
        if (StrUtil.isBlank(condition)) {
            condition = globalCondition;
        } else {
            condition = StrUtil.builder().append(condition).append(String.format(AND_STATEMENT_TEMPLATE, globalCondition)).toString();
        }
        if (StrUtil.isBlank(condition)) {
            return null;
        }
        return condition;
    }

    /**
     * 在查询块信息里查找表名
     *
     * @param select 查询块信息
     * @return 表名 或 表别名
     */
    private static String lookupTableName(SQLSelectQueryBlock select) {
        if (select.getFrom() == null) {
            return "";
        }
        if (select.getFrom() instanceof SQLExprTableSource) {
            return ((SQLExprTableSource) select.getFrom()).getExpr().toString();
        }
        return select.getFrom().getAlias();
    }

    /**
     * 重写SQL
     *
     * @param sql SQL语句
     * @return 重写后的SQL
     */
    @Override
    public String rewrite(String sql) {
        if (CollectionUtil.isEmpty(Envs.scopeCollections()) && CollectionUtil.isEmpty(EnvsRule.columnRules())) {
            return sql;
        }
        String rewriteSql = super.rewrite(sql);
        DbType dbType = dbTypeValueOf(EnvsOperation.getDbType());
        SchemaStatVisitor visitor = DruidSchemaVisitorMapping.findSchemaStatVisitor(dbType);
        List<SQLStatement> sqlStatements = SQLUtils.parseStatements(rewriteSql, dbType);
        return this.processSQL(sqlStatements, visitor, sql);
    }

    /**
     * 根据数据库名称构建数据库类型
     *
     * @param name 数据库名称
     * @return 数据库类型
     */
    private static DbType dbTypeValueOf(String name) {
        return DbType.valueOf(name);
    }

    /**
     * 处理SQL
     *
     * @param sqlStatements 语句集合
     * @param visitor       数据库方言访问者
     * @param origSql       原始SQL
     * @return SQL
     */
    private String processSQL(List<SQLStatement> sqlStatements, SchemaStatVisitor visitor, String origSql) {
        if (CollectionUtil.isEmpty(sqlStatements) || visitor == null) {
            return origSql;
        }
        sqlStatements.stream().map(e -> ((SQLSelectStatement) e).getSelect()).forEach(e -> this.rewriteSingleSQLStatement(e));
        return SQLUtils.toSQLString(sqlStatements, dbTypeValueOf(EnvsOperation.getDbType()), formatOption);
    }

    /**
     * 处理单个SQL语句
     *
     * @param select SQL语句
     */
    private void rewriteSingleSQLStatement(SQLSelect select) {
        SQLSelect resultSelect = select;
        if (compatibleProcessor != null) {
            resultSelect = compatibleProcessor.compatible(select);
        }
        SQLSelectQueryBlock queryBlock = resultSelect.getQueryBlock();
        if (queryBlock == null) {
            doRewriteOtherSQLStatement(select);
            return;
        }
        if (CollectionUtil.isNotEmpty(EnvsRule.columnRules())) {
            rewriteColumn(queryBlock);
        }
        if (CollectionUtil.isNotEmpty(Envs.scopeCollections())) {
            rewriteWhere(queryBlock);
        }
    }

    /**
     * 重写SQL列
     *
     * @param queryBlock SQL语句
     */
    private void rewriteColumn(SQLSelectQueryBlock queryBlock) {
        List<SQLSelectItem> items = queryBlock.getSelectList();
        if (CollectionUtil.isEmpty(items)) {
            return;
        }
        List<String> columns = EnvsRule.columnRules().stream().filter(e -> !e.queryAccess()).map(ColumnRule::getColumn).collect(Collectors.toList());
        items.stream().collect(Collectors.toList()).forEach(e -> rewriteSQLSelectItem(e, items, columns));
    }

    private void rewriteSQLSelectItem(SQLSelectItem e, List<SQLSelectItem> items, List<String> columns) {
        String col = SQLUtils.toSQLString(e.getExpr(), dbTypeValueOf(EnvsOperation.getDbType()));
        if (TOKEN_STAR.equals(col)) {
            return;
        }
        if (columns.contains(col)) {
            items.remove(e);
        }
    }

    /**
     * 重写其他SQL语句
     *
     * @param select SQL语句
     */
    private void doRewriteOtherSQLStatement(SQLSelect select) {
        if (select.getQuery() instanceof SQLUnionQuery) {
            SQLUnionQuery sqlUnionQuery = (SQLUnionQuery) select.getQuery();
            rewriteSingleSQLStatement(new SQLSelect(sqlUnionQuery.getLeft()));
            rewriteSingleSQLStatement(new SQLSelect(sqlUnionQuery.getRight()));
        }
    }

    /**
     * In二维表表达式
     */
    @Data
    @NoArgsConstructor
    private static class InTableExpression {

        /**
         * 表名
         */
        private String table;

        /**
         * 列表达式
         */
        private List<InColumnExpression> columns;

        /**
         * 根据表名、列表达式构建二维表表达式
         *
         * @param table   表名
         * @param columns 列表达式
         */
        public InTableExpression(String table, List<InColumnExpression> columns) {
            this.table = table;
            this.columns = columns;
        }

        /**
         * 根据Map.Entry 信息构建二维表表达式
         *
         * @param table 表实体
         * @return 二维表表达式
         */
        public static InTableExpression valueOf(Map.Entry<String, List<SID>> table) {
            List<InColumnExpression> columnExpressions = Optional.ofNullable(table.getValue()).orElse(new ArrayList<>()).stream().filter(e -> e.toColumnString() != null).collect(Collectors.groupingBy(SID::toColumnString)).entrySet().stream().map(e -> InColumnExpression.valueOf(e, table.getKey())).collect(Collectors.toList());
            return new InTableExpression(table.getKey(), columnExpressions);
        }

        /**
         * 获得IN表达式字符串
         *
         * @param tableName 表名
         * @return 表达式字符串
         */
        public String toInExprString(String tableName) {
            if (columns.isEmpty()) {
                return null;
            }
            return columns.stream().filter(e -> e.getTable().equals(tableName)).map(InColumnExpression::toInExprString).collect(Collectors.joining(TOKEN_AND));
        }
    }

    /**
     * 列表达式
     */
    @Data
    @NoArgsConstructor
    public static class InColumnExpression {
        /**
         * 表名
         */
        private String table;
        /**
         * 列名
         */
        private String column;
        /**
         * 列值
         */
        private List<Object> values;

        /**
         * 根据表名、列名、列值构建列表达式
         *
         * @param table  表名
         * @param column 列名
         * @param values 列值
         */
        public InColumnExpression(String table, String column, List<Object> values) {
            this.table = table;
            this.column = column;
            this.values = values;
        }

        /**
         * 根据Map.Entry构建列表达式
         *
         * @param entry 列实体
         * @param table 表名
         * @return 列表达式
         */
        public static InColumnExpression valueOf(Map.Entry<String, List<SID>> entry, String table) {
            List<Object> list = Optional.ofNullable(entry.getValue()).orElse(new ArrayList<>()).stream().map(SID::getValue).collect(Collectors.toList());
            return new InColumnExpression(table, entry.getKey(), list);
        }

        /**
         * 值为空检查
         *
         * @return true 空 false 不为空
         */
        public boolean isNotEmpty() {
            return CollectionUtil.isNotEmpty(values);
        }

        /**
         * 转换为SQL表达式
         *
         * @return SQL表达式
         */
        public SQLExpr toInExpr() {
            String ids = values.stream().map(e -> "'" + Convert.toStr(e) + "'").collect(Collectors.joining(","));
            String inExpr = StrUtil.builder().append(column).append(" in (").append(ids).append(")").toString();
            return SQLUtils.toSQLExpr(inExpr);
        }

        /**
         * 转换为SQL表达式字符串
         *
         * @return SQL表达式字符串
         */
        public String toInExprString() {
            return SQLUtils.toSQLString(toInExpr(), EnvsOperation.getDbType());
        }
    }

    /**
     * Druid 数据库方言映射
     */
    @NoArgsConstructor
    private static final class DruidSchemaVisitorMapping extends HashMap<DbType, SchemaStatVisitor> {
        /**
         * 默认方言
         */
        private static final DbType DEFAULT_VISITOR = DbType.other;
        /**
         * 实例
         */
        private static final DruidSchemaVisitorMapping INSTANCE = new DruidSchemaVisitorMapping();

        static {
            INSTANCE.put(DbType.mysql, new MySqlSchemaStatVisitor());
            INSTANCE.put(DbType.db2, new DB2SchemaStatVisitor());
            INSTANCE.put(DbType.oracle, new OracleSchemaStatVisitor());
            INSTANCE.put(DbType.sqlserver, new SQLServerSchemaStatVisitor());
            INSTANCE.put(DbType.postgresql, new PGSchemaStatVisitor());
            INSTANCE.put(DbType.odps, new OdpsSchemaStatVisitor());
            INSTANCE.put(DbType.h2, new H2SchemaStatVisitor());
            INSTANCE.put(DbType.mariadb, new MySqlSchemaStatVisitor());
            INSTANCE.put(DbType.hive, new HiveSchemaStatVisitor());
            INSTANCE.put(DbType.phoenix, new PhoenixSchemaStatVisitor());
            INSTANCE.put(DbType.tidb, new MySqlSchemaStatVisitor());
            INSTANCE.put(DbType.clickhouse, new ClickSchemaStatVisitor());
            INSTANCE.put(DbType.antspark, new AntsparkSchemaStatVisitor());
            INSTANCE.put(DbType.other, new SchemaStatVisitor());
        }

        /**
         * 获取Druid数据库方言映射
         *
         * @return Druid数据库方言映射
         */
        private static DruidSchemaVisitorMapping instance() {
            return INSTANCE;
        }

        /**
         * 根据数据库类型查找Druid数据库方言
         *
         * @param dbType 数据库类型
         * @return 数据库方言
         */
        private static SchemaStatVisitor findSchemaStatVisitor(DbType dbType) {
            return Optional.ofNullable(instance().get(dbType)).orElse(instance().get(DEFAULT_VISITOR));
        }
    }
}
