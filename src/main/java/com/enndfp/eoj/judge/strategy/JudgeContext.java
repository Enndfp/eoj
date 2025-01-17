package com.enndfp.eoj.judge.strategy;

import com.enndfp.eoj.model.dto.question.JudgeCase;
import com.enndfp.eoj.model.dto.questionsubmit.JudgeInfo;
import com.enndfp.eoj.model.entity.Question;
import com.enndfp.eoj.model.entity.QuestionSubmit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 判题上下文
 *
 * @author Enndfp
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JudgeContext {

    /**
     * 判题信息
     */
    private JudgeInfo judgeInfo;

    /**
     * 输入列表
     */
    private List<String> inputList;

    /**
     * 输出列表
     */
    private List<String> outputList;

    /**
     * 判题用例列表
     */
    private List<JudgeCase> judgeCaseList;

    /**
     * 题目信息
     */
    private Question question;

    /**
     * 题目提交信息
     */
    private QuestionSubmit questionSubmit;
}
