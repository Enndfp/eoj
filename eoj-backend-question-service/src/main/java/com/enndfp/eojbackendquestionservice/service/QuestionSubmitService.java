package com.enndfp.eojbackendquestionservice.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.enndfp.eojbackendmodel.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.enndfp.eojbackendmodel.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.enndfp.eojbackendmodel.model.entity.QuestionSubmit;
import com.enndfp.eojbackendmodel.model.entity.User;
import com.enndfp.eojbackendmodel.model.vo.QuestionSubmitVO;

import javax.servlet.http.HttpServletRequest;

/**
 * 题目提交服务
 *
 * @author Enndfp
 */
public interface QuestionSubmitService extends IService<QuestionSubmit> {

    /**
     * 提交题目
     *
     * @param questionSubmitAddRequest 提交题目请求
     * @param request                  请求
     * @return 题目提交 id
     */
    Long doQuestionSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, HttpServletRequest request);

    /**
     * 获取脱敏的题目提交信息
     *
     * @param questionSubmit 题目提交
     * @param loginUser      登录用户
     * @return 脱敏的题目提交信息
     */
    QuestionSubmitVO getQuestionSubmitVO(QuestionSubmit questionSubmit, User loginUser);

    /**
     * 分页获取题目提交列表（已脱敏）
     *
     * @param questionSubmitQueryRequest 题目提交查询请求
     * @param request                    请求
     * @return 题目提交列表
     */
    Page<QuestionSubmitVO> listQuestionSubmitVOByPage(QuestionSubmitQueryRequest questionSubmitQueryRequest, HttpServletRequest request);

    /**
     * 获取查询条件
     *
     * @param questionSubmitQueryRequest 题目提交查询请求
     * @return 查询条件
     */
    QueryWrapper<QuestionSubmit> getQueryWrapper(QuestionSubmitQueryRequest questionSubmitQueryRequest);
}
