package com.enndfp.eojbackendserviceclient.service;

import com.enndfp.eojbackendmodel.model.entity.Question;
import com.enndfp.eojbackendmodel.model.entity.QuestionSubmit;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 题目服务
 *
 * @author Enndfp
 */
@FeignClient(name = "eoj-backend-question-service", path = "/api/question/inner")
public interface QuestionFeignClient {

    /**
     * 根据 ID 获取题目
     *
     * @param questionId 题目 ID
     * @return 题目
     */
    @GetMapping("/get/id")
    Question getQuestionById(@RequestParam("questionId") Long questionId);

    /**
     * 根据 ID 获取题目提交信息
     *
     * @param questionSubmitId 提交题目 ID
     * @return 提交题目信息
     */
    @GetMapping("/question_submit/get/id")
    QuestionSubmit getQuestionSubmitById(@RequestParam("questionSubmitId") Long questionSubmitId);

    /**
     * 更新题目提交信息
     *
     * @param questionSubmit 提交题目信息
     * @return 是否更新成功
     */
    @PostMapping("/question_submit/update")
    Boolean updateQuestionSubmit(@RequestBody QuestionSubmit questionSubmit);

}
