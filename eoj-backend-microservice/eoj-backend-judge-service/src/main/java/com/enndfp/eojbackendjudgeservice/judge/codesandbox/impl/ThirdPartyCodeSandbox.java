package com.enndfp.eojbackendjudgeservice.judge.codesandbox.impl;

import com.enndfp.eojbackendjudgeservice.judge.codesandbox.CodeSandbox;
import com.enndfp.eojbackendmodel.model.codesandbox.ExecuteCodeRequest;
import com.enndfp.eojbackendmodel.model.codesandbox.ExecuteCodeResponse;

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
