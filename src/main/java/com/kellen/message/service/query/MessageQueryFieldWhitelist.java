package com.kellen.message.service.query;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Map;

/**
 * 消息查询字段白名单工具。
 * <p>
 * 前端传入的排序字段与查询字段是 SQL 标识符，不能用预编译参数绑定，只能按白名单换成数据库列名
 * （OWASP SQL Injection Prevention Cheat Sheet 的 allow-list 做法，与 user 服务的 AuthQueryFieldWhitelist 一致），
 * 原样字符串一律不进 SQL。
 *
 * @author sunkailun
 * @className MessageQueryFieldWhitelist
 * @time 2026/11/23
 */
public final class MessageQueryFieldWhitelist {

    /**
     * 私有构造方法。
     *
     * @author sunkailun
     */
    private MessageQueryFieldWhitelist() {
        // 工具类禁止实例化，所有能力通过静态方法提供。
    }

    /**
     * 解析安全排序字段。
     *
     * @param requestedField 前端请求排序字段
     * @param defaultField   默认排序字段
     * @param allowedFields  允许的字段名到数据库列名的映射
     * @return 安全排序列名
     * @author sunkailun
     */
    public static String resolveSortField(String requestedField, String defaultField, Map<String, String> allowedFields) {
        String requestedColumn = allowedFields.get(StringUtils.trimToEmpty(requestedField)); // 未命中表示字段不在白名单。
        if (StringUtils.isNotBlank(requestedColumn)) {
            return requestedColumn; // 前端字段合法时返回白名单里的列名。
        }
        String defaultColumn = allowedFields.get(StringUtils.trimToEmpty(defaultField)); // 默认字段也经白名单解析。
        if (StringUtils.isNotBlank(defaultColumn)) {
            return defaultColumn; // 返回默认排序列。
        }
        return allowedFields.get("id"); // 默认字段未登记时按主键排序，保证排序稳定。
    }

    /**
     * 解析安全查询字段集合。
     *
     * @param requestedFields 前端请求查询字段，英文逗号分隔
     * @param allowedFields   允许的字段名到数据库列名的映射
     * @return 安全查询列名数组，为空表示不改默认查询列
     * @author sunkailun
     */
    public static String[] resolveSelectFields(String requestedFields, Map<String, String> allowedFields) {
        if (StringUtils.isBlank(requestedFields)) {
            return new String[0]; // 未指定查询字段时保持默认查询列。
        }
        return Arrays.stream(requestedFields.split(","))
                .map(StringUtils::trimToEmpty) // 去掉字段两侧空白再匹配白名单。
                .map(allowedFields::get) // 字段名换成数据库列名，未命中为 null。
                .filter(StringUtils::isNotBlank) // 丢掉不在白名单的字段（含注入片段）。
                .distinct() // 同一列只查一次。
                .toArray(String[]::new);
    }
}
