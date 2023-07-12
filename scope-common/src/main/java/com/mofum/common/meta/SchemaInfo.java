package com.mofum.common.meta;

import cn.hutool.core.util.StrUtil;
import com.mofum.common.annotation.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 表信息
 */
@Data
@NoArgsConstructor
public class SchemaInfo {
    /**
     * 表名
     */
    private String name;
    /**
     * 表别名
     */
    private String alias;

    /**
     * 根据表名构造表信息
     *
     * @param name 表名
     */
    public SchemaInfo(String name) {
        this.name = name;
    }

    /**
     * 根据表信息注解构建表信息
     *
     * @param schema 表注解
     * @return 表信息
     */
    public static SchemaInfo valueOf(Schema schema) {
        if (schema == null) {
            return null;
        }
        if (StrUtil.isBlank(schema.value())) {
            return null;
        }
        SchemaInfo schemaInfo = new SchemaInfo();
        schemaInfo.setName(schema.value());
        if (StrUtil.isNotBlank(schema.alias())) {
            schemaInfo.setAlias(schema.alias());
        }
        return schemaInfo;
    }

    public static boolean isNotEmpty(SchemaInfo schema) {
        return schema != null && !schema.isEmpty();
    }

    public static boolean isEmpty(SchemaInfo schema) {
        return !isNotEmpty(schema);
    }

    /**
     * 转表信息 别名 或 表名
     *
     * @return 表名
     */
    public String toSchemaString() {
        if (StrUtil.isBlank(alias)) {
            return StrUtil.isBlank(this.name) ? "" : this.name;
        }
        return this.alias;
    }

    public boolean isEmpty() {
        return StrUtil.isBlank(this.name);
    }
}