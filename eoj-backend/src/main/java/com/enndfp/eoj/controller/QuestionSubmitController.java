package com.enndfp.eoj.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enndfp.eoj.common.BaseResponse;
import com.enndfp.eoj.common.ErrorCode;
import com.enndfp.eoj.common.ResultUtil;
import com.enndfp.eoj.exception.ThrowUtil;
import com.enndfp.eoj.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.enndfp.eoj.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.enndfp.eoj.model.vo.QuestionSubmitVO;
import com.enndfp.eoj.service.QuestionSubmitService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 题目提交接口
 *
 * @author Enndfp
 */
@Slf4j
@RestController
@RequestMapping("/question_submit")
@ApiIgnore
public class QuestionSubmitController {

    @Resource
    private QuestionSubmitService questionSubmitService;

    /**
     * 提交题目
     *
     * @param questionSubmitAddRequest 提交题目请求
     * @param request                  请求
     * @return 题目提交 id
     */
    @PostMapping("/")
    @ApiOperation(value = "提交题目")
    public BaseResponse<Long> doQuestionSubmit(@RequestBody QuestionSubmitAddRequest questionSubmitAddRequest, HttpServletRequest request) {
        // 1. 校验请求参数
        ThrowUtil.throwIf(questionSubmitAddRequest == null || questionSubmitAddRequest.getQuestionId() <= 0, ErrorCode.PARAMS_ERROR);
        ThrowUtil.throwIf(request == null, ErrorCode.PARAMS_ERROR);

        // 2. 处理提交题目逻辑
        Long questionSubmitId = questionSubmitService.doQuestionSubmit(questionSubmitAddRequest, request);

        return ResultUtil.success(questionSubmitId);
    }

    /**
     * 分页获取题目提交列表（已脱敏）
     *
     * @param questionSubmitQueryRequest 查询请求
     * @param request                    请求
     * @return 题目提交列表
     */
    @PostMapping("/list/page/vo")
    @ApiOperation(value = "分页获取题目提交列表（已脱敏）")
    public BaseResponse<Page<QuestionSubmitVO>> listQuestionSubmitVOByPage(@RequestBody QuestionSubmitQueryRequest questionSubmitQueryRequest, HttpServletRequest request) {
        // 1. 校验请求参数
        ThrowUtil.throwIf(questionSubmitQueryRequest == null, ErrorCode.PARAMS_ERROR);
        ThrowUtil.throwIf(request == null, ErrorCode.PARAMS_ERROR);

        // 2. 处理分页获取用户列表逻辑
        Page<QuestionSubmitVO> questionSubmitVOPage = questionSubmitService.listQuestionSubmitVOByPage(questionSubmitQueryRequest, request);

        return ResultUtil.success(questionSubmitVOPage);
    }

}
