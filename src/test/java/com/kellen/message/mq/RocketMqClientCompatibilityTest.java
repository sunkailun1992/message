package com.kellen.message.mq;

import org.apache.rocketmq.common.MQVersion;
import org.apache.rocketmq.remoting.RPCHook;
import org.apache.rocketmq.spring.support.RocketMQUtil;
import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * message 运行时的 RocketMQ 客户端必须与 NAS 上的服务端同一版本线（5.5.1）。
 *
 * <p>Spring Cloud Alibaba 的 BOM 会把客户端管成 5.3.1，而 utils 排除了只发到 5.3.2 的 rocketmq-acl；
 * 5.3.1 里没有 ACL 签名类，监听容器按账号创建签名钩子时失败，message 启动失败。</p>
 */
class RocketMqClientCompatibilityTest {

    @Test
    void listenerAclHookComesFromServerLineClient() {
        RPCHook hook = RocketMQUtil.getRPCHookByAkSk(new MockEnvironment(), "github-dev-message", "synthetic-not-a-secret");

        assertThat(hook).isNotNull();
        assertThat(hook.getClass().getName()).isEqualTo("org.apache.rocketmq.acl.common.AclClientRPCHook");
        assertThat(hook.getClass().getProtectionDomain().getCodeSource().getLocation().toString()).contains("rocketmq-client-5.5.1");
        assertThat(MQVersion.getVersionDesc(MQVersion.CURRENT_VERSION)).isEqualTo("V5_5_1");
    }
}
