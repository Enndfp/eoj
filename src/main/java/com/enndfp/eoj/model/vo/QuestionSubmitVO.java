package com.enndfp.eoj.model.vo;

import cn.hutool.json.JSONUtil;
import com.enndfp.eoj.model.dto.questionsubmit.JudgeInfo;
import com.enndfp.eoj.model.entity.QuestionSubmit;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * 题目提交视图（脱敏）
 *
 * @auther Enndfp
 */
@Data
public class QuestionSubmitVO implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 编程语言
     */
    private String language;

    /**
     * 用户代码
     */
    private String code;

    /**
     * 判题信息
     */
    private JudgeInfo judgeInfo;

    /**
     * 判题状态（0 - 待判题、1 - 判题中、2 - 成功、3 - 失败）
     */
    private Integer status;

    /**
     * 题目 id
     */
    private Long questionId;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 创建用户信息
     */
    private UserVO userVO;

    /**
     * 题目信息
     */
    private QuestionVO questionVO;

    /**
     * 包装类转对象
     *
     * @param questionSubmitVO 题目提交包装类
     * @return 题目提交对象
     */
    public static QuestionSubmit voToObj(QuestionSubmitVO questionSubmitVO) {
        // 1. 判断包装类是否为空
        if (questionSubmitVO == null) {
            return null;
        }

        // 2. 将包装类转换为对象
        QuestionSubmit questionSubmit = new QuestionSubmit();
        BeanUtils.copyProperties(questionSubmitVO, questionSubmit);
        JudgeInfo judgeInfo = questionSubmitVO.getJudgeInfo();
        if (judgeInfo != null) {
            questionSubmit.setJudgeInfo(JSONUtil.toJsonStr(judgeInfo));
        }

        return questionSubmit;
    }

    /**
     * 对象转包装类
     *
     * @param questionSubmit 题目提交对象
     * @return 题目提交包装类
     */
    public static QuestionSubmitVO objToVo(QuestionSubmit questionSubmit) {
        // 1. 判断对象是否为空
        if (questionSubmit == null) {
            return null;
        }

        // 2. 将对象转换为包装类
        QuestionSubmitVO questionSubmitVO = new QuestionSubmitVO();
        BeanUtils.copyProperties(questionSubmit, questionSubmitVO);
        String judgeInfoStr = questionSubmit.getJudgeInfo();
        questionSubmitVO.setJudgeInfo(JSONUtil.toBean(judgeInfoStr, JudgeInfo.class));

        return questionSubmitVO;
    }

    private static final long serialVersionUID = 1L;
}