package com.kellen.message.mq;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils;
import com.baomidou.mybatisplus.spring.MybatisSqlSessionFactoryBean;
import com.kellen.config.mq.MqAutoConfiguration;
import com.kellen.config.mq.MqContextConsumeMessageHook;
import com.kellen.config.mybatis.MyBatisPlusConfig;
import com.kellen.config.mybatis.MyMetaObjectHandler;
import com.kellen.config.mybatis.MybatisPlusSecurityProperties;
import com.kellen.database.PostgresqlSchemaFixture;
import com.kellen.datapermission.DataPermissionProperties;
import com.kellen.message.mapper.UserMessageMapper;
import com.kellen.message.mq.consumer.UserAccountCreatedConsumer;
import com.kellen.message.service.impl.UserMessageServiceImpl;
import com.kellen.message.service.query.UserMessageServiceQuery;
import com.kellen.message.service.results.UserMessageServiceResults;
import com.kellen.rpc.user.event.UserAccountCreatedEvent;
import com.kellen.security.config.TenantProperties;
import com.kellen.utils.mq.MqHeaders;
import org.apache.rocketmq.client.hook.ConsumeMessageContext;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.autoconfigure.RocketMQAutoConfiguration;
import org.apache.rocketmq.spring.support.RocketMQMessageConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.messaging.support.MessageBuilder;

import java.sql.SQLException;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * 在独立 schema 上验证“账号已创建”事件的消费：真实的 utils 消费钩子还原上下文，真实的 Mapper、租户插件与自动填充写库。
 */
@EnabledIfEnvironmentVariable(named = "FLEET_PG_TEST_URL", matches = ".+")
class UserAccountCreatedConsumerLiveTest extends PostgresqlSchemaFixture {

    /** utils 的消费钩子，与监听容器里注册的是同一个类。 */
    private final MqContextConsumeMessageHook hook = new MqContextConsumeMessageHook();

    private UserAccountCreatedConsumer consumer;

    @BeforeEach
    void wireConsumer() throws Exception {
        assertThat(migrate(null).success).isTrue();
        Properties connection = new Properties();
        connection.setProperty("currentSchema", schema + ",public");
        DriverManagerDataSource dataSource = new DriverManagerDataSource(System.getenv("FLEET_PG_TEST_URL"),
                System.getenv("FLEET_PG_TEST_USERNAME"), System.getenv("FLEET_PG_TEST_PASSWORD"));
        dataSource.setConnectionProperties(connection);
        TenantProperties tenant = new TenantProperties();
        tenant.setEnabled(true);
        tenant.setIgnoreWithoutTenant(false);
        MybatisConfiguration configuration = new MybatisConfiguration();
        configuration.setMapUnderscoreToCamelCase(true);
        GlobalConfig global = new GlobalConfig().setDbConfig(new GlobalConfig.DbConfig()).setMetaObjectHandler(new MyMetaObjectHandler(tenant));
        GlobalConfigUtils.setGlobalConfig(configuration, global);
        configuration.addMapper(UserMessageMapper.class);
        MybatisSqlSessionFactoryBean factory = new MybatisSqlSessionFactoryBean();
        factory.setDataSource(dataSource);
        factory.setConfiguration(configuration);
        factory.setGlobalConfig(global);
        factory.setPlugins(new MyBatisPlusConfig(tenant, new DataPermissionProperties(), new MybatisPlusSecurityProperties()).mybatisPlusInterceptor());
        UserMessageMapper mapper = new SqlSessionTemplate(factory.getObject()).getMapper(UserMessageMapper.class);
        consumer = new UserAccountCreatedConsumer(new UserMessageServiceImpl(mapper, mock(UserMessageServiceQuery.class),
                mock(UserMessageServiceResults.class)));
    }

