package com.kellen.bean;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kellen.utils.datasource.DataSourceUtil;
import com.kellen.utils.context.DynamicSourceTtl;
import jodd.util.StringUtil;
import org.slf4j.MDC;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.Objects;

/**
 * <p>
 * MQ队列配置项
 * </p>
 *
 * @author 孙凯伦
 * @since 2021-04-02
 */
@Configuration
public class RabbitConfig {

    /**
     * RabbitMQ 虚拟主机配置项名称。
     */
    private static final String RABBITMQ_VIRTUAL_HOST = "rabbitmq.virtualHost";

    /**
     * rabbitmq
     */
    @Value("${rabbitmq.host}")
    private String rabbitmqHost;
    @Value("${rabbitmq.port}")
    private Integer rabbitmqPort;
    @Value("${rabbitmq.username}")
    private String rabbitmqUsername;
    @Value("${rabbitmq.password}")
    private String rabbitmqPassword;
    @Autowired
    private ConfigurableApplicationContext applicationContext;

    /**
     * 测试
     * @return
     */
    @Bean
    public Queue test() {
        return new Queue("test");
    }


    /**
     * rabbitmq 处理连接端口
     * @return
     */
    @Bean
    public CachingConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(rabbitmqHost,rabbitmqPort);
        connectionFactory.setUsername(rabbitmqUsername);
        connectionFactory.setPassword(rabbitmqPassword);
        if(StringUtil.isNotBlank(applicationContext.getEnvironment().getProperty(RABBITMQ_VIRTUAL_HOST))) {
            connectionFactory.setVirtualHost(applicationContext.getEnvironment().getProperty(RABBITMQ_VIRTUAL_HOST));
        }
        return connectionFactory;
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        RabbitAdmin admin = new RabbitAdmin(connectionFactory);
        admin.setAutoStartup(true);
        return admin;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setUseDirectReplyToContainer(false);
        //发送之前加一个拦截器，每次发送都会调用这个方法，方法名称已经说明了一切了
        template.setBeforePublishPostProcessors(new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                //拦截逻辑添加环境变量
                message.getMessageProperties().getHeaders().put(DataSourceUtil.DATA_SOURCE, DynamicSourceTtl.get());
                message.getMessageProperties().getHeaders().put("traceId", MDC.get("traceId"));
                return message;
            }
        });
        template.setMessageConverter(messageConverter());
        return template;
    }


    @Bean(name = "rabbitListenerContainerFactory")
    @ConditionalOnProperty(prefix = "spring.rabbitmq.listener", name = "type", havingValue = "simple", matchIfMissing = true)
    public SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory(SimpleRabbitListenerContainerFactoryConfigurer configurer,
                                                                                     ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        //消息接收之前加拦截处理，每次接收消息都会调用，标记消息的，先存到副本变量，后续的操作数据库根据这个变量进行切换影子库
        factory.setAfterReceivePostProcessors(new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                Map header = message.getMessageProperties().getHeaders();
                //判断是动态切换影子库
                if (StringUtil.isNotBlank(String.valueOf(header.get(DataSourceUtil.DATA_SOURCE)))){
                    DynamicSourceTtl.push(String.valueOf(header.get(DataSourceUtil.DATA_SOURCE)));
                }
                if (Objects.nonNull(header.get("traceId"))){
                    MDC.put("traceId", String.valueOf(header.get("traceId")));
                }
                return message;
            }
        });
        configurer.configure(factory, connectionFactory);
        return factory;
    }


    @Bean
    public MessageConverter messageConverter(){
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return new Jackson2JsonMessageConverter(objectMapper);
    }

}
