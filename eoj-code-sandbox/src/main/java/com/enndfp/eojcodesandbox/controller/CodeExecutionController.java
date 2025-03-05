package com.enndfp.eojcodesandbox.controller;

import com.enndfp.eojcodesandbox.model.dto.ExecuteCodeRequest;
import com.enndfp.eojcodesandbox.model.dto.ExecuteCodeResponse;
import com.enndfp.eojcodesandbox.service.CodeSandbox;
import com.enndfp.eojcodesandbox.strategy.CodeSandboxManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.enndfp.eojcodesandbox.constant.AuthConstant.AUTH_REQUEST_HEADER;
import static com.enndfp.eojcodesandbox.constant.AuthConstant.AUTH_REQUEST_SECRET;

/**
 * @author Enndfp
 */
@RestController("/")
public class CodeExecutionController {

    @Resource
    private CodeSandboxManager codeSandboxManager;

    /**
     * 执行代码
     *
     * @param executeCodeRequest 请求参数
     * @return 执行结果
     */
    @PostMapping("executeCode")
    ExecuteCodeResponse executeCode(@RequestBody ExecuteCodeRequest executeCodeRequest, HttpServletRequest request,
                                    HttpServletResponse response) {
        // 1. 校验请求参数
        if (executeCodeRequest == null) {
            throw new RuntimeException("请求参数为空");
        }

        // 2. 校验请求头中的请求密钥是否正确
        String requestSecret = request.getHeader(AUTH_REQUEST_HEADER);
        if (!AUTH_REQUEST_SECRET.equals(requestSecret)) {
            response.setStatus(403);
            return null;
        }

        // 3. 获取对应语言的沙箱实例
        String language = executeCodeRequest.getLanguage();
        CodeSandbox codeSandbox = codeSandboxManager.getSandBox(language);

        return codeSandbox.executeCode(executeCodeRequest);
    }
}
