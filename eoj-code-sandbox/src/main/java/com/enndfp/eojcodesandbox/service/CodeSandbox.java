package com.enndfp.eojcodesandbox.service;

import com.enndfp.eojcodesandbox.model.dto.ExecuteCodeRequest;
import com.enndfp.eojcodesandbox.model.dto.ExecuteCodeResponse;

/**
 * 代码沙箱接口
 *
 * @author Enndfp
 */
public interface CodeSandbox {

    /**
     * 执行代码
     *
     * @param executeCodeRequest 执行代码请求
     * @return 执行代码响应
     */
    ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest);
}
