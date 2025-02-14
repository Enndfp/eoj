package com.enndfp.eojcodesandbox.controller;

import com.enndfp.eojcodesandbox.JavaNativeCodeSandbox;
import com.enndfp.eojcodesandbox.model.ExecuteCodeRequest;
import com.enndfp.eojcodesandbox.model.ExecuteCodeResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Enndfp
 */
@RestController("/")
public class MainController {

    /**
     * 请求头
     */
    private static final String AUTH_REQUEST_HEADER = "auth";

    /**
     * 请求密钥
     */
    private static final String AUTH_REQUEST_SECRET = "secretKey";

    @Resource
    private JavaNativeCodeSandbox javaNativeCodeSandbox;

    @GetMapping("/health")
    public String health() {
        return "ok";
    }

    /**
     * 执行代码
     *
     * @param executeCodeRequest 请求参数
     * @return 执行结果
     */
    @PostMapping("executeCode")
    ExecuteCodeResponse executeCode(@RequestBody ExecuteCodeRequest executeCodeRequest, HttpServletRequest request,
                                    HttpServletResponse response) {
        String requestSecret = request.getHeader(AUTH_REQUEST_HEADER);
        if (!AUTH_REQUEST_SECRET.equals(requestSecret)) {
            response.setStatus(403);
            return null;
        }
        if (executeCodeRequest == null) {
            throw new RuntimeException("请求参数为空");
        }
        return javaNativeCodeSandbox.executeCode(executeCodeRequest);
    }
}