    @Test
    void welcomeNoticeWrittenForNewAccount() throws Exception {
        UserAccountCreatedEvent event = event("小明");

        consume(event);

        Map<String, String> row = onlyRow(event.getEventId());
        assertThat(row).containsEntry("message_type", "SYSTEM_NOTICE")
                .containsEntry("sender_name", "system")
                .containsEntry("sender_user_id", null)
                .containsEntry("receiver_user_id", "u")
                .containsEntry("title", "欢迎使用")
                .containsEntry("content", "你好，小明，你的账号已创建。")
                .containsEntry("send_state", "SENT")
                .containsEntry("read_state", "UNREAD")
                .containsEntry("tenant_id", "t")
                .containsEntry("owner_user_id", "op")
                .containsEntry("dept_id", "d")
                .containsEntry("create_by", "op")
                .containsEntry("create_dept", "d")
                .containsEntry("source_event_id", event.getEventId());
    }

    @Test
    void usernameUsedWhenNicknameBlank() throws Exception {
        UserAccountCreatedEvent event = event(null);

        consume(event);

        assertThat(onlyRow(event.getEventId())).containsEntry("content", "你好，xiaoming，你的账号已创建。");
    }

    @Test
    void duplicateEventInsertsOnlyOneNotice() throws Exception {
        UserAccountCreatedEvent event = event("小明");

        consume(event);
        consume(event);

        assertThat(number("SELECT count(*) FROM message_user_message WHERE source_event_id = '" + event.getEventId() + "'"))
                .isEqualTo(1);
    }

    @Test
    void consumesEventWithUnknownField() throws Exception {
        String eventId = UUID.randomUUID().toString();
        String json = """
                {"eventId":"%s","occurredAt":"2026-11-20T09:30:00Z","tenantId":"t","userId":"u","username":"xiaoming",\
                "nickname":"小明","addedLater":{"any":1}}""".formatted(eventId);
        new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(RocketMQAutoConfiguration.class, MqAutoConfiguration.class))
                .withPropertyValues("rocketmq.name-server=127.0.0.1:9876")
                .run(context -> {
                    Object converted = context.getBean(RocketMQMessageConverter.class).getMessageConverter()
                            .fromMessage(MessageBuilder.withPayload(json).build(), UserAccountCreatedEvent.class);
                    assertThat(converted).isInstanceOf(UserAccountCreatedEvent.class);
                    consume((UserAccountCreatedEvent) converted);
                });

        assertThat(onlyRow(eventId)).containsEntry("content", "你好，小明，你的账号已创建。");
    }

    /** 按监听容器的顺序处理一条带上下文属性的消息：钩子还原上下文、监听器处理、钩子恢复原值。 */
    private void consume(UserAccountCreatedEvent event) {
        MessageExt message = new MessageExt();
        message.putUserProperty(MqHeaders.TENANT_ID, "t");
        message.putUserProperty(MqHeaders.USER_ID, "op");
        message.putUserProperty(MqHeaders.DEPT_ID, "d");
        ConsumeMessageContext context = new ConsumeMessageContext();
        context.setMsgList(List.of(message));
        hook.consumeMessageBefore(context);
        try {
            consumer.onMessage(event);
        } finally {
            hook.consumeMessageAfter(context);
        }
    }

    private static UserAccountCreatedEvent event(String nickname) {
        UserAccountCreatedEvent event = new UserAccountCreatedEvent();
        event.setEventId(UUID.randomUUID().toString());
        event.setOccurredAt(Instant.parse("2026-11-20T09:30:00Z"));
        event.setTenantId("t");
        event.setUserId("u");
        event.setUsername("xiaoming");
        event.setNickname(nickname);
        return event;
    }

    /** 某个事件写入的唯一一行站内信，列名到文本值。 */
    private Map<String, String> onlyRow(String eventId) throws SQLException {
        Map<String, String> row = new LinkedHashMap<>();
        try (var statement = connection.prepareStatement("SELECT message_type, sender_name, sender_user_id, receiver_user_id, title,"
                + " content, send_state, read_state, tenant_id, owner_user_id, dept_id, create_by, create_dept, source_event_id"
                + " FROM message_user_message WHERE source_event_id = ?")) {
            statement.setString(1, eventId);
            try (var result = statement.executeQuery()) {
                assertThat(result.next()).as("事件 %s 应写入一行站内信", eventId).isTrue();
                for (int column = 1; column <= result.getMetaData().getColumnCount(); column++) {
                    row.put(result.getMetaData().getColumnName(column), result.getString(column));
                }
                assertThat(result.next()).as("事件 %s 只应写入一行", eventId).isFalse();
            }
        }
        return row;
    }
}
