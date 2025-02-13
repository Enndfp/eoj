package com.enndfp.eojbackendjudgeservice.mq;

import com.enndfp.eojbackendcommon.common.ErrorCode;
import com.enndfp.eojbackendcommon.exception.BusinessException;
import com.enndfp.eojbackendcommon.exception.ThrowUtil;
import com.enndfp.eojbackendjudgeservice.judge.JudgeService;
import com.enndfp.eojbackendmodel.model.entity.Question;
import com.enndfp.eojbackendmodel.model.entity.QuestionSubmit;
import com.enndfp.eojbackendmodel.model.enums.QuestionSubmitStatusEnum;
import com.enndfp.eojbackendserviceclient.service.QuestionFeignClient;
import com.rabbitmq.client.Channel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;

import static com.enndfp.eojbackendcommon.constant.MqConstant.CODE_QUEUE;

/**
 * 代码评测消息队列消费者
 *
 * @author Enndfp
 */
@Slf4j
@Component
public class CodeMqConsumer {

    @Resource
    private JudgeService judgeService;

    @Resource
    private QuestionFeignClient questionFeignClient;

    /**
     * 消费者方法，监听消息队列并处理消息
     *
     * @param message     接收到的消息内容
     * @param channel     消息通道
     * @param deliveryTag 消息标识
     */
    @SneakyThrows
    @RabbitListener(queues = {CODE_QUEUE}, ackMode = "MANUAL", concurrency = "2")
    public void receiveMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        // 1. 记录接收到的消息
        log.info("接收到消息 ： {}", message);

        // 2. 如果消息为空，拒绝消息并进入死信队列
        if (message == null) {
            channel.basicNack(deliveryTag, false, false);
            throw new BusinessException(ErrorCode.NULL_ERROR, "消息为空");
        }

        Long questionSubmitId = null;
        try {
            // 3. 尝试解析消息
            questionSubmitId = Long.parseLong(message);
            judgeService.doJudge(questionSubmitId);

            // 4. 获取题目提交记录
            QuestionSubmit questionSubmit = questionFeignClient.getQuestionSubmitById(questionSubmitId);
            if (questionSubmit == null || !questionSubmit.getStatus().equals(QuestionSubmitStatusEnum.SUCCESS.getValue())) {
                // 5. 判题失败，拒绝消息并进入死信队列
                channel.basicNack(deliveryTag, false, false);
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "判题失败");
            }

            log.info("题目提交成功，提交信息：{}", questionSubmit);

            // 6. 如果题目不存在，拒绝消息并进入死信队列
            Long questionId = questionSubmit.getQuestionId();
            Question question = questionFeignClient.getQuestionById(questionId);
            if (question == null) {
                channel.basicNack(deliveryTag, false, false);
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "题目不存在");
            }

            // 7. 更新题目通过数
            Integer acceptedNum = question.getAcceptedNum();
            Question updateQuestion = new Question();
            synchronized (question) {
                acceptedNum = acceptedNum + 1;
                updateQuestion.setId(questionId);
                updateQuestion.setAcceptedNum(acceptedNum);

                boolean updateSuccess = questionFeignClient.updateQuestion(updateQuestion);
                ThrowUtil.throwIf(!updateSuccess, ErrorCode.OPERATION_ERROR, "保存题目通过数失败");
            }

            // 9. 手动确认消息
            channel.basicAck(deliveryTag, false);
        } catch (NumberFormatException e) {
            // 10. 如果消息格式错误，拒绝消息并记录错误
            channel.basicNack(deliveryTag, false, false);
            log.error("消息格式错误，无法解析提交ID: {}", message, e);
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "消息格式错误");
        } catch (IOException e) {
            // 11. 捕获IO异常，拒绝消息并记录异常
            channel.basicNack(deliveryTag, false, false);
            log.error("处理消息时发生IO异常", e);
            throw new RuntimeException(e);
        }
    }
}
