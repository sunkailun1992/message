package com.kellen.message.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kellen.message.entity.UserMessage;
import com.kellen.message.entity.bo.UserMessageBO;
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
}
