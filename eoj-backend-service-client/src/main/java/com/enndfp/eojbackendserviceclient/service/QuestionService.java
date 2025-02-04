package com.enndfp.eojbackendserviceclient.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.enndfp.eojbackendcommon.common.DeleteRequest;
import com.enndfp.eojbackendmodel.model.dto.question.QuestionAddRequest;
import com.enndfp.eojbackendmodel.model.dto.question.QuestionEditRequest;
import com.enndfp.eojbackendmodel.model.dto.question.QuestionQueryRequest;
import com.enndfp.eojbackendmodel.model.dto.question.QuestionUpdateRequest;
import com.enndfp.eojbackendmodel.model.entity.Question;
import com.enndfp.eojbackendmodel.model.vo.QuestionVO;

import javax.servlet.http.HttpServletRequest;

/**
 * 题目服务
 *
 * @author Enndfp
 */
public interface QuestionService extends IService<Question> {

    /**
     * 创建题目
     *
     * @param questionAddRequest 题目创建请求
     * @param request            请求
     * @return 题目 id
     */
    Long addQuestion(QuestionAddRequest questionAddRequest, HttpServletRequest request);

    /**
     * 删除题目
     *
     * @param deleteRequest 删除请求
     * @param request       请求
     * @return 是否删除成功
     */
    Boolean deleteQuestion(DeleteRequest deleteRequest, HttpServletRequest request);

    /**
     * 更新题目
     *
     * @param questionUpdateRequest 题目更新请求
     * @return 是否更新成功
     */
    Boolean updateQuestion(QuestionUpdateRequest questionUpdateRequest);

    /**
     * 编辑题目
     *
     * @param questionEditRequest 编辑题目请求
     * @param request             请求
     * @return 是否编辑成功
     */
    Boolean editQuestion(QuestionEditRequest questionEditRequest, HttpServletRequest request);

    /**
     * 获取脱敏的题目信息
     *
     * @param question 题目
     * @return 脱敏的题目信息
     */
    QuestionVO getQuestionVO(Question question);

    /**
     * 分页获取题目列表（仅管理员）
     *
     * @param questionQueryRequest 题目查询请求
     * @return 题目列表
     */
    Page<Question> listQuestionByPage(QuestionQueryRequest questionQueryRequest);

    /**
     * 分页获取题目列表（已脱敏）
     *
     * @param questionQueryRequest 题目查询请求
     * @return 题目列表
     */
    Page<QuestionVO> listQuestionVOByPage(QuestionQueryRequest questionQueryRequest);

    /**
     * 分页获取当前用户的题目列表（已脱敏）
     *
     * @param questionQueryRequest 题目查询请求
     * @param request              请求
     * @return 题目列表
     */
    Page<QuestionVO> listMyQuestionVOByPage(QuestionQueryRequest questionQueryRequest, HttpServletRequest request);

    /**
     * 校验题目信息
     *
     * @param question 题目
     * @param add      是否为新增
     */
    void validQuestion(Question question, boolean add);

    /**
     * 获取查询条件
     *
     * @param questionQueryRequest 题目查询请求
     * @return 查询条件
     */
    QueryWrapper<Question> getQueryWrapper(QuestionQueryRequest questionQueryRequest);

}
