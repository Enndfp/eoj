package com.enndfp.eoj.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.enndfp.eoj.common.ErrorCode;
import com.enndfp.eoj.constant.CommonConstant;
import com.enndfp.eoj.exception.ThrowUtil;
import com.enndfp.eoj.mapper.QuestionSubmitMapper;
import com.enndfp.eoj.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.enndfp.eoj.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.enndfp.eoj.model.entity.Question;
import com.enndfp.eoj.model.entity.QuestionSubmit;
import com.enndfp.eoj.model.entity.User;
import com.enndfp.eoj.model.enums.QuestionSubmitLanguageEnum;
import com.enndfp.eoj.model.enums.QuestionSubmitStatusEnum;
import com.enndfp.eoj.model.vo.QuestionSubmitVO;
import com.enndfp.eoj.service.QuestionService;
import com.enndfp.eoj.service.QuestionSubmitService;
import com.enndfp.eoj.service.UserService;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 题目提交服务实现
 *
 * @author Enndfp
 */
@Service
public class QuestionSubmitServiceImpl extends ServiceImpl<QuestionSubmitMapper, QuestionSubmit> implements QuestionSubmitService {

    @Resource
    private QuestionService questionService;

    @Resource
    private UserService userService;

    @Override
    public Long doQuestionSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, HttpServletRequest request) {
        // 1. 用户是否登录
        User loginUser = userService.getLoginUser(request);
        ThrowUtil.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);

        // 2. 题目是否存在
        long questionId = questionSubmitAddRequest.getQuestionId();
        Question question = questionService.getById(questionId);
        ThrowUtil.throwIf(question == null, ErrorCode.NOT_FOUND_ERROR);

        // 3. 编程语言是否存在
        String language = questionSubmitAddRequest.getLanguage();
        QuestionSubmitLanguageEnum languageEnum = QuestionSubmitLanguageEnum.getEnumByValue(language);
        ThrowUtil.throwIf(languageEnum == null, ErrorCode.PARAMS_ERROR, "编程语言不存在");

        // 4. 设置题目提交信息
        QuestionSubmit questionSubmit = new QuestionSubmit();
        questionSubmit.setJudgeInfo("{}");
        questionSubmit.setLanguage(language);
        questionSubmit.setQuestionId(questionId);
        questionSubmit.setUserId(loginUser.getId());
        questionSubmit.setCode(questionSubmitAddRequest.getCode());
        questionSubmit.setStatus(QuestionSubmitStatusEnum.WAITING.getValue());

        // 5. 保存题目提交
        boolean save = this.save(questionSubmit);
        ThrowUtil.throwIf(!save, ErrorCode.SYSTEM_ERROR, "题目提交失败");

        return questionSubmit.getId();
    }

    @Override
    public QuestionSubmitVO getQuestionSubmitVO(QuestionSubmit questionSubmit, User loginUser) {
        // 1. 将题目提交对象转换为包装类
        QuestionSubmitVO questionSubmitVO = QuestionSubmitVO.objToVo(questionSubmit);

        // 2. 关联查询用户信息
        Long userId = questionSubmit.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        // 将用户对象转换为包装类
        questionSubmitVO.setUserVO(userService.getUserVO(user));

        // 3. 关联查询题目信息
        Long questionId = questionSubmit.getQuestionId();
        Question question = null;
        if (questionId != null && questionId > 0) {
            question = questionService.getById(questionId);
        }
        // 将题目对象转换为包装类
        questionSubmitVO.setQuestionVO(questionService.getQuestionVO(question));

        // 4. 校验是否需要脱敏代码：仅本人或管理员能看到代码
        Long loginUserId = loginUser.getId();

        // 如果当前用户不是提交者且不是管理员，脱敏代码字段
        if (!Objects.equals(loginUserId, questionSubmit.getUserId()) && !userService.isAdmin(loginUser)) {
            questionSubmitVO.setCode(null); // 脱敏：非管理员和非提交者的用户不能查看代码
        }

        return questionSubmitVO;
    }

    @Override
    public Page<QuestionSubmitVO> listQuestionSubmitVOByPage(QuestionSubmitQueryRequest questionSubmitQueryRequest, HttpServletRequest request) {
        // 1. 获取分页信息
        long current = questionSubmitQueryRequest.getCurrent();
        long size = questionSubmitQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtil.throwIf(size > 20, ErrorCode.PARAMS_ERROR);

        // 2. 获取登录用户
        User loginUser = userService.getLoginUser(request);

        // 3. 处理分页获取题目提交列表逻辑
        Page<QuestionSubmit> questionSubmitPage = this.page(new Page<>(current, size), getQueryWrapper(questionSubmitQueryRequest));
        Page<QuestionSubmitVO> questionSubmitVOPage = new Page<>(current, size, questionSubmitPage.getTotal());
        List<QuestionSubmitVO> questionSubmitVOList = questionSubmitPage.getRecords().stream()
                .map(questionSubmit -> getQuestionSubmitVO(questionSubmit, loginUser))
                .collect(Collectors.toList());
        questionSubmitVOPage.setRecords(questionSubmitVOList);

        return questionSubmitVOPage;
    }

    @Override
    public QueryWrapper<QuestionSubmit> getQueryWrapper(QuestionSubmitQueryRequest questionSubmitQueryRequest) {
        QueryWrapper<QuestionSubmit> queryWrapper = new QueryWrapper<>();

        // 如果请求为空，直接返回空的 QueryWrapper
        if (questionSubmitQueryRequest == null) {
            return queryWrapper;
        }

        // 提取查询参数
        String language = questionSubmitQueryRequest.getLanguage();
        Integer status = questionSubmitQueryRequest.getStatus();
        Long questionId = questionSubmitQueryRequest.getQuestionId();
        Long userId = questionSubmitQueryRequest.getUserId();
        String sortField = questionSubmitQueryRequest.getSortField();
        String sortOrder = questionSubmitQueryRequest.getSortOrder();

        // 拼接查询条件
        queryWrapper.like(StringUtils.isNotBlank(language), "language", language);
        queryWrapper.like(ObjectUtils.allNotNull(QuestionSubmitStatusEnum.getEnumByValue(status)), "status", status);
        queryWrapper.eq(ObjectUtils.isNotEmpty(questionId), "questionId", questionId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);

        // 假设 isDelete 字段用于标记是否删除
        queryWrapper.eq("isDelete", false);

        // 排序功能
        if (StringUtils.isNotBlank(sortField) && StringUtils.isNotBlank(sortOrder)) {
            boolean isAsc = CommonConstant.SORT_ORDER_ASC.equals(sortOrder); // 如果排序顺序为 ASC
            queryWrapper.orderBy(true, isAsc, sortField);  // 按照指定字段排序
        }

        return queryWrapper;
    }
}




