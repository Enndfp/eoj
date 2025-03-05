package com.enndfp.eojcodesandbox.service.java;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.enndfp.eojcodesandbox.enums.QuestionSubmitStatusEnum;
import com.enndfp.eojcodesandbox.model.ExecuteCodeRequest;
import com.enndfp.eojcodesandbox.model.ExecuteCodeResponse;
import com.enndfp.eojcodesandbox.model.ExecuteMessage;
import com.enndfp.eojcodesandbox.model.JudgeInfo;
import com.enndfp.eojcodesandbox.service.CodeSandbox;
import com.enndfp.eojcodesandbox.util.ProcessUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Java代码沙箱模板
 *
 * @author Enndfp
 */
@Slf4j
public abstract class JavaCodeSandboxTemplate implements CodeSandbox {

    /**
     * 全局代码目录名称
     */
    public static final String GLOBAL_CODE_DIR_NAME = "tmpCode";

    /**
     * 全局Java类名称
     */
    public static final String GLOBAL_JAVA_CLASS_NAME = "Main.java";

    /**
     * 超时时间
     */
    public static final long TIME_OUT = 10000L;

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        String code = executeCodeRequest.getCode();
        String language = executeCodeRequest.getLanguage();
        List<String> inputList = executeCodeRequest.getInputList();

        // 1. 创建用户代码文件
        File userCodeFile = saveCodeToFile(code);

        // 2. 编译用户代码
        ExecuteMessage compileMessage = compileFile(userCodeFile);
        System.out.println(compileMessage);

        // 3. 执行用户代码
        List<ExecuteMessage> executeMessageList = runFile(userCodeFile, inputList);

        // 4. 封装执行结果
        ExecuteCodeResponse executeCodeResponse = getOutputResponse(executeMessageList);

        // 5. 删除用户代码文件
        boolean b = deleteFile(userCodeFile);
        if (!b) {
            log.error("deleteFile error,userCodeFilePath = {}", userCodeFile.getAbsolutePath());
        }

        return executeCodeResponse;
    }

    /**
     * 创建用户代码文件
     *
     * @param code 用户代码
     * @return 用户代码文件
     */
    public File saveCodeToFile(String code) {
        // 1. 创建全局代码目录
        String userDir = System.getProperty("user.dir");
        String globalCodePathName = userDir + File.separator + GLOBAL_CODE_DIR_NAME;
        if (!FileUtil.exist(globalCodePathName)) {
            FileUtil.mkdir(globalCodePathName);
        }

        // 2. 创建用户代码文件
        String userCodeParentPath = globalCodePathName + File.separator + UUID.randomUUID();
        String userCodePath = userCodeParentPath + File.separator + GLOBAL_JAVA_CLASS_NAME;

        return FileUtil.writeUtf8String(code, userCodePath);
    }

    /**
     * 编译用户代码
     *
     * @param userCodeFile 用户代码文件
     * @return 执行消息
     */
    public ExecuteMessage compileFile(File userCodeFile) {
        String compileCmd = String.format("javac -encoding utf-8 %s", userCodeFile.getAbsoluteFile());
        try {
            Process compileProcess = Runtime.getRuntime().exec(compileCmd);
            ExecuteMessage executeMessage = ProcessUtil.runProcessAndGetMessage(compileProcess, "编译");
            if (executeMessage.getExitValue() != 0) {
                throw new RuntimeException("编译错误");
            }
            return executeMessage;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 运行用户代码
     *
     * @param userCodeFile 用户代码文件
     * @param inputList    输入参数列表
     * @return 执行消息列表
     */
    public List<ExecuteMessage> runFile(File userCodeFile, List<String> inputList) {
        // 1. 获取用户代码文件的父目录
        String userCodeParentPath = userCodeFile.getParentFile().getAbsolutePath();
        List<ExecuteMessage> executeMessageList = new ArrayList<>();

        // 2. 遍历输入参数列表，执行用户代码
        for (String inputArgs : inputList) {
            String runCmd = String.format("java -Dfile.encoding=UTF-8 -cp %s Main %s", userCodeParentPath, inputArgs);
            try {
                Process runProcess = Runtime.getRuntime().exec(runCmd);
                // 超时控制
                new Thread(() -> {
                    try {
                        Thread.sleep(TIME_OUT);
                        runProcess.destroy();
                        System.out.println("超过程序最大运行时间，终止进程");
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }).start();
                ExecuteMessage executeMessage = ProcessUtil.runProcessAndGetMessage(runProcess, "运行");
                System.out.println(executeMessage);
                executeMessageList.add(executeMessage);
            } catch (Exception e) {
                throw new RuntimeException("执行错误", e);
            }
        }

        return executeMessageList;
    }

    /**
     * 获取输出响应
     *
     * @param executeMessageList 执行消息列表
     * @return 执行代码响应
     */
    public ExecuteCodeResponse getOutputResponse(List<ExecuteMessage> executeMessageList) {
        // 1. 封装执行结果
        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        List<String> outputList = new ArrayList<>();
        long maxTime = 0;
        long maxMemory = 0;

        // 2. 遍历执行消息列表
        for (ExecuteMessage executeMessage : executeMessageList) {
            // 2.1 获取错误消息
            String errorMessage = executeMessage.getErrorMessage();
            if (StrUtil.isNotBlank(errorMessage)) {
                outputList.add(executeMessage.getMessage());
                executeCodeResponse.setMessage(errorMessage);
                executeCodeResponse.setStatus(QuestionSubmitStatusEnum.FAILED.getValue());
                break;
            }
            // 2.2 获取输出消息
            outputList.add(executeMessage.getMessage());
            // 2.3 获取执行时间和内存
            Long time = executeMessage.getTime();
            if (time != null) {
                maxTime = Math.max(maxTime, time);
            }
            Long memory = executeMessage.getMemory();
            if (memory != null) {
                maxMemory = Math.max(maxMemory, memory);
            }
        }

        // 3. 判断是否全部执行成功，封装执行结果
        if (outputList.size() == executeMessageList.size()) {
            executeCodeResponse.setStatus(QuestionSubmitStatusEnum.SUCCESS.getValue());
            executeCodeResponse.setMessage(QuestionSubmitStatusEnum.SUCCESS.getText());
        }
        executeCodeResponse.setOutputList(outputList);
        JudgeInfo judgeInfo = new JudgeInfo();
        judgeInfo.setTime(maxTime);
        judgeInfo.setMemory(maxMemory);
        executeCodeResponse.setJudgeInfo(judgeInfo);

        return executeCodeResponse;
    }

    /**
     * 删除用户代码文件
     *
     * @param userCodeFile 用户代码文件
     * @return 是否删除成功
     */
    public boolean deleteFile(File userCodeFile) {
        if (userCodeFile.getParentFile() != null) {
            String userCodeParentPath = userCodeFile.getParentFile().getAbsolutePath();
            boolean del = FileUtil.del(userCodeParentPath);
            System.out.println("删除" + (del ? "成功" : "失败"));
            return del;
        }
        return true;
    }
}
