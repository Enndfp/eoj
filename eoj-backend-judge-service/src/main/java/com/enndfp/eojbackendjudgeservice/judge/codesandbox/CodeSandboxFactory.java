package com.enndfp.eojbackendjudgeservice.judge.codesandbox;

import com.enndfp.eojbackendjudgeservice.judge.codesandbox.impl.ExampleCodeSandbox;
import com.enndfp.eojbackendjudgeservice.judge.codesandbox.impl.RemoteCodeSandbox;
import com.enndfp.eojbackendjudgeservice.judge.codesandbox.impl.ThirdPartyCodeSandbox;

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
