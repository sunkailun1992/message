package com.kellen.message.controller;

import com.kellen.rpc.user.UserRpcDTO;
import com.kellen.rpc.user.UserRpcService;
import com.kellen.utils.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Dubbo RPC 本地验证接口。
 */
@RestController
@RequestMapping("/messages/rpc-test/users")
@Tag(name = "RPC 测试", description = "验证 message 通过 Dubbo 调用 user 服务")
@PreAuthorize("hasAuthority('user:auth:manage')")
public class UserRpcTestController {

    /**
     * 用户中心 RPC。
     */
    @DubboReference(check = false, timeout = 3000)
    private UserRpcService userRpcService;

    /**
     * 通过 Dubbo 查询 user 服务中的用户详情。
     *
     * @param userId   用户 ID
     * @param tenantId 租户 ID
     * @return 用户详情
     */
    @GetMapping("/{userId}")
    @Operation(summary = "RPC 查询用户详情", description = "message 服务通过 Dubbo 调用 user 服务，按租户和用户 ID 查询用户详情")
    public ApiResponse<UserRpcDTO> getUserByRpc(@PathVariable String userId, @RequestParam String tenantId) {
        return ApiResponse.success(userRpcService.getUserById(tenantId, userId));
    }
}
