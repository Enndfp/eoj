package com.enndfp.eoj.judge.codesandbox.model;

import com.enndfp.eoj.model.dto.questionsubmit.JudgeInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 执行代码响应
 *
 * @author Enndfp
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExecuteCodeResponse implements Serializable {

    /**
     * 接口信息
     */
    private String message;

    /**
     * 执行状态
     */
    private Integer status;

    /**
     * 判题信息
     */
    private JudgeInfo judgeInfo;

    /**
     * 输出列表
     */
    private List<String> outputList;

    private static final long serialVersionUID = 1L;
}
