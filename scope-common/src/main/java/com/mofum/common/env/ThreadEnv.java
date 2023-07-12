package com.mofum.common.env;

import com.mofum.common.meta.SID;
import com.mofum.common.meta.SSID;
import com.mofum.common.meta.ScopeColumnRule;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * 线程环境
 */
@Data
@AllArgsConstructor
public class ThreadEnv {

    /**
     * 范围ID集合
     */
    private List<SID> sids;

    /**
     * 业务ID集合
     */
    private List<SSID> ssids;

    /**
     * 数据库类型
     */
    private String dbType;

    /**
     * 忽略提取的类型
     */
    private List<Class> ignoreExtractTypes;

    /**
     * 过滤规则
     */
    private List<ScopeColumnRule> scopeFilterRules;

    public ThreadEnv() {
    }
}
