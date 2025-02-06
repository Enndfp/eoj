package com.enndfp.eojbackendquestionservice.controller.inner;

import com.enndfp.eojbackendmodel.model.entity.Question;
import com.enndfp.eojbackendmodel.model.entity.QuestionSubmit;
import com.enndfp.eojbackendquestionservice.service.QuestionService;
import com.enndfp.eojbackendquestionservice.service.QuestionSubmitService;
import com.enndfp.eojbackendserviceclient.service.QuestionFeignClient;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 题目内部接口
 *
 * @author Enndfp
 */
@RestController
@RequestMapping("/inner")
public class QuestionInnerController implements QuestionFeignClient {

    @Resource
    private QuestionService questionService;

    @Resource
    private QuestionSubmitService questionSubmitService;

    /**
     * 根据 ID 获取题目
     *
     * @param questionId 题目 ID
     * @return 题目
     */
    @Override
    @GetMapping("/get/id")
    @ApiOperation(value = "根据 ID 获取题目")
    public Question getQuestionById(@RequestParam("questionId") Long questionId) {
        return questionService.getById(questionId);
    }

    /**
     * 根据 ID 获取题目提交信息
     *
     * @param questionSubmitId 提交题目 ID
     * @return 提交题目信息
     */
    @Override
    @GetMapping("/question_submit/get/id")
    @ApiOperation(value = "根据 ID 获取题目提交信息")
    public QuestionSubmit getQuestionSubmitById(@RequestParam("questionSubmitId") Long questionSubmitId) {
        return questionSubmitService.getById(questionSubmitId);
    }

    /**
     * 更新题目提交信息
     *
     * @param questionSubmit 提交题目信息
     * @return 是否更新成功
     */
    @Override
    @PostMapping("/question_submit/update")
    @ApiOperation(value = "更新题目提交信息")
    public Boolean updateQuestionSubmit(@RequestBody QuestionSubmit questionSubmit) {
        return questionSubmitService.updateById(questionSubmit);
    }

}
