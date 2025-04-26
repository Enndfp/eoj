package com.enndfp.eojcodesandbox.service.cpp;

import cn.hutool.dfa.WordTree;
import com.enndfp.eojcodesandbox.model.dto.ExecuteMessage;
import com.enndfp.eojcodesandbox.service.CodeSandboxTemplate;
import com.enndfp.eojcodesandbox.util.ProcessUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.enndfp.eojcodesandbox.constant.CodeBlackList.CPP_BLACK_LIST;

/**
 * C++代码沙箱模板
 *
 * @author Enndfp
 */
@Slf4j
public class CppCodeSandboxTemplate extends CodeSandboxTemplate {

    /**
     * 全局C++源文件名称
     */
    private static final String GLOBAL_CPP_FILE_NAME = "main.cpp";

    /**
     * 编译后的可执行文件名称
     */
    private static final String EXECUTABLE_NAME = "main";

    /**
     * 字典树，Hutool
     */
    private static final WordTree WORD_TREE;

    static {
        // 初始化字典树
        WORD_TREE = new WordTree();
        WORD_TREE.addWords(CPP_BLACK_LIST);
    }

    @Override
    protected WordTree getWordTree() {
        return WORD_TREE;
    }

    @Override
    protected String getCodeFileName() {
        return GLOBAL_CPP_FILE_NAME;
    }

    @Override
    protected ExecuteMessage compileFile(File codeFile) {
        String parentPath = codeFile.getParentFile().getAbsolutePath();
        String executablePath = parentPath + File.separator + EXECUTABLE_NAME;
        String compileCmd = String.format("g++ -o %s %s -std=c++17", executablePath, codeFile.getAbsolutePath());
        try {
            Process compileProcess = Runtime.getRuntime().exec(compileCmd);
            ExecuteMessage executeMessage = ProcessUtil.runProcessAndGetMessage(compileProcess, "编译");
            return executeMessage;
        } catch (IOException e) {
            log.error("编译失败", e);
            ExecuteMessage executeMessage = new ExecuteMessage();
            executeMessage.setExitValue(1);
            executeMessage.setErrorMessage("编译错误: " + e.getMessage());
            return executeMessage;
        }
    }

    @Override
    protected List<ExecuteMessage> runFile(File codeFile, List<String> inputList) {
        String parentPath = codeFile.getParentFile().getAbsolutePath();
        String executablePath = parentPath + File.separator + EXECUTABLE_NAME + ".exe";
        List<ExecuteMessage> executeMessageList = new ArrayList<>();

        for (String inputArgs : inputList) {
            String runCmd = String.format("\"%s\" %s", executablePath, inputArgs);
            try {
                Process runProcess = Runtime.getRuntime().exec(runCmd);
                // 超时控制
                new Thread(() -> {
                    try {
                        Thread.sleep(TIME_OUT);
                        if (runProcess.isAlive()) {
                            runProcess.destroy();
                            log.info("超过程序最大运行时间，终止进程");
                        }
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }).start();
                ExecuteMessage executeMessage = ProcessUtil.runProcessAndGetMessage(runProcess, "运行");
                executeMessageList.add(executeMessage);
            } catch (Exception e) {
                log.error("运行失败", e);
                ExecuteMessage executeMessage = new ExecuteMessage();
                executeMessage.setExitValue(1);
                executeMessage.setErrorMessage("运行错误: " + e.getMessage());
                executeMessageList.add(executeMessage);
            }
        }

        return executeMessageList;
    }
}