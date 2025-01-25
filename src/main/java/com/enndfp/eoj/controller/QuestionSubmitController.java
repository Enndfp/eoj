package com.enndfp.eoj.controller;

import com.enndfp.eoj.service.QuestionSubmitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 题目提交接口
 *
 * @author Enndfp
 */
@Slf4j
@RestController
//@Api(tags = "题目提交功能接口")
//@RequestMapping("/question_submit")
@Deprecated
public class QuestionSubmitController {

    @Resource
    private QuestionSubmitService questionSubmitService;

//    /**
//     * 提交题目
//     *
//     * @param questionSubmitAddRequest 提交题目请求
//     * @param request                  请求
//     * @return 题目提交 id
//     */
//    @PostMapping("/")
//    @ApiOperation(value = "提交题目")
//    public BaseResponse<Long> doQuestionSubmit(@RequestBody QuestionSubmitAddRequest questionSubmitAddRequest, HttpServletRequest request) {
//        // 1. 校验请求参数
//        ThrowUtil.throwIf(questionSubmitAddRequest == null || questionSubmitAddRequest.getQuestionId() <= 0, ErrorCode.PARAMS_ERROR);
//        ThrowUtil.throwIf(request == null, ErrorCode.PARAMS_ERROR);
//
//        // 2. 处理提交题目逻辑
//        Long questionSubmitId = questionSubmitService.doQuestionSubmit(questionSubmitAddRequest, request);
//
//        return ResultUtil.success(questionSubmitId);
//    }
//
//    /**
//     * 分页获取题目提交列表（已脱敏）
//     *
//     * @param questionSubmitQueryRequest 查询请求
//     * @param request                    请求
//     * @return 题目提交列表
//     */
//    @PostMapping("/list/page/vo")
//    @ApiOperation(value = "分页获取题目提交列表（已脱敏）")
//    public BaseResponse<Page<QuestionSubmitVO>> listQuestionSubmitVOByPage(@RequestBody QuestionSubmitQueryRequest questionSubmitQueryRequest, HttpServletRequest request) {
//        // 1. 校验请求参数
//        ThrowUtil.throwIf(questionSubmitQueryRequest == null, ErrorCode.PARAMS_ERROR);
//        ThrowUtil.throwIf(request == null, ErrorCode.PARAMS_ERROR);
//
//        // 2. 处理分页获取用户列表逻辑
//        Page<QuestionSubmitVO> questionSubmitVOPage = questionSubmitService.listQuestionSubmitVOByPage(questionSubmitQueryRequest, request);
//
//        return ResultUtil.success(questionSubmitVOPage);
//    }

}
