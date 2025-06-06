package com.enndfp.eojcodesandbox.service.java;

import com.enndfp.eojcodesandbox.model.dto.ExecuteCodeRequest;
import com.enndfp.eojcodesandbox.model.dto.ExecuteCodeResponse;
import org.springframework.stereotype.Component;

/**
 * Java 原生代码沙箱实现（模板方法模式）
 *
 * @author Enndfp
 */
@Component
public class JavaNativeCodeSandbox extends JavaCodeSandboxTemplate {
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        return super.executeCode(executeCodeRequest);
    }
}
