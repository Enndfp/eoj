package com.enndfp.eojbackendjudgeservice.judge;

import com.enndfp.eojbackendmodel.model.dto.question.QuestionRunRequest;
import com.enndfp.eojbackendmodel.model.dto.question.QuestionRunResponse;
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

    /**
     * 运行代码
     *
     * @param questionRunRequest 运行代码请求
     * @return 运行结果
     */
    QuestionRunResponse doQuestionRun(QuestionRunRequest questionRunRequest);
}
