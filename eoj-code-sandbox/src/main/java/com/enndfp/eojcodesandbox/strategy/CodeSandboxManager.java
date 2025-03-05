package com.enndfp.eojcodesandbox.strategy;

import com.enndfp.eojcodesandbox.service.java.JavaNativeCodeSandbox;
import com.enndfp.eojcodesandbox.service.CodeSandbox;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 沙箱管理器
 *
 * @author Enndfp
 */
@Service
public class CodeSandboxManager {

    /**
     * 沙箱映射
     */
    private static final Map<String, CodeSandbox> sandboxMap = new HashMap<>();

    static {
        // 初始化沙箱
        sandboxMap.put("java", new JavaNativeCodeSandbox());
        // TODO: 以后可以在此处继续添加其他语言的沙箱实现
    }

    /**
     * 获取沙箱
     *
     * @param language 编程语言
     * @return 对应语言的沙箱实例
     * @throws RuntimeException 如果不支持该语言，则抛出异常
     */
    public CodeSandbox getSandBox(String language) {
        // 获取对应语言的沙箱实例
        CodeSandbox sandbox = sandboxMap.get(language);

        // 如果不存在该语言的沙箱，抛出异常
        if (sandbox == null) {
            throw new RuntimeException("不支持该语言: " + language + ". 请在 SandboxManager 中添加该语言的沙箱支持。");
        }

        return sandbox;
    }
}
