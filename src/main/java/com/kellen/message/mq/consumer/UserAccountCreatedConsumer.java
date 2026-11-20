package com.kellen.message.mq.consumer;

import com.kellen.message.service.UserMessageService;
import com.kellen.rpc.user.event.UserAccountCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * “账号已创建”事件的消费者：给新账号发欢迎站内信。
 *
 * <p>主题与消费组读 Nacos 的 fleet.mq 配置；非稳定实例由 utils 消息模块自动加泳道后缀。
 * 消费前 utils 的消费钩子已按消息还原租户与操作人。按事件 ID 去重，重复投递不重复写入；抛出异常时 RocketMQ 重试，3 次后进死信。</p>
 */
@Slf4j
@Component
@RocketMQMessageListener(topic = "${fleet.mq.topic.user-account-created}",
        consumerGroup = "${fleet.mq.group.user-account-created}", maxReconsumeTimes = 3)
public class UserAccountCreatedConsumer implements RocketMQListener<UserAccountCreatedEvent> {

    /**
     * 用户消息服务。
     */
    private final UserMessageService userMessageService;

    /**
     * 创建消费者。
     *
     * @param userMessageService 用户消息服务
     */
    public UserAccountCreatedConsumer(UserMessageService userMessageService) {
        this.userMessageService = userMessageService;
    }

    @Override
    public void onMessage(UserAccountCreatedEvent event) {
        if (!userMessageService.sendWelcomeNotice(event)) {
            log.info("账号已创建事件已处理过，跳过：eventId={}", event.getEventId()); // 只记事件ID，不记个人信息。
        }
    }
}
