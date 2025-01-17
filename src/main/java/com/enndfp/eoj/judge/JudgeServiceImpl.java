package com.enndfp.eoj.judge;

import cn.hutool.json.JSONUtil;
import com.enndfp.eoj.common.ErrorCode;
import com.enndfp.eoj.exception.ThrowUtil;
import com.enndfp.eoj.judge.codesandbox.CodeSandbox;
import com.enndfp.eoj.judge.codesandbox.CodeSandboxFactory;
import com.enndfp.eoj.judge.codesandbox.CodeSandboxProxy;
import com.enndfp.eoj.judge.codesandbox.model.ExecuteCodeRequest;
import com.enndfp.eoj.judge.codesandbox.model.ExecuteCodeResponse;
import com.enndfp.eoj.judge.strategy.JudgeContext;
import com.enndfp.eoj.model.dto.question.JudgeCase;
import com.enndfp.eoj.model.dto.questionsubmit.JudgeInfo;
import com.enndfp.eoj.model.entity.Question;
import com.enndfp.eoj.model.entity.QuestionSubmit;
import com.enndfp.eoj.model.enums.QuestionSubmitStatusEnum;
import com.enndfp.eoj.service.QuestionService;
import com.enndfp.eoj.service.QuestionSubmitService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 判题服务实现
 *
 * @author Enndfp
 */
@Service
public class JudgeServiceImpl implements JudgeService {

    @Value("${codesandbox.type}")
    private String type;

    @Resource
    private JudgeManager judgeManager;

    @Resource
    private QuestionService questionService;

    @Resource
    private QuestionSubmitService questionSubmitService;

    @Override
    public QuestionSubmit doJudge(long questionSubmitId) {
        // 1. 判断提交记录是否存在
        QuestionSubmit questionSubmit = questionSubmitService.getById(questionSubmitId);
        ThrowUtil.throwIf(questionSubmit == null, ErrorCode.NOT_FOUND_ERROR, "提交记录不存在");

        // 2. 判断题目是否存在
        long questionId = questionSubmit.getQuestionId();
        Question question = questionService.getById(questionId);
        ThrowUtil.throwIf(question == null, ErrorCode.NOT_FOUND_ERROR, "题目不存在");

        // 3. 判断提交记录状态是否为等待
        if (!Objects.equals(questionSubmit.getStatus(), QuestionSubmitStatusEnum.WAITING.getValue())) {
            ThrowUtil.throwIf(true, ErrorCode.OPERATION_ERROR, "题目正在判题中");
        }
        QuestionSubmit questionSubmitUpdate = new QuestionSubmit();
        questionSubmitUpdate.setId(questionSubmitId);
        questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.RUNNING.getValue());
        boolean result = questionSubmitService.updateById(questionSubmitUpdate);
        ThrowUtil.throwIf(!result, ErrorCode.SYSTEM_ERROR, "提交题目状态更新失败");

        // 5. 调用代码沙箱执行代码
        CodeSandbox codeSandbox = CodeSandboxFactory.newInstance(type);
        codeSandbox = new CodeSandboxProxy(codeSandbox);
        String code = questionSubmit.getCode();
        String language = questionSubmit.getLanguage();
        String judgeCaseStr = question.getJudgeCase();
        List<JudgeCase> judgeCaseList = JSONUtil.toList(judgeCaseStr, JudgeCase.class);
        List<String> inputList = judgeCaseList.stream().map(JudgeCase::getInput).collect(Collectors.toList());

        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder()
                .code(code)
                .language(language)
                .inputList(inputList)
                .build();
        ExecuteCodeResponse executeCodeResponse = codeSandbox.executeCode(executeCodeRequest);

        // 6. 判断沙箱执行结果
        JudgeContext judgeContext = JudgeContext.builder()
                .judgeInfo(executeCodeResponse.getJudgeInfo())
                .inputList(inputList)
                .outputList(executeCodeResponse.getOutputList())
                .judgeCaseList(judgeCaseList)
                .question(question)
                .questionSubmit(questionSubmit)
                .build();
        JudgeInfo judgeInfo = judgeManager.doJudge(judgeContext);

        // 7. 更新提交记录状态
        questionSubmitUpdate = new QuestionSubmit();
        questionSubmitUpdate.setId(questionSubmitId);
        questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.SUCCESS.getValue());
        questionSubmitUpdate.setJudgeInfo(JSONUtil.toJsonStr(judgeInfo));
        result = questionSubmitService.updateById(questionSubmitUpdate);
        ThrowUtil.throwIf(!result, ErrorCode.SYSTEM_ERROR, "提交题目状态更新失败");
        QuestionSubmit questionSubmitResult = questionSubmitService.getById(questionSubmitId);

        return questionSubmitResult;
    }
}
