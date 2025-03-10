package com.enndfp.eojbackendserviceclient.service;

import com.enndfp.eojbackendcommon.common.BaseResponse;
import com.enndfp.eojbackendmodel.model.dto.question.QuestionRunRequest;
import com.enndfp.eojbackendmodel.model.dto.question.QuestionRunResponse;
import com.enndfp.eojbackendmodel.model.entity.QuestionSubmit;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 判题服务
 *
 * @author Enndfp
 */
@FeignClient(name = "eoj-backend-judge-service", path = "/api/judge/inner")
public interface JudgeFeignClient {

    /**
     * 判题
     *
     * @param questionSubmitId 提交记录ID
     * @return 提交记录
     */
    @PostMapping("/do")
    QuestionSubmit doJudge(@RequestParam("questionSubmitId") Long questionSubmitId);

    /**
     * 运行代码
     *
     * @param questionRunRequest 运行代码请求
     * @return 运行结果
     */
    @PostMapping("/do/run")
    BaseResponse<QuestionRunResponse> doQuestionRun(@RequestBody QuestionRunRequest questionRunRequest);
}
