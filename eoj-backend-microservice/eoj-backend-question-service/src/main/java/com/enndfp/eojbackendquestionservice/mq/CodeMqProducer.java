package com.enndfp.eojbackendquestionservice.mq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 代码评测消息队列生产者
 *
 * @author Enndfp
 */
@Component
public class CodeMqProducer {

    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送消息到指定交换机和队列
     *
     * @param exchange   交换机名称
     * @param routingKey 路由键
     * @param message    消息内容
     */
    public void sendMessage(String exchange, String routingKey, String message) {
        rabbitTemplate.convertAndSend(exchange, routingKey, message);
    }
}