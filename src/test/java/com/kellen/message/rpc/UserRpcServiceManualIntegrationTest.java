package com.kellen.message.rpc;

import com.kellen.ApiApplication;
import com.kellen.rpc.user.UserRpcDTO;
import com.kellen.rpc.user.UserRpcService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * message 调用 user Dubbo RPC 的真实集成测试。
 *
 * <p>运行前需要本机 Nacos、user Dubbo provider 和测试数据就绪。</p>
 */
@Tag("manual-rpc")
@DisabledIfEnvironmentVariable(named = "CI", matches = "true")
@SpringBootTest(classes = ApiApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserRpcServiceManualIntegrationTest {

    private static final Logger log = LoggerFactory.getLogger(UserRpcServiceManualIntegrationTest.class);

    /**
     * 默认测试租户 ID，对应 user 初始化脚本中的默认租户。
     */
    private static final String DEFAULT_TENANT_ID = "100";

    /**
     * 默认测试用户 ID，对应 user 初始化脚本中的默认管理员。
     */
    private static final String DEFAULT_USER_ID = "u_admin_100";

    /**
     * 断点调试 RPC 时使用的超时时间，避免服务端暂停在断点导致 Dubbo consumer 提前超时。
     */
    private static final int DEBUG_RPC_TIMEOUT_MILLIS = 300_000;

    /**
     * 用户中心 RPC consumer 引用。
     */
    @DubboReference(check = false, timeout = DEBUG_RPC_TIMEOUT_MILLIS)
    private UserRpcService userRpcService;

    @Test
    @DisplayName("message should query user detail from user service by Dubbo RPC")
    void shouldQueryUserDetailFromUserServiceByDubboRpc() {
        String tenantId = setting("message.rpc.test.tenant-id", "MESSAGE_RPC_TEST_TENANT_ID", DEFAULT_TENANT_ID);
        String userId = setting("message.rpc.test.user-id", "MESSAGE_RPC_TEST_USER_ID", DEFAULT_USER_ID);

        UserRpcDTO user = userRpcService.getUserById(tenantId, userId);

        assertThat(user)
                .as("user RPC result should exist, tenantId=%s, userId=%s", tenantId, userId)
                .isNotNull();
        assertThat(user.getId()).isEqualTo(userId);
        assertThat(user.getTenantId()).isEqualTo(tenantId);
        assertThat(user.getUsername()).isNotBlank();
        assertThat(user.getVersion()).isNotNull();
        log.info("Dubbo RPC user result: id={}, tenantId={}, username={}, nickname={}, state={}, version={}",
                user.getId(), user.getTenantId(), user.getUsername(), user.getNickname(), user.getState(), user.getVersion());
    }

    /**
     * 先读 JVM 参数，再读环境变量，最后使用默认值，方便 IDE 和命令行两种方式手动测试。
     *
     * @param propertyName JVM 参数名
     * @param envName      环境变量名
     * @param defaultValue 默认值
     * @return 测试参数值
     */
    private static String setting(String propertyName, String envName, String defaultValue) {
        String propertyValue = System.getProperty(propertyName);
        if (propertyValue != null && !propertyValue.isBlank()) {
            return propertyValue;
        }
        String envValue = System.getenv(envName);
        if (envValue != null && !envValue.isBlank()) {
            return envValue;
        }
        return defaultValue;
    }
}
