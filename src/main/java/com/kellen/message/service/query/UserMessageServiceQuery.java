package com.kellen.message.service.query;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kellen.message.entity.UserMessage;
import com.kellen.message.entity.query.UserMessageQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * 用户消息查询条件增强。
 *
 * @author sunkailun
 * @className UserMessageServiceQuery
 * @DateTime 2026/06/04
 * @email 376253703@qq.com
 */
@Component
public class UserMessageServiceQuery {

    /**
     * 默认排序字段。
     */
    private static final String DEFAULT_SORT_FIELD = "send_date_time";

    /**
     * 拼接用户消息公共查询条件。
     *
     * @param query        用户消息查询参数
     * @param queryWrapper 查询包装器
     * @return 查询包装器
     */
    public QueryWrapper<UserMessage> query(UserMessageQuery query, QueryWrapper<UserMessage> queryWrapper) {
        if (query == null) {
            return queryWrapper; // 查询参数为空时直接返回已有包装器。
        }
        String sortField = StringUtils.defaultIfBlank(query.getCollationFields(), DEFAULT_SORT_FIELD); // 计算排序字段，默认按发送时间排序。
        if (Boolean.TRUE.equals(query.getCollation())) {
            queryWrapper.orderByAsc(sortField); // 显式传 true 时按升序排序。
        } else {
            queryWrapper.orderByDesc(sortField); // 消息默认按最新发送时间倒序展示。
        }
        if (StringUtils.isNotBlank(query.getFields())) {
            queryWrapper.select(query.getFields()); // 指定查询字段非空时控制查询列。
        }
        return queryWrapper; // 返回完整查询包装器。
    }
}
