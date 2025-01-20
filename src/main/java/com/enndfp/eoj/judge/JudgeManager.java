package com.enndfp.eoj.judge;

import com.enndfp.eoj.judge.strategy.DefaultJudgeStrategy;
import com.enndfp.eoj.judge.strategy.JavaLanguageJudgeStrategy;
import com.enndfp.eoj.judge.strategy.JudgeContext;
import com.enndfp.eoj.judge.strategy.JudgeStrategy;
import com.enndfp.eoj.judge.codesandbox.model.JudgeInfo;
import com.enndfp.eoj.model.entity.QuestionSubmit;
import org.springframework.stereotype.Service;

/**
 * 判题管理器
 *
 * @author Enndfp
 */
@Service
public class JudgeManager {

    /**
     * 执行判题
     *
     * @param judgeContext 判题上下文
     * @return 判题信息
     */
    JudgeInfo doJudge(JudgeContext judgeContext) {
        // 1. 获取题目提交信息
        QuestionSubmit questionSubmit = judgeContext.getQuestionSubmit();
        String language = questionSubmit.getLanguage();

        // 2. 根据语言选择判题策略
        JudgeStrategy judgeStrategy = new DefaultJudgeStrategy();
        if ("java".equals(language)) {
            judgeStrategy = new JavaLanguageJudgeStrategy();
        }

        return judgeStrategy.doJudge(judgeContext);
    }
}
