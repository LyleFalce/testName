package com.mofum.common.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import com.mofum.common.env.EnvsRule;
import com.mofum.common.meta.ColumnRule;
import com.mofum.common.meta.reflect.MethodInfo;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 数据过滤器
 */
public final class ResultFilterHelper {
    public static void doFilter(MethodInfo methodInfo) {
        Object value = methodInfo.getReturnValue();
        if (value == null) {
            return;
        }
        methodInfo.setReturnValue(doFilterProcess(value));
    }

    private static Object doFilterProcess(Object value) {
        if (ObjectUtil.isBasicType(value)) {
            return value;
        }
        if (Collection.class.isAssignableFrom(value.getClass()) && !Map.class.isAssignableFrom(value.getClass())) {
            return doCollectionFilterProcess(value);
        }
        Object result = doSingleFilterProcess(value);
        return result;
    }

    private static Object doSingleFilterProcess(Object value) {
        List<String> columns = EnvsRule.propertiesRules().stream().filter(e -> !e.queryAccess()).map(ColumnRule::getColumn).collect(Collectors.toList());
        Object result = ReflectUtil.newInstance(value.getClass());
        BeanUtil.copyProperties(value, result, columns.toArray(new String[]{}));
        return result;
    }

    private static Object doCollectionFilterProcess(Object value) {
        Collection collection = (Collection) ReflectUtil.newInstance(value.getClass());
        ((Collection<?>) value).forEach(e -> {
            if (e == null) {
                return;
            }
            collection.add(doSingleFilterProcess(e));
        });
        return collection;
    }
}
