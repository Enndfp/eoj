package com.enndfp.eoj.judge.codesandbox.impl;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.enndfp.eoj.common.ErrorCode;
import com.enndfp.eoj.exception.BusinessException;
import com.enndfp.eoj.judge.codesandbox.CodeSandbox;
import com.enndfp.eoj.judge.codesandbox.model.ExecuteCodeRequest;
import com.enndfp.eoj.judge.codesandbox.model.ExecuteCodeResponse;
import org.apache.commons.lang3.StringUtils;

/**
 * 远程代码沙箱
 *
 * @author Enndfp
 */
public class RemoteCodeSandbox implements CodeSandbox {

    /**
     * 请求头
     */
    private static final String AUTH_REQUEST_HEADER = "auth";

    /**
     * 请求密钥
     */
    private static final String AUTH_REQUEST_SECRET = "secretKey";

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        System.out.println("远程代码沙箱");
        String url = "http://localhost:8090/executeCode";
        String json = JSONUtil.toJsonStr(executeCodeRequest);
        String responseStr = HttpUtil.createPost(url)
                .header(AUTH_REQUEST_HEADER, AUTH_REQUEST_SECRET)
                .body(json)
                .execute()
                .body();
        if (StringUtils.isBlank(responseStr)) {
            throw new BusinessException(ErrorCode.API_REQUEST_ERROR, "executeCode remoteSandbox error, message = " + responseStr);
        }
        return JSONUtil.toBean(responseStr, ExecuteCodeResponse.class);
    }

}
