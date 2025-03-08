package com.enndfp.eojbackendquestionservice.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enndfp.eojbackendcommon.annotation.AuthCheck;
import com.enndfp.eojbackendcommon.common.BaseResponse;
import com.enndfp.eojbackendcommon.common.DeleteRequest;
import com.enndfp.eojbackendcommon.common.ErrorCode;
import com.enndfp.eojbackendcommon.common.ResultUtil;
import com.enndfp.eojbackendcommon.constant.UserConstant;
import com.enndfp.eojbackendcommon.exception.BusinessException;
import com.enndfp.eojbackendcommon.exception.ThrowUtil;
import com.enndfp.eojbackendmodel.model.dto.question.QuestionAddRequest;
import com.enndfp.eojbackendmodel.model.dto.question.QuestionEditRequest;
import com.enndfp.eojbackendmodel.model.dto.question.QuestionQueryRequest;
import com.enndfp.eojbackendmodel.model.dto.question.QuestionUpdateRequest;
import com.enndfp.eojbackendmodel.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.enndfp.eojbackendmodel.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.enndfp.eojbackendmodel.model.entity.Question;
import com.enndfp.eojbackendmodel.model.entity.User;
import com.enndfp.eojbackendmodel.model.vo.QuestionSubmitVO;
import com.enndfp.eojbackendmodel.model.vo.QuestionVO;
import com.enndfp.eojbackendquestionservice.manager.RedisLimiterManager;
import com.enndfp.eojbackendquestionservice.service.QuestionService;
import com.enndfp.eojbackendquestionservice.service.QuestionSubmitService;
import com.enndfp.eojbackendserviceclient.service.UserFeignClient;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;

/**
 * 题目接口
 *
 * @author Enndfp
 */
@Slf4j
@RestController
@RequestMapping("/")
public class QuestionController {

    @Resource
    private QuestionService questionService;

    @Resource
    private QuestionSubmitService questionSubmitService;

    @Resource
    private UserFeignClient userFeignClient;

    @Resource
    private RedisLimiterManager redisLimiterManager;

    /**
     * 创建题目
     *
     * @param questionAddRequest 题目创建请求
     * @param request            请求
     * @return 题目 id
     */
    @PostMapping("/add")
    @ApiOperation(value = "创建题目")
    public BaseResponse<Long> addQuestion(@RequestBody QuestionAddRequest questionAddRequest, HttpServletRequest request) {
        // 1. 校验请求参数
        ThrowUtil.throwIf(questionAddRequest == null, ErrorCode.PARAMS_ERROR);
        ThrowUtil.throwIf(request == null, ErrorCode.PARAMS_ERROR);

        // 2. 处理创建题目逻辑
        Long questionId = questionService.addQuestion(questionAddRequest, request);

        return ResultUtil.success(questionId);
    }

    /**
     * 删除题目
     *
     * @param deleteRequest 删除请求
     * @param request       请求
     * @return 是否成功
     */
    @PostMapping("/delete")
    @ApiOperation(value = "删除题目")
    public BaseResponse<Boolean> deleteQuestion(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        // 1. 校验请求参数
        ThrowUtil.throwIf(deleteRequest == null || deleteRequest.getId() <= 0, ErrorCode.PARAMS_ERROR);
        ThrowUtil.throwIf(request == null, ErrorCode.PARAMS_ERROR);

        // 2. 处理删除题目逻辑
        boolean result = questionService.deleteQuestion(deleteRequest, request);

        return ResultUtil.success(result);
    }

    /**
     * 更新题目（仅管理员）
     *
     * @param questionUpdateRequest 更新请求
     * @return 是否成功
     */
    @PostMapping("/update")
    @ApiOperation(value = "更新题目（仅管理员）")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateQuestion(@RequestBody QuestionUpdateRequest questionUpdateRequest) {
        // 1. 校验请求参数
        ThrowUtil.throwIf(questionUpdateRequest == null || questionUpdateRequest.getId() <= 0, ErrorCode.PARAMS_ERROR);

        // 2. 处理更新题目逻辑
        boolean result = questionService.updateQuestion(questionUpdateRequest);

        return ResultUtil.success(result);
    }

