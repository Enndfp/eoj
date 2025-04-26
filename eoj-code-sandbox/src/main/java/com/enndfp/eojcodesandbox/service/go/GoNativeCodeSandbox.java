package com.enndfp.eojcodesandbox.service.go;

import com.enndfp.eojcodesandbox.model.dto.ExecuteCodeRequest;
import com.enndfp.eojcodesandbox.model.dto.ExecuteCodeResponse;
import org.springframework.stereotype.Component;

/**
 * Go 原生代码沙箱实现
 *
 * @author Enndfp
 */
@Component
public class GoNativeCodeSandbox extends GoCodeSandboxTemplate {
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        return super.executeCode(executeCodeRequest);
    }
}