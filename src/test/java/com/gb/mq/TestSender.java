package com.gb.mq;

import com.gb.SpringTest;
import org.junit.Test;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

/**
 * @ClassName TestSender
 * @Description rabbitmq测试发送
 * @Author 孙凯伦
 * @Mobile 13777579028
 * @Email 376253703@qq.com
 * @Time 2021/4/21 10:51 上午
 */
public class TestSender extends SpringTest {
    @Autowired
    private AmqpTemplate rabbitTemplate;

    @Test
    public void send() {
        String context = "hello " + new Date();
        System.out.println("Sender : " + context);
        rabbitTemplate.convertAndSend("test", context);
    }
}
