package com.enndfp.eojcodesandbox;

import com.enndfp.eojcodesandbox.model.ExecuteCodeRequest;
import com.enndfp.eojcodesandbox.model.ExecuteCodeResponse;
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