    /**
     * 编辑题目（用户）
     *
     * @param questionEditRequest 编辑请求
     * @param request             请求
     * @return 是否成功
     */
    @PostMapping("/edit")
    @ApiOperation(value = "编辑题目（用户）")
    public BaseResponse<Boolean> editQuestion(@RequestBody QuestionEditRequest questionEditRequest, HttpServletRequest request) {
        // 1. 校验请求参数
        ThrowUtil.throwIf(questionEditRequest == null || questionEditRequest.getId() <= 0, ErrorCode.PARAMS_ERROR);
        ThrowUtil.throwIf(request == null, ErrorCode.PARAMS_ERROR);

        // 2. 处理编辑题目逻辑
        boolean result = questionService.editQuestion(questionEditRequest, request);

        return ResultUtil.success(result);
    }

    /**
     * 根据 id 获取题目
     *
     * @param id      题目 id
     * @param request 请求
     * @return 题目信息
     */
    @GetMapping("/get")
    @ApiOperation(value = "根据 id 获取题目")
    public BaseResponse<Question> getQuestionById(long id, HttpServletRequest request) {
        // 1. 校验请求参数
        ThrowUtil.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);

        // 2. 处理获取题目逻辑
        Question question = questionService.getById(id);
        ThrowUtil.throwIf(question == null, ErrorCode.NOT_FOUND_ERROR);

        // 3. 校验是否有权限查看题目
        User loginUser = userFeignClient.getLoginUser(request);
        if (!Objects.equals(loginUser.getId(), question.getUserId()) && !userFeignClient.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }

