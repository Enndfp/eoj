package com.enndfp.eojcodesandbox.service;

import cn.hutool.core.io.FileUtil;
import cn.hutool.dfa.FoundWord;
import cn.hutool.dfa.WordTree;
import com.enndfp.eojcodesandbox.model.dto.ExecuteCodeRequest;
import com.enndfp.eojcodesandbox.model.dto.ExecuteCodeResponse;
import com.enndfp.eojcodesandbox.model.dto.ExecuteMessage;
import com.enndfp.eojcodesandbox.model.dto.JudgeInfo;
import com.enndfp.eojcodesandbox.model.enums.JudgeInfoMessageEnum;
import com.enndfp.eojcodesandbox.model.enums.QuestionSubmitStatusEnum;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 代码沙箱模板基类
 *
 * @author Enndfp
 */
@Slf4j
public abstract class CodeSandboxTemplate implements CodeSandbox {

    /**
     * 全局代码目录名称
     */
    protected static final String GLOBAL_CODE_DIR_NAME = "tmpCode";

    /**
     * 超时时间
     */
    protected static final long TIME_OUT = 10000L;

    /**
     * 获取字典树（黑名单词汇）
     */
    protected abstract WordTree getWordTree();

    /**
     * 获取文件名
     */
    protected abstract String getCodeFileName();

    /**
     * 编译文件
     */
    protected abstract ExecuteMessage compileFile(File codeFile);

    /**
     * 运行文件
     */
    protected abstract List<ExecuteMessage> runFile(File codeFile, List<String> inputList);

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        String code = executeCodeRequest.getCode();
        String language = executeCodeRequest.getLanguage();
        List<String> inputList = executeCodeRequest.getInputList();

        //  校验代码中是否包含黑名单中的禁用词
        WordTree wordTree = getWordTree();
        if (wordTree != null) {
            FoundWord foundWord = wordTree.matchWord(code);
            if (foundWord != null) {
                String message = "包含禁止词：" + foundWord.getFoundWord();
                log.warn(message);
                return new ExecuteCodeResponse().builder()
                        .outputList(new ArrayList<>())
                        .message(message)
                        .status(QuestionSubmitStatusEnum.FAILED.getValue())
                        .judgeInfo(new JudgeInfo(JudgeInfoMessageEnum.DANGEROUS_OPERATION.getValue(), 0L, 0L))
                        .build();
            }
        }

        // 1. 创建用户代码文件
        File codeFile = saveCodeToFile(code);

        // 2. 编译用户代码
        ExecuteMessage compileMessage = compileFile(codeFile);
        log.info("编译结果: {}", compileMessage);
        if (compileMessage != null && compileMessage.getExitValue() != 0) {
            return handleCompileError(compileMessage);
        }

        // 3. 执行用户代码
        List<ExecuteMessage> executeMessageList = runFile(codeFile, inputList);

        // 4. 封装执行结果
        ExecuteCodeResponse executeCodeResponse = getOutputResponse(executeMessageList);

        // 5. 删除用户代码文件
        boolean b = deleteFile(codeFile);
        if (!b) {
            log.error("deleteFile error, codeFilePath = {}", codeFile.getAbsolutePath());
        }

        return executeCodeResponse;
    }

    /**
     * 创建用户代码文件
     */
    protected File saveCodeToFile(String code) {
        // 1. 创建全局代码目录
        String userDir = System.getProperty("user.dir");
        String globalCodePathName = userDir + File.separator + GLOBAL_CODE_DIR_NAME;
        if (!FileUtil.exist(globalCodePathName)) {
            FileUtil.mkdir(globalCodePathName);
        }

        // 2. 创建用户代码文件
        String userCodeParentPath = globalCodePathName + File.separator + UUID.randomUUID();
        String userCodePath = userCodeParentPath + File.separator + getCodeFileName();

        return FileUtil.writeUtf8String(code, userCodePath);
    }

    /**
     * 处理编译错误
     */
    protected ExecuteCodeResponse handleCompileError(ExecuteMessage compileMessage) {
        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        executeCodeResponse.setStatus(QuestionSubmitStatusEnum.FAILED.getValue());
        executeCodeResponse.setMessage(compileMessage.getErrorMessage());
        List<String> outputList = new ArrayList<>();
        outputList.add(compileMessage.getErrorMessage());
        executeCodeResponse.setOutputList(outputList);
        executeCodeResponse.setJudgeInfo(new JudgeInfo(JudgeInfoMessageEnum.COMPILE_ERROR.getValue(), 0L, 0L));
        return executeCodeResponse;
    }

    /**
     * 获取输出响应
     */
    protected ExecuteCodeResponse getOutputResponse(List<ExecuteMessage> executeMessageList) {
        // 1. 封装执行结果
        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        List<String> outputList = new ArrayList<>();
        long maxTime = 0;
        long maxMemory = 0;

        // 2. 遍历执行消息列表
        for (ExecuteMessage executeMessage : executeMessageList) {
            // 2.1 获取错误消息
            String errorMessage = executeMessage.getErrorMessage();
            if (errorMessage != null && !errorMessage.isEmpty()) {
                outputList.add(executeMessage.getMessage());
                executeCodeResponse.setMessage(errorMessage);
                executeCodeResponse.setStatus(QuestionSubmitStatusEnum.FAILED.getValue());
                break;
            }
            // 2.2 获取输出消息
            outputList.add(executeMessage.getMessage().trim());
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
     */
    protected boolean deleteFile(File codeFile) {
        if (codeFile.getParentFile() != null) {
            String userCodeParentPath = codeFile.getParentFile().getAbsolutePath();
            boolean del = FileUtil.del(userCodeParentPath);
            log.info("删除{}", (del ? "成功" : "失败"));
            return del;
        }
        return true;
    }
}