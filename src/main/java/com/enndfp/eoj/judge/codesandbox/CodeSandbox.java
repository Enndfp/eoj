package com.enndfp.eoj.judge.codesandbox;

import com.enndfp.eoj.judge.codesandbox.model.ExecuteCodeRequest;
import com.enndfp.eoj.judge.codesandbox.model.ExecuteCodeResponse;

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
