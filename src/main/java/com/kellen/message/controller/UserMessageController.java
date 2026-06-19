package com.kellen.message.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kellen.message.entity.UserMessage;
import com.kellen.message.entity.bo.UserMessageBO;
import com.kellen.message.entity.enums.MessageTypeEnum;
import com.kellen.message.entity.query.UserMessageQuery;
import com.kellen.message.entity.vo.UserMessageVO;
import com.kellen.message.service.UserMessageService;
import com.kellen.utils.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 用户消息请求层。
 *
 * @author sunkailun
 * @className UserMessageController
 */
@RestController
@RequestMapping("/messages/user-messages")
@Tag(name = "用户消息", description = "维护管理端发送给用户的系统提醒和正常消息")
public class UserMessageController {

    /**
     * 用户消息业务服务。
     */
    private final UserMessageService userMessageService;

    /**
     * 构造用户消息请求层。
     *
     * @param userMessageService 用户消息业务服务
     * @return void
     * @author sunkailun
     */
    public UserMessageController(UserMessageService userMessageService) {
        this.userMessageService = userMessageService; // 注入用户消息业务服务。
    }

    /**
     * 查询用户消息选项。
     *
     * @param query 用户消息查询参数
     * @return 用户消息列表
     * @author sunkailun
     */
    @GetMapping("/options")
    @PreAuthorize("hasAnyAuthority('user:auth:manage','message:user-message:list')")
    @Operation(summary = "查询用户消息选项", description = "按查询条件返回当前租户下的用户消息轻量列表")
    public ApiResponse<List<UserMessageVO>> list(@ParameterObject @Validated UserMessageQuery query) {
        return ApiResponse.success(userMessageService.list(query)); // 查询指定租户的用户消息轻量列表。
    }

    /**
     * 分页查询用户消息。
     *
     * @param query 用户消息查询参数
     * @return 用户消息分页
     * @author sunkailun
     */
    @GetMapping(params = {"current", "size"})
    @PreAuthorize("hasAnyAuthority('user:auth:manage','message:user-message:list')")
    @Operation(summary = "分页查询用户消息", description = "按查询条件分页返回当前租户下的用户消息数据")
    public ApiResponse<Page<UserMessageVO>> page(@ParameterObject @Validated(UserMessageQuery.Select.class) UserMessageQuery query) {
        Page<UserMessage> page = new Page<>(query.getCurrent(), query.getSize()); // 创建 MyBatis-Plus 分页对象。
        return ApiResponse.success(userMessageService.page(page, query)); // 查询用户消息分页并返回统一响应。
    }

    /**
     * 分页查询当前用户消息。
     *
     * @param query 用户消息查询参数
     * @return 当前用户消息分页
     */
    @GetMapping(value = "/current", params = {"current", "size"})
    @Operation(summary = "分页查询当前用户消息", description = "分页返回当前认证用户在当前租户接收的系统提醒和正常消息")
    public ApiResponse<Page<UserMessageVO>> currentPage(@ParameterObject @Validated(UserMessageQuery.Select.class) UserMessageQuery query) {
        Page<UserMessage> page = new Page<>(query.getCurrent(), query.getSize()); // 创建 MyBatis-Plus 分页对象。
        return ApiResponse.success(userMessageService.currentPage(page, query)); // 查询当前用户消息分页并返回统一响应。
    }

    /**
     * 查询当前用户未读消息数量。
     *
     * @param tenantId    租户ID
     * @param messageType 消息类型
     * @return 未读消息数量
     */
    @GetMapping("/current/unread-count")
    @Operation(summary = "查询当前用户未读消息数量", description = "返回当前认证用户在当前租户下的未读消息数量")
    public ApiResponse<Long> currentUnreadCount(@RequestParam String tenantId, @RequestParam(required = false) MessageTypeEnum messageType) {
        return ApiResponse.success(userMessageService.currentUnreadCount(tenantId, messageType)); // 查询当前用户未读消息数量。
    }

    /**
     * 标记当前用户消息已读。
     *
     * @param id       消息ID
     * @param tenantId 租户ID
     * @return 是否更新成功
     */
    @PatchMapping("/{id}/read")
    @Operation(summary = "标记当前用户消息已读", description = "仅允许当前认证用户标记自己接收的消息为已读")
    public ApiResponse<Boolean> readCurrentMessage(@PathVariable String id, @RequestParam String tenantId) {
        return ApiResponse.success(userMessageService.readCurrentMessage(tenantId, id)); // 标记当前用户消息已读。
    }

    /**
     * 发送用户消息。
     *
     * @param bo 用户消息发送参数
     * @return 发送数量
     * @author sunkailun
     */
    @PostMapping
    @PreAuthorize("hasAnyAuthority('user:auth:manage','message:user-message:send')")
    @Operation(summary = "发送用户消息", description = "给指定用户发送系统提醒或正常消息，并返回实际发送数量")
    public ApiResponse<Integer> send(@Validated @RequestBody UserMessageBO bo) {
        return ApiResponse.success(userMessageService.send(bo)); // 发送用户消息并返回发送数量。
    }
}
