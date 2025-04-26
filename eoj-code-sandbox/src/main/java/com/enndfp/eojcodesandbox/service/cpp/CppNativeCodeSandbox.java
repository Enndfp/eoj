package com.enndfp.eojcodesandbox.service.cpp;

import com.enndfp.eojcodesandbox.model.dto.ExecuteCodeRequest;
import com.enndfp.eojcodesandbox.model.dto.ExecuteCodeResponse;
import org.springframework.stereotype.Component;

/**
 * C++ 原生代码沙箱实现
 *
 * @author Enndfp
 */
@Component
public class CppNativeCodeSandbox extends CppCodeSandboxTemplate {
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        return super.executeCode(executeCodeRequest);
    }
}