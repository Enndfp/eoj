package com.enndfp.eoj.judge.strategy;

import com.enndfp.eoj.model.dto.questionsubmit.JudgeInfo;

/**
 * 判题策略
 *
 * @author Enndfp
 */
public interface JudgeStrategy {

    /**
     * 执行判题
     *
     * @param judgeContext 判题上下文
     * @return 判题信息
     */
    JudgeInfo doJudge(JudgeContext judgeContext);
}
