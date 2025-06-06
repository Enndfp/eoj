package com.enndfp.eojbackendjudgeservice.judge.strategy;

import cn.hutool.json.JSONUtil;
import com.enndfp.eojbackendmodel.model.codesandbox.JudgeInfo;
import com.enndfp.eojbackendmodel.model.dto.question.JudgeCase;
import com.enndfp.eojbackendmodel.model.dto.question.JudgeConfig;
import com.enndfp.eojbackendmodel.model.entity.Question;
import com.enndfp.eojbackendmodel.model.enums.JudgeInfoMessageEnum;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Java语言判题策略
 *
 * @author Enndfp
 */
public class JavaLanguageJudgeStrategy implements JudgeStrategy {

    @Override
    public JudgeInfo doJudge(JudgeContext judgeContext) {
        JudgeInfo judgeInfo = judgeContext.getJudgeInfo();
        Long time = Optional.ofNullable(judgeInfo.getTime()).orElse(0L);
        Long memory = Optional.ofNullable(judgeInfo.getMemory()).orElse(0L);
        List<String> inputList = judgeContext.getInputList();
        List<String> outputList = judgeContext.getOutputList();
        List<JudgeCase> judgeCaseList = judgeContext.getJudgeCaseList();
        Question question = judgeContext.getQuestion();

        JudgeInfo judgeInfoResponse = new JudgeInfo();
        judgeInfoResponse.setTime(time);
        judgeInfoResponse.setMemory(memory);

        // 1. 判断沙箱执行的结果输出数量是否和预期输出数量相等
        JudgeInfoMessageEnum judgeInfoMessageEnum = JudgeInfoMessageEnum.ACCEPTED;
        if (outputList.size() != inputList.size()) {
            judgeInfoMessageEnum = JudgeInfoMessageEnum.ANSWER_ERROR;
            judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
            return judgeInfoResponse;
        }

        // 2. 判断沙箱执行的结果输出是否和预期输出相等
        for (int i = 0; i < judgeCaseList.size(); i++) {
            JudgeCase judgeCase = judgeCaseList.get(i);
            if (!Objects.equals(judgeCase.getOutput().trim(), outputList.get(i).trim())) {
                judgeInfoMessageEnum = JudgeInfoMessageEnum.ANSWER_ERROR;
                judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
                return judgeInfoResponse;
            }
        }

        // 3. 判断沙箱执行的结果时间和内存是否超过限制
        String judgeConfigStr = question.getJudgeConfig();
        JudgeConfig judgeConfig = JSONUtil.toBean(judgeConfigStr, JudgeConfig.class);
        Long expectTimeLimit = judgeConfig.getTimeLimit();
        Long expectMemoryLimit = judgeConfig.getMemoryLimit();
        // Java 程序本身需要额外执行10秒钟
        long JAVA_PROGRAM_TIME_COST = 1000L;
        if ((time - JAVA_PROGRAM_TIME_COST) > expectTimeLimit) {
            judgeInfoMessageEnum = JudgeInfoMessageEnum.TIME_LIMIT_EXCEEDED;
            judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
            return judgeInfoResponse;
        }
        if (memory > expectMemoryLimit) {
            judgeInfoMessageEnum = JudgeInfoMessageEnum.MEMORY_LIMIT_EXCEEDED;
            judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
            return judgeInfoResponse;
        }

        // 4. 返回判题信息
        judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());

        return judgeInfoResponse;
    }
}
