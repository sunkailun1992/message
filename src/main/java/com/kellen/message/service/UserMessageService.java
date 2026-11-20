package com.kellen.message.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kellen.message.entity.UserMessage;
import com.kellen.message.entity.bo.UserMessageBO;
import com.kellen.message.entity.enums.MessageTypeEnum;
import com.kellen.message.entity.query.UserMessageQuery;
import com.kellen.message.entity.vo.UserMessageVO;
import com.kellen.rpc.user.event.UserAccountCreatedEvent;

import java.util.List;

/**
 * 用户消息业务服务。
 *
 * @author sunkailun
 * @className UserMessageService
 */
public interface UserMessageService {

    /**
     * 分页查询用户消息。
     *
     * @param page  分页对象
     * @param query 用户消息查询参数
     * @return 用户消息分页
     */
    Page<UserMessageVO> page(Page<UserMessage> page, UserMessageQuery query);

    /**
     * 查询用户消息列表。
     *
     * @param query 用户消息查询参数
     * @return 用户消息列表
     */
    List<UserMessageVO> list(UserMessageQuery query);

    /**
     * 发送用户消息。
     *
     * @param bo 用户消息发送参数
     * @return 新增消息数量
     */
    Integer send(UserMessageBO bo);

    /**
     * 给新账号发欢迎站内信（系统提醒），由“账号已创建”事件触发。
     *
     * <p>按事件 ID 与接收人去重：同一事件重复投递时不再写入，返回 false。消息归属取消费钩子还原的操作人。</p>
     *
     * @param event 账号已创建事件
     * @return true 表示写入了一条站内信；false 表示该事件已处理过
     */
    boolean sendWelcomeNotice(UserAccountCreatedEvent event);

    /**
     * 分页查询当前用户接收的消息。
     *
     * @param page  分页对象
     * @param query 用户消息查询参数
     * @return 当前用户消息分页
     */
    Page<UserMessageVO> currentPage(Page<UserMessage> page, UserMessageQuery query);

    /**
     * 查询当前用户未读消息数量。
     *
     * @param tenantId    租户ID
     * @param messageType 消息类型
     * @return 未读消息数量
     */
    Long currentUnreadCount(String tenantId, MessageTypeEnum messageType);

    /**
     * 标记当前用户消息为已读。
     *
     * @param tenantId 租户ID
     * @param id       消息ID
     * @return 是否更新成功
     */
    Boolean readCurrentMessage(String tenantId, String id);
}
