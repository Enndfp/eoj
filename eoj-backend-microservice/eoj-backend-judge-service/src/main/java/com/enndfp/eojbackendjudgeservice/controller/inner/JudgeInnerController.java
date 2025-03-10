package com.enndfp.eojbackendjudgeservice.controller.inner;

import com.enndfp.eojbackendcommon.common.BaseResponse;
import com.enndfp.eojbackendcommon.common.ErrorCode;
import com.enndfp.eojbackendcommon.common.ResultUtil;
import com.enndfp.eojbackendcommon.exception.ThrowUtil;
import com.enndfp.eojbackendjudgeservice.judge.JudgeService;
import com.enndfp.eojbackendmodel.model.dto.question.QuestionRunRequest;
import com.enndfp.eojbackendmodel.model.dto.question.QuestionRunResponse;
import com.enndfp.eojbackendmodel.model.entity.QuestionSubmit;
import com.enndfp.eojbackendserviceclient.service.JudgeFeignClient;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 判题内部接口
 *
 * @author Enndfp
 */
@RestController
@RequestMapping("/inner")
public class JudgeInnerController implements JudgeFeignClient {

    @Resource
    private JudgeService judgeService;

    /**
     * 判题
     *
     * @param questionSubmitId 提交记录ID
     * @return 提交记录
     */
    @Override
    @PostMapping("/do")
    @ApiOperation(value = "判题")
    public QuestionSubmit doJudge(@RequestParam("questionSubmitId") Long questionSubmitId) {
        return judgeService.doJudge(questionSubmitId);
    }

    /**
     * 运行代码
     *
     * @param questionRunRequest 运行代码请求
     * @return 运行结果
     */
    @Override
    @PostMapping("/do/run")
    @ApiOperation(value = "运行代码")
    public BaseResponse<QuestionRunResponse> doQuestionRun(QuestionRunRequest questionRunRequest) {
        // 1. 校验请求参数
        ThrowUtil.throwIf(questionRunRequest == null, ErrorCode.PARAMS_ERROR);

        // 2. 处理运行代码逻辑
        QuestionRunResponse questionRunResponse = judgeService.doQuestionRun(questionRunRequest);

        return ResultUtil.success(questionRunResponse);
    }

}
