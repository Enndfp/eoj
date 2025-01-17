package com.enndfp.eoj.judge.codesandbox.impl;

import com.enndfp.eoj.judge.codesandbox.CodeSandbox;
import com.enndfp.eoj.judge.codesandbox.model.ExecuteCodeRequest;
import com.enndfp.eoj.judge.codesandbox.model.ExecuteCodeResponse;
import com.enndfp.eoj.model.dto.questionsubmit.JudgeInfo;
import com.enndfp.eoj.model.enums.JudgeInfoMessageEnum;
import com.enndfp.eoj.model.enums.QuestionSubmitStatusEnum;

/**
 * 示例代码沙箱
 *
 * @author Enndfp
 */
public class ExampleCodeSandbox implements CodeSandbox {

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        JudgeInfo judgeInfo = new JudgeInfo();
        judgeInfo.setMessage(JudgeInfoMessageEnum.ACCEPTED.getText());
        judgeInfo.setMemory(100L);
        judgeInfo.setTime(100L);

        ExecuteCodeResponse executeCodeResponse = ExecuteCodeResponse.builder()
                .message("示例代码沙箱")
                .outputList(executeCodeRequest.getInputList())
                .status(QuestionSubmitStatusEnum.SUCCESS.getValue())
                .judgeInfo(judgeInfo)
                .build();

        return executeCodeResponse;
    }

}