        return ResultUtil.success(question);
    }

    /**
     * 根据 id 获取题目（已脱敏）
     *
     * @param id 题目 id
     * @return 题目信息
     */
    @GetMapping("/get/vo")
    @ApiOperation(value = "根据 id 获取题目（已脱敏）")
    public BaseResponse<QuestionVO> getQuestionVOById(long id) {
        // 1. 校验请求参数
        ThrowUtil.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);

        // 2. 处理获取题目逻辑
        Question question = questionService.getById(id);
        ThrowUtil.throwIf(question == null, ErrorCode.NOT_FOUND_ERROR);

        // 3. 处理题目脱敏逻辑
        QuestionVO questionVO = questionService.getQuestionVO(question);

        return ResultUtil.success(questionVO);
    }

    /**
     * 分页获取题目列表（仅管理员）
     *
     * @param questionQueryRequest 查询请求
     * @return 题目列表
     */
    @PostMapping("/list/page")
    @ApiOperation(value = "分页获取题目列表（仅管理员）")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<Question>> listQuestionByPage(@RequestBody QuestionQueryRequest questionQueryRequest) {
        // 1. 校验请求参数
        ThrowUtil.throwIf(questionQueryRequest == null, ErrorCode.PARAMS_ERROR);

        // 2. 处理分页获取题目列表逻辑
        Page<Question> questionPage = questionService.listQuestionByPage(questionQueryRequest);

        return ResultUtil.success(questionPage);
    }

    /**
     * 分页获取题目列表（已脱敏）
     *
     * @param questionQueryRequest 查询请求
     * @return 题目列表
     */
    @PostMapping("/list/page/vo")
    @ApiOperation(value = "分页获取题目列表（已脱敏）")
    public BaseResponse<Page<QuestionVO>> listQuestionVOByPage(@RequestBody QuestionQueryRequest questionQueryRequest) {
        // 1. 校验请求参数
        ThrowUtil.throwIf(questionQueryRequest == null, ErrorCode.PARAMS_ERROR);

        // 2. 处理分页获取用户列表逻辑
        Page<QuestionVO> questionVOPage = questionService.listQuestionVOByPage(questionQueryRequest);

        return ResultUtil.success(questionVOPage);
    }

    /**
     * 分页获取当前用户的题目列表（已脱敏）
     *
     * @param questionQueryRequest 查询请求
     * @param request              请求
     * @return 题目列表
     */
    @PostMapping("/my/list/page/vo")
    @ApiOperation(value = "分页获取当前用户的题目列表（已脱敏）")
    public BaseResponse<Page<QuestionVO>> listMyQuestionVOByPage(@RequestBody QuestionQueryRequest questionQueryRequest, HttpServletRequest request) {
        // 1. 校验请求参数
        ThrowUtil.throwIf(questionQueryRequest == null, ErrorCode.PARAMS_ERROR);
        ThrowUtil.throwIf(request == null, ErrorCode.PARAMS_ERROR);

        // 2. 处理分页获取用户列表逻辑
        Page<QuestionVO> questionVOPage = questionService.listMyQuestionVOByPage(questionQueryRequest, request);

        return ResultUtil.success(questionVOPage);
    }

    /**
     * 提交题目
     *
     * @param questionSubmitAddRequest 提交题目请求
     * @param request                  请求
     * @return 题目提交 id
     */
    @PostMapping("/question_submit/do")
    @ApiOperation(value = "提交题目")
    public BaseResponse<Long> doQuestionSubmit(@RequestBody QuestionSubmitAddRequest questionSubmitAddRequest, HttpServletRequest request) {
        // 1. 校验请求参数
        ThrowUtil.throwIf(questionSubmitAddRequest == null || questionSubmitAddRequest.getQuestionId() <= 0, ErrorCode.PARAMS_ERROR);
        ThrowUtil.throwIf(request == null, ErrorCode.PARAMS_ERROR);

        // 2. 登录用户校验
        User loginUser = userFeignClient.getLoginUser(request);
        ThrowUtil.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR, "用户未登录");

        // 3. 限流校验
        boolean rateLimit = redisLimiterManager.doRateLimit(loginUser.getId().toString());
        ThrowUtil.throwIf(!rateLimit, ErrorCode.TOO_MANY_REQUEST, "提交过于频繁，请稍后再试");

        // 4. 处理提交题目逻辑
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
    @PostMapping("/question_submit/list/page")
    @ApiOperation(value = "分页获取题目提交列表（已脱敏）")
    public BaseResponse<Page<QuestionSubmitVO>> listQuestionSubmitVOByPage(@RequestBody QuestionSubmitQueryRequest questionSubmitQueryRequest, HttpServletRequest request) {
        // 1. 校验请求参数
        ThrowUtil.throwIf(questionSubmitQueryRequest == null, ErrorCode.PARAMS_ERROR);
        ThrowUtil.throwIf(request == null, ErrorCode.PARAMS_ERROR);

        // 2. 处理分页获取用户列表逻辑
        Page<QuestionSubmitVO> questionSubmitVOPage = questionSubmitService.listQuestionSubmitVOByPage(questionSubmitQueryRequest, request);

        return ResultUtil.success(questionSubmitVOPage);
    }

    /**
     * 根据题目 ID 列表获取题目提交记录（已脱敏）
     *
     * @param questionSubmitQueryRequest 查询请求
     * @param request                    请求
     * @return 题目提交列表
     */
    @PostMapping("/question_submit/list/questionIds")
    @ApiOperation(value = "根据题目ID列表获取题目提交记录（已脱敏）")
    public BaseResponse<List<QuestionSubmitVO>> listQuestionSubmitVOByQuestionIds(@RequestBody QuestionSubmitQueryRequest questionSubmitQueryRequest, HttpServletRequest request) {
        // 1. 校验请求参数
        ThrowUtil.throwIf(questionSubmitQueryRequest == null, ErrorCode.PARAMS_ERROR);
        ThrowUtil.throwIf(request == null, ErrorCode.PARAMS_ERROR);

        // 2. 处理批量查询逻辑
        List<QuestionSubmitVO> questionSubmitVOList = questionSubmitService.listQuestionSubmitVOByQuestionIds(questionSubmitQueryRequest, request);

        return ResultUtil.success(questionSubmitVOList);
    }
}
