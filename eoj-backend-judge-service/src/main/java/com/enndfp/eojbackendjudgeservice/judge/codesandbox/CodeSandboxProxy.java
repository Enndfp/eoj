package com.enndfp.eojbackendjudgeservice.judge.codesandbox;

import com.enndfp.eojbackendmodel.model.codesandbox.ExecuteCodeRequest;
import com.enndfp.eojbackendmodel.model.codesandbox.ExecuteCodeResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Enndfp
 */
@Slf4j
public class CodeSandboxProxy implements CodeSandbox {

    /**
     * 代码沙箱
     */
    private final CodeSandbox codeSandbox;

    public CodeSandboxProxy(CodeSandbox codeSandbox) {
        this.codeSandbox = codeSandbox;
    }

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        log.info("代码沙箱请求信息：{}", executeCodeRequest);
        ExecuteCodeResponse executeCodeResponse = codeSandbox.executeCode(executeCodeRequest);
        log.info("代码沙箱响应信息：{}", executeCodeResponse);

        return executeCodeResponse;
    }
}
