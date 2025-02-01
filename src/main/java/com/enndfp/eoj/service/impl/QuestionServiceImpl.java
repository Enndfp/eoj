package com.enndfp.eoj.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.enndfp.eoj.common.DeleteRequest;
import com.enndfp.eoj.common.ErrorCode;
import com.enndfp.eoj.constant.CommonConstant;
import com.enndfp.eoj.exception.BusinessException;
import com.enndfp.eoj.exception.ThrowUtil;
import com.enndfp.eoj.mapper.QuestionMapper;
import com.enndfp.eoj.model.dto.question.*;
import com.enndfp.eoj.model.entity.Question;
import com.enndfp.eoj.model.entity.User;
import com.enndfp.eoj.model.vo.QuestionVO;
import com.enndfp.eoj.model.vo.UserVO;
import com.enndfp.eoj.service.QuestionService;
import com.enndfp.eoj.service.UserService;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 题目服务实现
 *
 * @author Enndfp
 */
@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question> implements QuestionService {

    @Resource
    private UserService userService;

    @Override
    public Long addQuestion(QuestionAddRequest questionAddRequest, HttpServletRequest request) {
        // 1. 封装题目信息
        Question question = BeanUtil.toBean(questionAddRequest, Question.class);
        // 将 tags、judgeCase、judgeConfig 转换为 JSON 字符串
        List<String> tags = questionAddRequest.getTags();
        if (tags != null) {
            question.setTags(JSONUtil.toJsonStr(tags));
        }
        List<JudgeCase> judgeCases = questionAddRequest.getJudgeCase();
        if (judgeCases != null) {
            question.setJudgeCase(JSONUtil.toJsonStr(judgeCases));
        }
        JudgeConfig judgeConfig = questionAddRequest.getJudgeConfig();
        if (judgeConfig != null) {
            question.setJudgeConfig(JSONUtil.toJsonStr(judgeConfig));
        }

        // 2. 校验题目信息
        validQuestion(question, true);

        // 3. 封装创建者信息
        User loginUser = userService.getLoginUser(request);
        question.setUserId(loginUser.getId());

        // 4. 保存题目
        boolean result = this.save(question);
        ThrowUtil.throwIf(!result, ErrorCode.OPERATION_ERROR);

        return question.getId();
    }

    @Override
    public Boolean deleteQuestion(DeleteRequest deleteRequest, HttpServletRequest request) {
        // 1. 获取当前登录用户
        User loginUser = userService.getLoginUser(request);

        // 2. 校验题目是否存在
        long id = deleteRequest.getId();
        Question oldQuestion = this.getById(id);
        ThrowUtil.throwIf(oldQuestion == null, ErrorCode.NOT_FOUND_ERROR);

        // 3. 校验用户是否有权限删除题目
        if (!oldQuestion.getUserId().equals(loginUser.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }

        return this.removeById(id);
    }

    @Override
    public Boolean updateQuestion(QuestionUpdateRequest questionUpdateRequest) {
        // 1. 封装题目信息
        Question question = BeanUtil.toBean(questionUpdateRequest, Question.class);
        // 将 tags、judgeCase、judgeConfig 转换为 JSON 字符串
        List<String> tags = questionUpdateRequest.getTags();
        if (tags != null) {
            question.setTags(JSONUtil.toJsonStr(tags));
        }
        List<JudgeCase> judgeCases = questionUpdateRequest.getJudgeCase();
        if (judgeCases != null) {
            question.setJudgeCase(JSONUtil.toJsonStr(judgeCases));
        }
        JudgeConfig judgeConfig = questionUpdateRequest.getJudgeConfig();
        if (judgeConfig != null) {
            question.setJudgeConfig(JSONUtil.toJsonStr(judgeConfig));
        }

        // 2. 校验题目信息
        validQuestion(question, false);

        // 3. 校验题目是否存在
        long id = questionUpdateRequest.getId();
        Question oldQuestion = this.getById(id);
        ThrowUtil.throwIf(oldQuestion == null, ErrorCode.NOT_FOUND_ERROR);

        return this.updateById(question);
    }

    @Override
    public Boolean editQuestion(QuestionEditRequest questionEditRequest, HttpServletRequest request) {
        // 1. 封装题目信息
        Question question = BeanUtil.toBean(questionEditRequest, Question.class);
        // 将 tags、judgeCase、judgeConfig 转换为 JSON 字符串
        List<String> tags = questionEditRequest.getTags();
        if (tags != null) {
            question.setTags(JSONUtil.toJsonStr(tags));
        }
        List<JudgeCase> judgeCases = questionEditRequest.getJudgeCase();
        if (judgeCases != null) {
            question.setJudgeCase(JSONUtil.toJsonStr(judgeCases));
        }
        JudgeConfig judgeConfig = questionEditRequest.getJudgeConfig();
        if (judgeConfig != null) {
            question.setJudgeConfig(JSONUtil.toJsonStr(judgeConfig));
        }

        // 2. 校验题目信息
        validQuestion(question, false);

        // 3. 校验题目是否存在
        long id = questionEditRequest.getId();
        Question oldQuestion = this.getById(id);
        ThrowUtil.throwIf(oldQuestion == null, ErrorCode.NOT_FOUND_ERROR);

        // 4. 校验用户是否有权限删除题目
        User loginUser = userService.getLoginUser(request);
        if (!oldQuestion.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }

        return this.updateById(question);
    }

    @Override
    public QuestionVO getQuestionVO(Question question) {
        // 1. 将题目对象转换为包装类
        QuestionVO questionVO = QuestionVO.objToVo(question);

        // 2. 关联查询用户信息
        Long userId = question.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }

        // 3. 将用户对象转换为包装类
        UserVO userVO = userService.getUserVO(user);
        questionVO.setUserVO(userVO);

        return questionVO;
    }

    @Override
    public Page<Question> listQuestionByPage(QuestionQueryRequest questionQueryRequest) {
        // 1. 获取分页信息
        long current = questionQueryRequest.getCurrent();
        long size = questionQueryRequest.getPageSize();

        // 2. 处理分页获取用户列表逻辑
        return this.page(new Page<>(current, size), getQueryWrapper(questionQueryRequest));
    }

    @Override
    public Page<QuestionVO> listQuestionVOByPage(QuestionQueryRequest questionQueryRequest) {
        // 1. 获取分页信息
        long current = questionQueryRequest.getCurrent();
        long size = questionQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtil.throwIf(size > 20, ErrorCode.PARAMS_ERROR);

        // 2. 处理分页获取题目列表逻辑
        Page<Question> questionPage = this.page(new Page<>(current, size), getQueryWrapper(questionQueryRequest));
        Page<QuestionVO> questionVOPage = new Page<>(current, size, questionPage.getTotal());

        // 3. 获取当前页的题目记录
        List<Question> questionList = questionPage.getRecords();

        // 4. 获取所有题目的用户id并查询用户信息
        Set<Long> userIdSet = questionList.stream()
                .map(Question::getUserId)
                .filter(Objects::nonNull) // 确保userId不为null
                .collect(Collectors.toSet());

        // 如果userIdSet为空，直接返回空的userIdUserListMap
        Map<Long, List<User>> userIdUserListMap = Collections.emptyMap();
        if (!userIdSet.isEmpty()) {
            userIdUserListMap = userService.listByIds(userIdSet).stream()
                    .collect(Collectors.groupingBy(User::getId));
        }

        // 5. 填充题目和用户信息，转换为QuestionVO
        Map<Long, List<User>> finalUserIdUserListMap = userIdUserListMap;
        List<QuestionVO> questionVOList = questionList.stream().map(question -> {
            QuestionVO questionVO = QuestionVO.objToVo(question);
            Long userId = question.getUserId();
            User user = null;
            if (userId != null && finalUserIdUserListMap.containsKey(userId)) {
                user = finalUserIdUserListMap.get(userId).get(0);
            }
            questionVO.setUserVO(userService.getUserVO(user));
            return questionVO;
        }).collect(Collectors.toList());
        questionVOPage.setRecords(questionVOList);

        return questionVOPage;
    }

    @Override
    public Page<QuestionVO> listMyQuestionVOByPage(QuestionQueryRequest questionQueryRequest, HttpServletRequest request) {
        // 1. 获取分页信息
        long current = questionQueryRequest.getCurrent();
        long size = questionQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtil.throwIf(size > 20, ErrorCode.PARAMS_ERROR);

        // 2. 设置登录用户id
        User loginUser = userService.getLoginUser(request);
        questionQueryRequest.setUserId(loginUser.getId());

        // 3. 处理分页获取题目列表逻辑
        Page<Question> questionPage = this.page(new Page<>(current, size), getQueryWrapper(questionQueryRequest));
        Page<QuestionVO> questionVOPage = new Page<>(current, size, questionPage.getTotal());
        List<QuestionVO> questionVO = questionPage.getRecords().stream().map(this::getQuestionVO).collect(Collectors.toList());
        questionVOPage.setRecords(questionVO);

        return questionVOPage;
    }

    @Override
    public void validQuestion(Question question, boolean add) {
        // 1. 校验参数是否为空
        ThrowUtil.throwIf(question == null, ErrorCode.PARAMS_ERROR, "题目信息不能为空");

        String title = question.getTitle();
        String content = question.getContent();
        String tags = question.getTags();
        String answer = question.getAnswer();
        String judgeCase = question.getJudgeCase();
        String judgeConfig = question.getJudgeConfig();

        // 2. 校验题目标题（添加和修改时都需要校验）
        if (add || StringUtils.isNotBlank(title)) {
            ThrowUtil.throwIf(StringUtils.isBlank(title), ErrorCode.PARAMS_ERROR, "题目标题不能为空");
            ThrowUtil.throwIf(title.length() < 1 || title.length() > 100, ErrorCode.PARAMS_ERROR, "题目标题长度应在 1 到 100 字符之间");
        }

        // 3. 校验题目内容（添加和修改时都需要校验）
        if (add || StringUtils.isNotBlank(content)) {
            ThrowUtil.throwIf(StringUtils.isBlank(content), ErrorCode.PARAMS_ERROR, "题目内容不能为空");
            ThrowUtil.throwIf(content.length() < 1 || content.length() > 2000, ErrorCode.PARAMS_ERROR, "题目内容长度应在 1 到 2000 字符之间");
        }

        // 4. 校验题目标签（只有在添加题目时需要校验）
        if (add) {
            ThrowUtil.throwIf(StringUtils.isBlank(tags), ErrorCode.PARAMS_ERROR, "题目标签不能为空");
            List<String> tagList = JSONUtil.toList(tags, String.class);
            ThrowUtil.throwIf(tagList == null || tagList.isEmpty(), ErrorCode.PARAMS_ERROR, "题目标签不能为空");
            for (String tag : tagList) {
                ThrowUtil.throwIf(tag.length() < 1 || tag.length() > 50, ErrorCode.PARAMS_ERROR, "标签长度应在 1 到 50 字符之间");
            }
        }

        // 5. 校验题目答案（添加和修改时都需要校验）
        if (add || StringUtils.isNotBlank(answer)) {
            ThrowUtil.throwIf(StringUtils.isBlank(answer), ErrorCode.PARAMS_ERROR, "题目答案不能为空");
        }

        // 6. 校验判题用例（添加和修改时都需要校验）
        if (add || StringUtils.isNotBlank(judgeCase)) {
            ThrowUtil.throwIf(StringUtils.isBlank(judgeCase), ErrorCode.PARAMS_ERROR, "判题用例不能为空");
            List<JudgeCase> judgeCases = JSONUtil.toList(judgeCase, JudgeCase.class);
            ThrowUtil.throwIf(judgeCases == null || judgeCases.isEmpty(), ErrorCode.PARAMS_ERROR, "判题用例不能为空");
            for (JudgeCase judgeCaseObj : judgeCases) {
                ThrowUtil.throwIf(StringUtils.isBlank(judgeCaseObj.getInput()), ErrorCode.PARAMS_ERROR, "判题用例的输入不能为空");
                ThrowUtil.throwIf(StringUtils.isBlank(judgeCaseObj.getOutput()), ErrorCode.PARAMS_ERROR, "判题用例的输出不能为空");
            }
        }

        // 7. 校验判题配置（添加和修改时都需要校验）
        if (add || StringUtils.isNotBlank(judgeConfig)) {
            ThrowUtil.throwIf(StringUtils.isBlank(judgeConfig), ErrorCode.PARAMS_ERROR, "判题配置不能为空");
            JudgeConfig judgeConfigObj = JSONUtil.toBean(judgeConfig, JudgeConfig.class);
            ThrowUtil.throwIf(judgeConfigObj == null, ErrorCode.PARAMS_ERROR, "判题配置格式错误");

            // 校验时间限制、内存限制和堆栈限制是否大于 0
            Long timeLimit = judgeConfigObj.getTimeLimit();
            Long memoryLimit = judgeConfigObj.getMemoryLimit();
            Long stackLimit = judgeConfigObj.getStackLimit();

            ThrowUtil.throwIf(timeLimit == null || timeLimit <= 0, ErrorCode.PARAMS_ERROR, "时间限制必须大于 0 ms");
            ThrowUtil.throwIf(memoryLimit == null || memoryLimit <= 0, ErrorCode.PARAMS_ERROR, "内存限制必须大于 0 KB");
            ThrowUtil.throwIf(stackLimit == null || stackLimit <= 0, ErrorCode.PARAMS_ERROR, "堆栈限制必须大于 0 KB");
        }
    }

    @Override
    public QueryWrapper<Question> getQueryWrapper(QuestionQueryRequest questionQueryRequest) {
        QueryWrapper<Question> queryWrapper = new QueryWrapper<>();

        // 如果请求为空，直接返回空的 QueryWrapper
        if (questionQueryRequest == null) {
            return queryWrapper;
        }

        // 提取查询参数
        Long id = questionQueryRequest.getId();
        String title = questionQueryRequest.getTitle();
        String content = questionQueryRequest.getContent();
        List<String> tags = questionQueryRequest.getTags();
        String answer = questionQueryRequest.getAnswer();
        Long userId = questionQueryRequest.getUserId();
        String creator = questionQueryRequest.getCreator();
        String sortField = questionQueryRequest.getSortField();
        String sortOrder = questionQueryRequest.getSortOrder();

        // 拼接查询条件
        queryWrapper.like(StringUtils.isNotBlank(title), "title", title);
        queryWrapper.like(StringUtils.isNotBlank(content), "content", content);

        // 处理标签字段
        if (CollectionUtils.isNotEmpty(tags)) {
            for (String tag : tags) {
                queryWrapper.like("tags", "\"" + tag + "\"");
            }
        }

        queryWrapper.like(StringUtils.isNotBlank(answer), "answer", answer);
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        // 如果 creator 存在，使用子查询来通过 creator 查找 userId
        if (StringUtils.isNotBlank(creator)) {
            queryWrapper.inSql("userId", "SELECT id FROM user WHERE userName LIKE '%" + creator + "%'");
        }

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




