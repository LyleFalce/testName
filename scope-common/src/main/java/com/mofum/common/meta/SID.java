package com.mofum.common.meta;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Data;

/**
 * 范围ID
 */
@Data
public class SID {

    /**
     * 范围ID值
     */
    private Object value;
    /**
     * 表信息
     */
    private SchemaInfo schema;
    /**
     * 业务ID信息
     */
    private SSID ssid;
    /**
     * 列名
     */
    private String column;
    /**
     * 列别名
     */
    private String alias;
    /**
     * 范围类型
     */
    private String scope;
    /**
     * 所属表信息
     */
    private SchemaInfo attribution;

    /**
     * 获得所属表信息
     *
     * @return 表信息
     */
    public String toAttributionString() {
        if (this.getAttribution() == null) {
            return "";
        }
        return this.getAttribution().toSchemaString();
    }

    /**
     * 获得列信息 列别名 或 列名
     *
     * @return 列名
     */
    public String toColumnString() {
        String schemaPrefix = "";
        if (this.getSchema() != null) {
            schemaPrefix = this.getSchema().toSchemaString() + ".";
        }
        if (StrUtil.isBlank(this.alias)) {
            return schemaPrefix + this.column;
        }
        return schemaPrefix + this.alias;
    }

    /**
     * 填充自身为空的数据
     *
     * @param sourceSid 源SID
     */
    public void fillSelfEmptyBySource(SID sourceSid) {
        this.fillSelfEmptyScope(sourceSid);
        this.fillSelfEmptySchema(sourceSid);
        this.fillSelfEmptyColumn(sourceSid);
        this.fillSelfEmptyAlias(sourceSid);
        this.fillSelfEmptyAttribution(sourceSid);
    }

    /**
     * 填充从属为空的数据
     *
     * @param sourceSid 源SID
     */
    private void fillSelfEmptyAttribution(SID sourceSid) {
        boolean isNeedFillAttribution = SchemaInfo.isEmpty(attribution) && SchemaInfo.isNotEmpty(sourceSid.attribution);
        if (isNeedFillAttribution) {
            this.attribution = new SchemaInfo();
            BeanUtil.copyProperties(sourceSid.attribution, this.attribution);
        }
    }

    /**
     * 填充列别名为空的数据
     *
     * @param sourceSid 源SID
     */
    private void fillSelfEmptyAlias(SID sourceSid) {
        boolean isNeedFillAlias = StrUtil.isBlank(alias) && StrUtil.isNotBlank(sourceSid.alias);
        if (isNeedFillAlias) {
            this.alias = sourceSid.alias;
        }
    }

    /**
     * 填充列名为空的数据
     *
     * @param sourceSid 源SID
     */
    private void fillSelfEmptyColumn(SID sourceSid) {
        boolean isNeedFillColumn = StrUtil.isBlank(column) && StrUtil.isNotBlank(sourceSid.column);
        if (isNeedFillColumn) {
            this.column = sourceSid.column;
        }
    }

    /**
     * 填充表为空的数据
     *
     * @param sourceSid 源SID
     */
    private void fillSelfEmptySchema(SID sourceSid) {
        boolean isNeedFillSchema = SchemaInfo.isEmpty(schema) && SchemaInfo.isNotEmpty(sourceSid.schema);
        if (isNeedFillSchema) {
            this.schema = new SchemaInfo();
            BeanUtil.copyProperties(sourceSid.schema, this.schema);
        }
    }

    /**
     * 填充范围为空的数据
     *
     * @param sourceSid 源SID
     */
    private void fillSelfEmptyScope(SID sourceSid) {
        if (StrUtil.isBlank(scope) && StrUtil.isNotBlank(sourceSid.scope)) {
            this.scope = sourceSid.scope;
        }
    }

    /**
     * 替换自身数据，如果源数据不为空
     *
     * @param sourceSid 源SID
     */
    public void replaceSelfBySource(SID sourceSid) {
        if (StrUtil.isNotBlank(sourceSid.column)) {
            this.column = sourceSid.column;
        }
        if (StrUtil.isNotBlank(sourceSid.alias)) {
            this.alias = sourceSid.alias;
        }
        if (SchemaInfo.isNotEmpty(sourceSid.schema)) {
            this.schema = sourceSid.schema;
        }
        if (SchemaInfo.isNotEmpty(sourceSid.attribution)) {
            this.attribution = sourceSid.attribution;
        }
        if (StrUtil.isNotBlank(sourceSid.scope)) {
            this.scope = sourceSid.scope;
        }
    }
}
