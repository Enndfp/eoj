package com.enndfp.eojbackendjudgeservice.controller.inner;

import com.enndfp.eojbackendjudgeservice.judge.JudgeService;
import com.enndfp.eojbackendmodel.model.entity.QuestionSubmit;
import com.enndfp.eojbackendserviceclient.service.JudgeFeignClient;
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
    public QuestionSubmit doJudge(@RequestParam("questionSubmitId") Long questionSubmitId) {
        return judgeService.doJudge(questionSubmitId);
    }

}
