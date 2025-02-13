package com.enndfp.eojbackendjudgeservice.mq;

import com.enndfp.eojbackendmodel.model.entity.QuestionSubmit;
import com.enndfp.eojbackendmodel.model.enums.QuestionSubmitStatusEnum;
import com.enndfp.eojbackendserviceclient.service.QuestionFeignClient;
import com.rabbitmq.client.Channel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;

import static com.enndfp.eojbackendcommon.constant.MqConstant.CODE_DLX_QUEUE;


/**
 * 处理死信队列中的消息
 *
 * @author Enndfp
 */
@Component
@Slf4j
public class CodeMqDeadLetterConsumer {

    @Resource
    private QuestionFeignClient questionFeignClient;

    /**
     * 监听死信队列，处理失败的消息
     *
     * @param message  死信队列中的消息内容
     * @param channel  消息通道
     * @param delivery 消息标识
     */
    @SneakyThrows
    @RabbitListener(queues = {CODE_DLX_QUEUE}, ackMode = "MANUAL")
    public void receiveMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long delivery) {
        // 1. 检查消息是否为空或为空白
        if (StringUtils.isBlank(message)) {
            rejectMessage(channel, delivery, "消息为空");
            return;
        }

        long questionSubmitId;
        try {
            // 2. 尝试解析消息为提交 ID
            questionSubmitId = Long.parseLong(message);
        } catch (NumberFormatException e) {
            // 3. 如果消息格式错误，拒绝消息并记录错误
            rejectMessage(channel, delivery, "消息格式错误");
            return;
        }

        // 4. 获取题目提交记录
        QuestionSubmit questionSubmit = questionFeignClient.getQuestionSubmitById(questionSubmitId);
        if (questionSubmit == null) {
            // 5. 提交记录不存在，拒绝消息
            rejectMessage(channel, delivery, "提交的题目信息不存在");
            return;
        }

        // 6. 更新题目提交状态为失败
        questionSubmit.setStatus(QuestionSubmitStatusEnum.FAILED.getValue());
        boolean update = questionFeignClient.updateQuestionSubmit(questionSubmit);
        if (!update) {
            // 7. 更新失败，拒绝消息并记录日志
            log.error("处理死信队列消息失败, 提交的题目ID为: {}", questionSubmit.getId());
            rejectMessage(channel, delivery, "处理死信队列消息失败");
            return;
        }

        // 8. 成功处理消息，确认消息
        channel.basicAck(delivery, false);
        log.info("成功处理死信队列消息，提交ID为: {}", questionSubmitId);
    }

    /**
     * 拒绝消息并记录错误信息
     *
     * @param channel  消息通道
     * @param delivery 消息标识
     * @param errorMsg 错误信息
     */
    private void rejectMessage(Channel channel, long delivery, String errorMsg) {
        try {
            channel.basicNack(delivery, false, false);
            log.error("拒绝死信队列消息，原因: {}", errorMsg);
        } catch (IOException e) {
            log.error("拒绝消息失败，消息ID: {}", delivery, e);
        }
    }
}
