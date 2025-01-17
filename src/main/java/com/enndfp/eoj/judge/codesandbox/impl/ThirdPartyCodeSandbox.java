package com.enndfp.eoj.judge.codesandbox.impl;

import com.enndfp.eoj.judge.codesandbox.CodeSandbox;
import com.enndfp.eoj.judge.codesandbox.model.ExecuteCodeRequest;
import com.enndfp.eoj.judge.codesandbox.model.ExecuteCodeResponse;

/**
 * 第三方代码沙箱
 *
 * @author Enndfp
 */
public class ThirdPartyCodeSandbox implements CodeSandbox {

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        System.out.println("第三方代码沙箱");
        return null;
    }

}
