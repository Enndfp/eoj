package com.enndfp.eoj.judge;

import com.enndfp.eoj.model.entity.QuestionSubmit;

/**
 * 判题服务
 *
 * @author Enndfp
 */
public interface JudgeService {

    /**
     * 判题
     *
     * @param questionSubmitId 提交记录ID
     * @return 提交记录
     */
    QuestionSubmit doJudge(long questionSubmitId);
}
