package com.enndfp.eojbackendcommon.constant;

/**
 * 消息队列常量
 *
 * @author Enndfp
 */
public interface MqConstant {

    // ===================== 普通交换机和队列 =====================

    /**
     * 普通交换机名称
     */
    String CODE_EXCHANGE = "code_exchange";

    /**
     * 普通队列名称
     */
    String CODE_QUEUE = "code_queue";

    /**
     * 普通交换机路由键
     */
    String CODE_ROUTING_KEY = "code_routingKey";

    /**
     * 交换机类型：直接交换机（Direct Exchange）
     */
    String EXCHANGE_TYPE_DIRECT = "direct";


    // ===================== 死信队列 =====================

    /**
     * 死信交换机名称
     */
    String CODE_DLX_EXCHANGE = "code_dlx_exchange";

    /**
     * 死信队列名称
     */
    String CODE_DLX_QUEUE = "code_dlx_queue";

    /**
     * 死信队列路由键
     */
    String CODE_DLX_ROUTING_KEY = "code_dlx_routingKey";


    // ===================== 死信队列参数 =====================

    /**
     * 死信交换机参数（RabbitMQ配置中的“x-dead-letter-exchange”）
     */
    String X_DEAD_LETTER_EXCHANGE = "x-dead-letter-exchange";

    /**
     * 死信路由键参数（RabbitMQ配置中的“x-dead-letter-routing-key”）
     */
    String X_DEAD_LETTER_ROUTING_KEY = "x-dead-letter-routing-key";
}
