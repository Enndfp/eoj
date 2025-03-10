package com.enndfp.eojbackendjudgeservice.judge;

import cn.hutool.json.JSONUtil;
import com.enndfp.eojbackendcommon.common.ErrorCode;
import com.enndfp.eojbackendcommon.exception.ThrowUtil;
import com.enndfp.eojbackendjudgeservice.judge.codesandbox.CodeSandbox;
import com.enndfp.eojbackendjudgeservice.judge.codesandbox.CodeSandboxFactory;
import com.enndfp.eojbackendjudgeservice.judge.codesandbox.CodeSandboxProxy;
import com.enndfp.eojbackendjudgeservice.judge.strategy.JudgeContext;
import com.enndfp.eojbackendmodel.model.codesandbox.ExecuteCodeRequest;
import com.enndfp.eojbackendmodel.model.codesandbox.ExecuteCodeResponse;
import com.enndfp.eojbackendmodel.model.codesandbox.JudgeInfo;
import com.enndfp.eojbackendmodel.model.dto.question.JudgeCase;
import com.enndfp.eojbackendmodel.model.dto.question.QuestionRunRequest;
import com.enndfp.eojbackendmodel.model.dto.question.QuestionRunResponse;
import com.enndfp.eojbackendmodel.model.entity.Question;
import com.enndfp.eojbackendmodel.model.entity.QuestionSubmit;
import com.enndfp.eojbackendmodel.model.enums.QuestionSubmitLanguageEnum;
import com.enndfp.eojbackendmodel.model.enums.QuestionSubmitStatusEnum;
import com.enndfp.eojbackendserviceclient.service.QuestionFeignClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
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
    private QuestionFeignClient questionFeignClient;

    @Override
    public QuestionSubmit doJudge(long questionSubmitId) {
        // 1. 判断提交记录是否存在
        QuestionSubmit questionSubmit = questionFeignClient.getQuestionSubmitById(questionSubmitId);
        ThrowUtil.throwIf(questionSubmit == null, ErrorCode.NOT_FOUND_ERROR, "提交记录不存在");

        // 2. 判断题目是否存在
        long questionId = questionSubmit.getQuestionId();
        Question question = questionFeignClient.getQuestionById(questionId);
        ThrowUtil.throwIf(question == null, ErrorCode.NOT_FOUND_ERROR, "题目不存在");

        // 3. 判断提交记录状态是否为等待
        if (!Objects.equals(questionSubmit.getStatus(), QuestionSubmitStatusEnum.WAITING.getValue())) {
            ThrowUtil.throwIf(true, ErrorCode.OPERATION_ERROR, "题目正在判题中");
        }
        QuestionSubmit questionSubmitUpdate = new QuestionSubmit();
        questionSubmitUpdate.setId(questionSubmitId);
        questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.RUNNING.getValue());
        boolean result = questionFeignClient.updateQuestionSubmit(questionSubmitUpdate);
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
        result = questionFeignClient.updateQuestionSubmit(questionSubmitUpdate);
        ThrowUtil.throwIf(!result, ErrorCode.SYSTEM_ERROR, "提交题目状态更新失败");
        QuestionSubmit questionSubmitResult = questionFeignClient.getQuestionSubmitById(questionSubmitId);

        return questionSubmitResult;
    }

    @Override
    public QuestionRunResponse doQuestionRun(QuestionRunRequest questionRunRequest) {
        // 1. 校验参数
        String code = questionRunRequest.getCode();
        String language = questionRunRequest.getLanguage();
        String input = questionRunRequest.getInput();
        ThrowUtil.throwIf(StringUtils.isAnyBlank(code, language, input), ErrorCode.PARAMS_ERROR, "参数不能为空");

        QuestionSubmitLanguageEnum languageEnum = QuestionSubmitLanguageEnum.getEnumByValue(language);
        ThrowUtil.throwIf(languageEnum == null, ErrorCode.PARAMS_ERROR, "编程语言不存在");

        // 2. 调用代码沙箱执行代码
        CodeSandbox codeSandbox = CodeSandboxFactory.newInstance(type);
        codeSandbox = new CodeSandboxProxy(codeSandbox);

        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder()
                .code(code)
                .language(language)
                .inputList(Collections.singletonList(input))
                .build();
        ExecuteCodeResponse executeCodeResponse = codeSandbox.executeCode(executeCodeRequest);

        // 3. 构造运行结果
        QuestionRunResponse questionRunResponse = new QuestionRunResponse();
        questionRunResponse.setInput(input);
        questionRunResponse.setOutput(executeCodeResponse.getOutputList().get(0));
        questionRunResponse.setJudgeInfo(executeCodeResponse.getJudgeInfo());

        return questionRunResponse;
    }
}
