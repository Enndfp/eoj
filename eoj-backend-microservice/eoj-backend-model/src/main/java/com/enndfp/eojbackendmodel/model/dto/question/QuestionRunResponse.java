package com.enndfp.eojbackendmodel.model.dto.question;

import com.enndfp.eojbackendmodel.model.codesandbox.JudgeInfo;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Enndfp
 */
@Data
public class QuestionRunResponse implements Serializable {

    /**
     * 输入用例
     */
    private String input;

    /**
     * 输出结果
     */
    private String output;

    /**
     * 判题信息
     */
    private JudgeInfo judgeInfo;

    private static final long serialVersionUID = 1L;
}
