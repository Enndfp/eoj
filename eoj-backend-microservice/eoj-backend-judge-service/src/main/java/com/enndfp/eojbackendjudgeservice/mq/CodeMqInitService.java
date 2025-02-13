package com.enndfp.eojbackendjudgeservice.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.HashMap;
import java.util.Map;

import static com.enndfp.eojbackendcommon.constant.MqConstant.*;

/**
 * RabbitMQ 初始化服务，负责初始化交换机、队列等（仅在应用启动时执行一次）
 *
 * @author Enndfp
 */
@Slf4j
@Service
public class CodeMqInitService {

    @Value("${spring.rabbitmq.host}")
    private String mqHost;

    @Value("${spring.rabbitmq.username}")
    private String mqUsername;

    @Value("${spring.rabbitmq.password}")
    private String mqPassword;

    @Value("${spring.rabbitmq.port}")
    private int mqPort;

    private Connection connection;
    private Channel channel;

    /**
     * 在 Spring Boot 启动时初始化 RabbitMQ 交换机和队列
     */
    @PostConstruct
    public void initCodeMq() {
        try {
            // 创建连接工厂并设置配置
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(mqHost);
            factory.setUsername(mqUsername);
            factory.setPassword(mqPassword);
            factory.setPort(mqPort);

            // 创建连接和通道
            connection = factory.newConnection();
            channel = connection.createChannel();

            // 创建普通交换机和普通队列，并绑定死信队列
            channel.exchangeDeclare(CODE_EXCHANGE, EXCHANGE_TYPE_DIRECT);
            Map<String, Object> codeMap = new HashMap<>();
            codeMap.put(X_DEAD_LETTER_EXCHANGE, CODE_DLX_EXCHANGE);
            codeMap.put(X_DEAD_LETTER_ROUTING_KEY, CODE_DLX_ROUTING_KEY);
            channel.queueDeclare(CODE_QUEUE, true, false, false, codeMap);
            channel.queueBind(CODE_QUEUE, CODE_EXCHANGE, CODE_ROUTING_KEY);

            // 创建死信交换机和死信队列
            channel.exchangeDeclare(CODE_DLX_EXCHANGE, EXCHANGE_TYPE_DIRECT);
            channel.queueDeclare(CODE_DLX_QUEUE, true, false, false, null);
            channel.queueBind(CODE_DLX_QUEUE, CODE_DLX_EXCHANGE, CODE_DLX_ROUTING_KEY);

            log.info("RabbitMQ 初始化成功！");
        } catch (Exception e) {
            log.error("RabbitMQ 初始化失败，原因: {}", e.getMessage(), e);
        }
    }

    /**
     * 在应用停止时关闭连接和通道
     */
    @PreDestroy
    public void closeMqConnection() {
        try {
            if (channel != null && channel.isOpen()) {
                channel.close();
            }
            if (connection != null && connection.isOpen()) {
                connection.close();
            }
            log.info("RabbitMQ 连接已关闭");
        } catch (Exception e) {
            log.error("关闭 RabbitMQ 连接时发生错误: {}", e.getMessage(), e);
        }
    }
}
