package com.enndfp.eoj.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * SQL 工具
 *
 * @author Enndfp
 */
public class SqlUtil {

    /**
     * 校验排序字段是否合法（防止 SQL 注入）
     *
     * @param sortField 排序字段
     * @return 是否合法
     */
    public static boolean validSortField(String sortField) {
        if (StringUtils.isBlank(sortField)) {
            return false;
        }
        return !StringUtils.containsAny(sortField, "=", "(", ")", " ");
    }
}
