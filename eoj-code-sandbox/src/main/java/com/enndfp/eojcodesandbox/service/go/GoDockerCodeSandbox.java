package com.enndfp.eojcodesandbox.service.go;

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

import static com.enndfp.eojcodesandbox.constant.CodeBlackList.GO_BLACK_LIST;

/**
 * Go Docker 代码沙箱
 *
 * @author Enndfp
 */
@Slf4j
@Component
public class GoDockerCodeSandbox extends DockerCodeSandboxTemplate {

    /**
     * 全局 Go 文件名称
     */
    public static final String GLOBAL_GO_FILE_NAME = "main.go";

    /**
     * 字典树，Hutool
     */
    private static final WordTree WORD_TREE;

    static {
        // 初始化字典树
        WORD_TREE = new WordTree();
        WORD_TREE.addWords(GO_BLACK_LIST);
    }

    @Override
    protected WordTree getWordTree() {
        return WORD_TREE;
    }

    @Override
    protected String getCodeFileName() {
        return GLOBAL_GO_FILE_NAME;
    }

    @Override
    protected String getDockerImage() {
        return "golang:1.21-alpine";
    }

    @Override
    protected ExecuteMessage compileFile(File codeFile) {
        return null;
    }

    @Override
    protected ExecuteMessage compileFileInContainer(DockerClient dockerClient, String containerId, File codeFile) {
        StopWatch stopWatch = new StopWatch();
        // Go 编译命令：go build -o /app/main /app/main.go
        String[] cmdArray = new String[]{"go", "build", "-o", "/app/main", "/app/main.go"};
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
            // Go 执行命令：/app/main [args]
            String[] cmdArray = ArrayUtil.append(new String[]{"/app/main"}, inputArgsArray);

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