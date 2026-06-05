package com.kellen.message.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kellen.message.entity.UserMessage;
import com.kellen.message.entity.bo.UserMessageBO;
import com.kellen.message.entity.enums.MessageReadStateEnum;
import com.kellen.message.entity.enums.MessageSendStateEnum;
import com.kellen.message.entity.query.UserMessageQuery;
import com.kellen.message.entity.vo.UserMessageVO;
import com.kellen.message.mapper.UserMessageMapper;
import com.kellen.message.service.UserMessageService;
import com.kellen.message.service.query.UserMessageServiceQuery;
import com.kellen.message.service.results.UserMessageServiceResults;
import com.kellen.security.SecurityUser;
import com.kellen.security.UserContextHolder;
import com.kellen.utils.context.TenantContextHolder;
import com.kellen.utils.convert.GeneralConvertor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * 用户消息业务服务实现。
 *
 * @author sunkailun
 * @className UserMessageServiceImpl
 * @DateTime 2026/06/04
 * @email 376253703@qq.com
 */
@Service
public class UserMessageServiceImpl implements UserMessageService {

    /**
     * 用户消息Mapper。
     */
    private final UserMessageMapper userMessageMapper;

    /**
     * 用户消息查询增强。
     */
    private final UserMessageServiceQuery userMessageServiceQuery;

    /**
     * 用户消息结果增强。
     */
    private final UserMessageServiceResults userMessageServiceResults;

    /**
     * 构造用户消息业务服务。
     *
     * @param userMessageMapper         用户消息Mapper
     * @param userMessageServiceQuery   用户消息查询增强
     * @param userMessageServiceResults 用户消息结果增强
     * @return void
     * @author sunkailun
     * @DateTime 2026/06/04
     * @email 376253703@qq.com
     */
    public UserMessageServiceImpl(UserMessageMapper userMessageMapper, UserMessageServiceQuery userMessageServiceQuery, UserMessageServiceResults userMessageServiceResults) {
        this.userMessageMapper = userMessageMapper; // 保存用户消息Mapper。
        this.userMessageServiceQuery = userMessageServiceQuery; // 保存用户消息查询增强。
        this.userMessageServiceResults = userMessageServiceResults; // 保存用户消息结果增强。
    }

    /**
     * 分页查询用户消息。
     *
     * @param page  分页对象
     * @param query 用户消息查询参数
     * @return com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.kellen.message.entity.vo.UserMessageVO>
     * @author sunkailun
     * @DateTime 2026/06/04
     * @email 376253703@qq.com
     */
    @Override
    public Page<UserMessageVO> page(Page<UserMessage> page, UserMessageQuery query) {
        try {
            TenantContextHolder.setTenantId(query.getTenantId()); // 设置目标租户上下文。
            Page<UserMessage> pageDO = userMessageMapper.selectPage(page, buildQueryWrapper(query)); // 执行分页查询。
            Page<UserMessageVO> pageVO = userMessageServiceResults.toPageVO(pageDO); // 转换为响应分页。
            return needAssignment(query) ? userMessageServiceResults.assignment(pageVO) : pageVO; // 根据查询参数决定是否执行结果增强。
        } finally {
            TenantContextHolder.clear(); // 清理租户上下文，避免线程复用串租户。
        }
    }

    /**
     * 查询用户消息列表。
     *
     * @param query 用户消息查询参数
     * @return java.util.List<com.kellen.message.entity.vo.UserMessageVO>
     * @author sunkailun
     * @DateTime 2026/06/04
     * @email 376253703@qq.com
     */
    @Override
    public List<UserMessageVO> list(UserMessageQuery query) {
        try {
            TenantContextHolder.setTenantId(query.getTenantId()); // 设置目标租户上下文。
            List<UserMessage> records = userMessageMapper.selectList(buildQueryWrapper(query)); // 查询用户消息实体列表。
            List<UserMessageVO> voRecords = userMessageServiceResults.toListVO(records); // 转换为响应列表。
            return needAssignment(query) ? userMessageServiceResults.assignment(voRecords) : voRecords; // 根据查询参数决定是否执行结果增强。
        } finally {
            TenantContextHolder.clear(); // 清理租户上下文，避免线程复用串租户。
        }
    }

