package com.mofum.common.meta;

/**
 * 数据过滤规则
 */
public interface DataFilterRule extends Rule {

    /**
     * 解析规则文本
     *
     * @param ruleContent 规则文本
     */
    void parse(String ruleContent);

    /**
     * 规则文本
     *
     * @return 规则文本内容
     */
    String ruleContent();

    /**
     * 更新访问
     *
     * @return true 可访问， false 不可访问
     */
    boolean updateAccess();

    /**
     * 查询访问
     *
     * @return true 可访问，false 不可访问
     */
    boolean queryAccess();

    /**
     * 删除访问
     *
     * @return true 可访问，false 不可访问
     */
    boolean deleteAccess();

    /**
     * 新增访问
     *
     * @return true 可访问,false 不可访问
     */
    boolean insertAccess();
}
