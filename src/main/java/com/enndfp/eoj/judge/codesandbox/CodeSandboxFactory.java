package com.enndfp.eoj.judge.codesandbox;

import com.enndfp.eoj.judge.codesandbox.impl.ExampleCodeSandbox;
import com.enndfp.eoj.judge.codesandbox.impl.RemoteCodeSandbox;
import com.enndfp.eoj.judge.codesandbox.impl.ThirdPartyCodeSandbox;

/**
 * 代码沙箱工厂
 *
 * @author Enndfp
 */
public class CodeSandboxFactory {

    /**
     * 创建代码沙箱
     *
     * @param type 代码沙箱类型
     * @return 代码沙箱
     */
    public static CodeSandbox newInstance(String type) {
        switch (type) {
            case "example":
                return new ExampleCodeSandbox();
            case "remote":
                return new RemoteCodeSandbox();
            case "thirdParty":
                return new ThirdPartyCodeSandbox();
            default:
                return null;
        }
    }
}
