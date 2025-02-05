package com.enndfp.eojbackendserviceclient.service;

import com.enndfp.eojbackendmodel.model.entity.QuestionSubmit;

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
