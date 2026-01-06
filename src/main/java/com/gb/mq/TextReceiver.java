package com.gb.mq;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
/**
 * <p>
 * TextReceiver
 * </p>
 *
 * @author 孙凯伦
 * @since 2021-04-21
 */
@Component
@RabbitListener(queues = "test")
public class TextReceiver {

    @RabbitHandler
    public void process(String json) throws Exception {
        System.out.printf(json);
    }

}