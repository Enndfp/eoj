package com.enndfp.eojcodesandbox.service.java;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.dfa.WordTree;
import com.enndfp.eojcodesandbox.model.dto.ExecuteMessage;
import com.enndfp.eojcodesandbox.service.DockerCodeSandboxTemplate;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.github.dockerjava.core.command.ExecStartResultCallback;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.enndfp.eojcodesandbox.constant.CodeBlackList.JAVA_BLACK_LIST;

/**
 * Java Docker 代码沙箱
 *
 * @author Enndfp
 */
@Slf4j
@Component
public class JavaDockerCodeSandbox extends DockerCodeSandboxTemplate {

    /**
     * 全局 Java 类名称
     */
    public static final String GLOBAL_JAVA_CLASS_NAME = "Main.java";

    /**
     * 字典树，Hutool
     */
    private static final WordTree WORD_TREE;

    static {
        // 初始化字典树
        WORD_TREE = new WordTree();
        WORD_TREE.addWords(JAVA_BLACK_LIST);
    }

    @Override
    protected WordTree getWordTree() {
        return WORD_TREE;
    }

    @Override
    protected String getCodeFileName() {
        return GLOBAL_JAVA_CLASS_NAME;
    }

    @Override
    protected ExecuteMessage compileFile(File codeFile) {
        return null;
    }

    @Override
    protected String getDockerImage() {
        return "openjdk:8-alpine";
    }

    @Override
    protected ExecuteMessage compileFileInContainer(DockerClient dockerClient, String containerId, File codeFile) {
        StopWatch stopWatch = new StopWatch();
        String[] cmdArray = new String[]{"javac", "-encoding", "utf-8", "/app/Main.java"};
        ExecCreateCmdResponse execCreateCmdResponse = dockerClient.execCreateCmd(containerId)
                .withCmd(cmdArray)
                .withAttachStderr(true)
                .withAttachStdout(true)
                .exec();
        log.info("创建编译命令: {}", execCreateCmdResponse.getId());

        ExecuteMessage executeMessage = new ExecuteMessage();
        final String[] message = {null};
        final String[] errorMessage = {null};
        long time = 0L;
        final boolean[] timeout = {true};

        ExecStartResultCallback execStartResultCallback = createExecResultCallback(message, errorMessage, timeout);

        try {
            stopWatch.start();
            dockerClient.execStartCmd(execCreateCmdResponse.getId())
                    .exec(execStartResultCallback)
                    .awaitCompletion(DOCKER_TIME_OUT, TimeUnit.MILLISECONDS);
            stopWatch.stop();
            time = stopWatch.getLastTaskTimeMillis();
        } catch (InterruptedException e) {
            log.error("编译异常", e);
            errorMessage[0] = "编译超时或发生错误: " + e.getMessage();
        }

        executeMessage.setMessage(message[0]);
        executeMessage.setErrorMessage(errorMessage[0]);
        executeMessage.setTime(time);
        executeMessage.setExitValue(timeout[0] ? 1 : 0);
        if (timeout[0]) {
            executeMessage.setErrorMessage("编译超时");
        }

        return executeMessage;
    }

    @Override
    protected List<ExecuteMessage> executeCodeInContainer(DockerClient dockerClient, String containerId, List<String> inputList) {
        List<ExecuteMessage> executeMessageList = new ArrayList<>();

        for (String inputArgs : inputList) {
            StopWatch stopWatch = new StopWatch();
            String[] inputArgsArray = inputArgs.split(" ");
            String[] cmdArray = ArrayUtil.append(new String[]{"java", "-cp", "/app", "Main"}, inputArgsArray);

            ExecCreateCmdResponse execCreateCmdResponse = dockerClient.execCreateCmd(containerId)
                    .withCmd(cmdArray)
                    .withAttachStderr(true)
                    .withAttachStdin(true)
                    .withAttachStdout(true)
                    .exec();
            log.info("创建执行命令: {}", execCreateCmdResponse.getId());

            ExecuteMessage executeMessage = new ExecuteMessage();
            final String[] message = {null};
            final String[] errorMessage = {null};
            long time = 0L;
            final boolean[] timeout = {true};

            String execId = execCreateCmdResponse.getId();
            ExecStartResultCallback execStartResultCallback = createExecResultCallback(message, errorMessage, timeout);

            Long[] memoryUsage = {0L};
            StatisticsCallback statsCallback = monitorMemoryUsage(dockerClient, containerId, memoryUsage);

            try {
                stopWatch.start();
                dockerClient.execStartCmd(execId)
                        .exec(execStartResultCallback)
                        .awaitCompletion(DOCKER_TIME_OUT, TimeUnit.MILLISECONDS);
                stopWatch.stop();
                time = stopWatch.getLastTaskTimeMillis();
            } catch (InterruptedException e) {
                log.error("程序执行异常", e);
                errorMessage[0] = "执行超时或发生错误: " + e.getMessage();
            } finally {
                try {
                    if (statsCallback != null) {
                        statsCallback.close();
                    }
                } catch (IOException e) {
                    log.error("关闭内存监控异常", e);
                }
            }

            executeMessage.setMessage(message[0]);
            executeMessage.setErrorMessage(errorMessage[0]);
            executeMessage.setTime(time);
            executeMessage.setMemory(memoryUsage[0]);
            executeMessage.setExitValue(timeout[0] ? 1 : 0);
            if (timeout[0]) {
                executeMessage.setErrorMessage("程序执行超时");
            }

            executeMessageList.add(executeMessage);
        }

        return executeMessageList;
    }
}