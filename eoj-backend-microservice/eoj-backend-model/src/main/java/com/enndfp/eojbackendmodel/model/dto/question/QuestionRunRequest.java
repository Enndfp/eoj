package com.enndfp.eojbackendmodel.model.dto.question;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Enndfp
 */
@Data
public class QuestionRunRequest implements Serializable {

    /**
     * 运行代码
     */
    private String code;

    /**
     * 编程语言
     */
    private String language;

    /**
     * 输入用例
     */
    private String input;

    private static final long serialVersionUID = 1L;
}
