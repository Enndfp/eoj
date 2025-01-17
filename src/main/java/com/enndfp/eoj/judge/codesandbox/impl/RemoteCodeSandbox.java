package com.enndfp.eoj.judge.codesandbox.impl;

import com.enndfp.eoj.judge.codesandbox.CodeSandbox;
import com.enndfp.eoj.judge.codesandbox.model.ExecuteCodeRequest;
import com.enndfp.eoj.judge.codesandbox.model.ExecuteCodeResponse;

/**
 * 远程代码沙箱
 *
 * @author Enndfp
 */
public class RemoteCodeSandbox implements CodeSandbox {

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        System.out.println("远程代码沙箱");
        return null;
    }

}
