package com.enndfp.eojcodesandbox.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 执行代码请求
 *
 * @author Enndfp
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExecuteCodeRequest implements Serializable {

    /**
     * 代码
     */
    private String code;

    /**
     * 编程语言
     */
    private String language;

    /**
     * 输入列表
     */
    private List<String> inputList;

    private static final long serialVersionUID = 1L;
}
