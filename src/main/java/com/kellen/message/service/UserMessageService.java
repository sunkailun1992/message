package com.kellen.message.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kellen.message.entity.UserMessage;
import com.kellen.message.entity.bo.UserMessageBO;
import com.kellen.message.entity.enums.MessageTypeEnum;
import com.kellen.message.entity.query.UserMessageQuery;
import com.kellen.message.entity.vo.UserMessageVO;

import java.util.List;

/**
 * 用户消息业务服务。
 *
 * @author sunkailun
 * @className UserMessageService
 * @DateTime 2026/06/04
 * @email 376253703@qq.com
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
