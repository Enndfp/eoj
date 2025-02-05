package com.enndfp.eojbackendjudgeservice.judge.codesandbox.impl;

import com.enndfp.eojbackendjudgeservice.judge.codesandbox.CodeSandbox;
import com.enndfp.eojbackendmodel.model.codesandbox.ExecuteCodeRequest;
import com.enndfp.eojbackendmodel.model.codesandbox.ExecuteCodeResponse;
import com.enndfp.eojbackendmodel.model.codesandbox.JudgeInfo;
import com.enndfp.eojbackendmodel.model.enums.JudgeInfoMessageEnum;
import com.enndfp.eojbackendmodel.model.enums.QuestionSubmitStatusEnum;

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