    /**
     * 发送用户消息。
     *
     * @param bo 用户消息发送参数
     * @return java.lang.Integer
     * @author sunkailun
     * @DateTime 2026/06/04
     * @email 376253703@qq.com
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer send(UserMessageBO bo) {
        try {
            TenantContextHolder.setTenantId(bo.getTenantId()); // 设置目标租户上下文。
            SecurityUser currentUser = UserContextHolder.get(); // 读取当前认证用户，作为消息发送人。
            LocalDateTime now = LocalDateTime.now(); // 统一本批消息发送时间。
            List<String> receiverUserIds = bo.getReceiverUserIds().stream().filter(StringUtils::isNotBlank).distinct().toList(); // 去除空接收人并去重。
            for (String receiverUserId : receiverUserIds) {
                UserMessage message = buildSendMessage(bo, currentUser, receiverUserId, now); // 构建单个接收人的消息实体。
                userMessageMapper.insert(message); // 插入用户消息记录。
            }
            return receiverUserIds.size(); // 返回实际发送用户数量。
        } finally {
            TenantContextHolder.clear(); // 清理租户上下文，避免线程复用串租户。
        }
    }

    /**
     * 构建发送消息实体。
     *
     * @param bo             用户消息发送参数
     * @param currentUser    当前认证用户
     * @param receiverUserId 接收人用户ID
     * @param now            发送时间
     * @return com.kellen.message.entity.UserMessage
     * @author sunkailun
     * @DateTime 2026/06/04
     * @email 376253703@qq.com
     */
    private UserMessage buildSendMessage(UserMessageBO bo, SecurityUser currentUser, String receiverUserId, LocalDateTime now) {
        UserMessage message = GeneralConvertor.convertor(bo, UserMessage.class); // 将 BO 基础字段转换为实体。
        message.setReceiverUserId(receiverUserId); // 写入当前循环接收人。
        message.setOwnerUserId(receiverUserId); // 数据权限负责人按接收人归属，便于后续按用户过滤。
        message.setSenderUserId(currentUser == null ? null : currentUser.getUserId()); // 写入当前发送人用户ID。
        message.setSenderName(currentUser == null ? "system" : StringUtils.defaultIfBlank(currentUser.getUsername(), currentUser.getUserId())); // 写入发送人展示名。
        message.setSendState(MessageSendStateEnum.SENT); // 当前实现为同步入库发送，入库成功即视为已发送。
        message.setReadState(MessageReadStateEnum.UNREAD); // 新消息默认未读。
        message.setSendDateTime(now); // 写入统一发送时间。
        return message; // 返回可入库实体。
    }

    /**
     * 构建用户消息查询包装器。
     *
     * @param query 用户消息查询参数
     * @return com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<com.kellen.message.entity.UserMessage>
     * @author sunkailun
     * @DateTime 2026/06/04
     * @email 376253703@qq.com
     */
    private QueryWrapper<UserMessage> buildQueryWrapper(UserMessageQuery query) {
        UserMessage entity = GeneralConvertor.convertor(query, UserMessage.class); // 将查询参数转换为实体。
        if (entity != null) {
            entity.setTenantId(null); // 租户条件由租户插件处理，避免重复拼 tenant_id。
        }
        QueryWrapper<UserMessage> queryWrapper = entity == null ? new QueryWrapper<>() : new QueryWrapper<>(entity); // 创建查询包装器。
        userMessageServiceQuery.query(query, queryWrapper); // 拼接公共查询条件。
        if (query != null && StringUtils.isNotBlank(query.getQuery())) {
            queryWrapper.and(wrapper -> wrapper.like("title", query.getQuery()).or().like("content", query.getQuery())); // 拼接标题和内容关键字查询。
        }
        return queryWrapper; // 返回完整查询包装器。
    }

    /**
     * 判断是否需要结果增强。
     *
     * @param query 用户消息查询参数
     * @return boolean
     * @author sunkailun
     * @DateTime 2026/06/04
     * @email 376253703@qq.com
     */
    private boolean needAssignment(UserMessageQuery query) {
        if (Objects.isNull(query)) {
            return true; // 查询对象为空时默认执行结果增强。
        }
        return !Boolean.FALSE.equals(query.getAssignment()); // assignment 明确传 false 时跳过结果增强，其余情况默认增强。
    }
}
