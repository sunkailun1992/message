package com.kellen.message.service.query;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kellen.message.entity.UserMessage;
import com.kellen.message.entity.query.UserMessageQuery;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 用户消息查询条件增强。
 * <p>
 * 排序字段与查询字段只按白名单换成数据库列名（见 {@link MessageQueryFieldWhitelist}），前端传来的原样字符串不进 SQL。
 *
 * @author sunkailun
 * @className UserMessageServiceQuery
 */
@Component
public class UserMessageServiceQuery {

    /**
     * 默认排序字段。
     */
    private static final String DEFAULT_SORT_FIELD = "send_date_time";

    /**
     * 允许排序和查询的字段：实体属性名与数据库列名都换成列名。
     */
    private static final Map<String, String> ALLOWED_FIELDS = Map.ofEntries(
            Map.entry("id", "id"), // 消息主键。
            Map.entry("messageType", "message_type"), // 消息类型。
            Map.entry("message_type", "message_type"), // 兼容前端传数据库列名。
            Map.entry("senderUserId", "sender_user_id"), // 发送人用户ID。
            Map.entry("sender_user_id", "sender_user_id"), // 兼容前端传数据库列名。
            Map.entry("senderName", "sender_name"), // 发送人展示名。
            Map.entry("sender_name", "sender_name"), // 兼容前端传数据库列名。
            Map.entry("receiverUserId", "receiver_user_id"), // 接收人用户ID。
            Map.entry("receiver_user_id", "receiver_user_id"), // 兼容前端传数据库列名。
            Map.entry("ownerUserId", "owner_user_id"), // 归属用户ID。
            Map.entry("owner_user_id", "owner_user_id"), // 兼容前端传数据库列名。
            Map.entry("deptId", "dept_id"), // 归属部门ID。
            Map.entry("dept_id", "dept_id"), // 兼容前端传数据库列名。
            Map.entry("title", "title"), // 消息标题。
            Map.entry("content", "content"), // 消息内容。
            Map.entry("sendState", "send_state"), // 发送状态。
            Map.entry("send_state", "send_state"), // 兼容前端传数据库列名。
            Map.entry("readState", "read_state"), // 已读状态。
            Map.entry("read_state", "read_state"), // 兼容前端传数据库列名。
            Map.entry("sendDateTime", "send_date_time"), // 发送时间。
            Map.entry("send_date_time", "send_date_time"), // 兼容前端传数据库列名。
            Map.entry("readDateTime", "read_date_time"), // 已读时间。
            Map.entry("read_date_time", "read_date_time"), // 兼容前端传数据库列名。
            Map.entry("sourceEventId", "source_event_id"), // 来源事件ID。
            Map.entry("source_event_id", "source_event_id"), // 兼容前端传数据库列名。
            Map.entry("tenantId", "tenant_id"), // 租户ID。
            Map.entry("tenant_id", "tenant_id"), // 兼容前端传数据库列名。
            Map.entry("createTime", "create_time"), // 创建时间。
            Map.entry("create_time", "create_time"), // 兼容前端传数据库列名。
            Map.entry("updateTime", "update_time"), // 更新时间。
            Map.entry("update_time", "update_time"), // 兼容前端传数据库列名。
            Map.entry("sort", "sort"), // 排序值。
            Map.entry("version", "version") // 乐观锁版本号。
    );

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
        String sortField = MessageQueryFieldWhitelist.resolveSortField(query.getCollationFields(), DEFAULT_SORT_FIELD, ALLOWED_FIELDS); // 不在白名单的字段回落默认的发送时间。
        if (Boolean.TRUE.equals(query.getCollation())) {
            queryWrapper.orderByAsc(sortField); // 显式传 true 时按升序排序。
        } else {
            queryWrapper.orderByDesc(sortField); // 消息默认按最新发送时间倒序展示。
        }
        String[] selectFields = MessageQueryFieldWhitelist.resolveSelectFields(query.getFields(), ALLOWED_FIELDS); // 只保留白名单内的查询列。
        if (selectFields.length > 0) {
            queryWrapper.select(selectFields); // 有合法查询列时才控制查询列，否则保持默认的全部列。
        }
        return queryWrapper; // 返回完整查询包装器。
    }
}
