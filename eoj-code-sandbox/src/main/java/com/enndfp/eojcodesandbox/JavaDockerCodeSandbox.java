package com.enndfp.eojcodesandbox;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.ArrayUtil;
import com.enndfp.eojcodesandbox.model.ExecuteCodeRequest;
import com.enndfp.eojcodesandbox.model.ExecuteCodeResponse;
import com.enndfp.eojcodesandbox.model.ExecuteMessage;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.*;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.command.ExecStartResultCallback;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Java Docker 代码沙箱（模板方法模式）
 *
 * @author Enndfp
 */
@Component
public class JavaDockerCodeSandbox extends JavaCodeSandboxTemplate {

    /**
     * 超时时间
     */
    public static final long TIME_OUT = 5000L;

    /**
     * 第一次初始化
     */
    private static Boolean FIRST_INIT = true;

    public static void main(String[] args) {
        JavaDockerCodeSandboxOld javaDockerCodeSandbox = new JavaDockerCodeSandboxOld();
        String code = ResourceUtil.readUtf8Str("testCode/simpleComputeArgs" + File.separator + GLOBAL_JAVA_CLASS_NAME);
        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder()
                .code(code)
                .language("java")
                .inputList(Arrays.asList("1 2", "1 3"))
                .build();

        ExecuteCodeResponse executeCodeResponse = javaDockerCodeSandbox.executeCode(executeCodeRequest);
        System.out.println(executeCodeResponse);
    }

    @Override
    public List<ExecuteMessage> runFile(File userCodeFile, List<String> inputList) {
        // 1. 获取用户代码文件的父路径
        String userCodeParentPath = userCodeFile.getParentFile().getAbsolutePath();

        // 2. 获取默认的 Docker 客户端
        DockerClient dockerClient = DockerClientBuilder.getInstance().build();

        // 3. 拉取镜像（如果需要）
        String image = "openjdk:8-alpine";
        pullImageIfNeeded(dockerClient, image);

        // 4. 创建 Docker 容器
        CreateContainerResponse containerResponse = createDockerContainer(dockerClient, image, userCodeParentPath);

        // 5. 启动 Docker 容器
        String containerId = containerResponse.getId();
        startContainer(dockerClient, containerId);

        // 6. 执行代码并获取执行结果
        return executeCodeInContainer(dockerClient, containerId, inputList);
    }

    /**
     * 拉取 Docker 镜像
     *
     * @param dockerClient Docker 客户端
     * @param image        镜像名称
     */
    private void pullImageIfNeeded(DockerClient dockerClient, String image) {
        // 1. 判断是否需要拉取镜像（仅在第一次初始化时拉取）
        if (FIRST_INIT) {
            // 2. 拉取镜像命令
            PullImageCmd pullImageCmd = dockerClient.pullImageCmd(image);

            // 3. 设置拉取镜像进度回调
            PullImageResultCallback callback = new PullImageResultCallback() {
                @Override
                public void onNext(PullResponseItem item) {
                    // 4. 输出镜像拉取进度
                    System.out.println("下载镜像：" + item.getStatus());
                    super.onNext(item);
                }
            };

            try {
                // 5. 执行拉取镜像命令并等待完成
                pullImageCmd.exec(callback).awaitCompletion();
                // 6. 拉取完成后，将 FIRST_INIT 设置为 false，防止下次再次拉取
                FIRST_INIT = false;
            } catch (InterruptedException e) {
                // 7. 异常处理
                System.out.println("拉取镜像异常");
                throw new RuntimeException(e);
            }
        }
        // 8. 输出镜像下载完成信息
        System.out.println("镜像下载完成");
    }

    /**
     * 创建 Docker 容器
     *
     * @param dockerClient       Docker 客户端
     * @param image              镜像名称
     * @param userCodeParentPath 用户代码的父路径
     * @return 创建的容器响应
     */
    private CreateContainerResponse createDockerContainer(DockerClient dockerClient, String image, String userCodeParentPath) {
        // 1. 创建容器命令
        CreateContainerCmd containerCmd = dockerClient.createContainerCmd(image);

        // 2. 配置容器的 Host 配置（内存、CPU 等）
        HostConfig hostConfig = new HostConfig();
        hostConfig.withMemory(100 * 1000 * 1000L)  // 设置容器内存
                .withMemorySwap(0L)  // 禁用交换内存
                .withCpuCount(1L)  // 设置 CPU 核心数
                .setBinds(new Bind(userCodeParentPath, new Volume("/app")));  // 绑定用户代码目录到容器

        // 3. 执行创建容器命令并返回容器响应
        return containerCmd.withHostConfig(hostConfig)
                .withNetworkDisabled(true)  // 禁用网络
                .withReadonlyRootfs(true)  // 根文件系统只读
                .withAttachStdin(true)
                .withAttachStderr(true)
                .withAttachStdout(true)
                .withTty(true)
                .exec();
    }

    /**
     * 启动 Docker 容器
     *
     * @param dockerClient Docker 客户端
     * @param containerId  容器 ID
     */
    private void startContainer(DockerClient dockerClient, String containerId) {
        // 1. 启动容器命令
        dockerClient.startContainerCmd(containerId).exec();
    }

    /**
     * 在 Docker 容器中执行代码
     *
     * @param dockerClient Docker 客户端
     * @param containerId  容器 ID
     * @param inputList    用户输入的参数列表
     * @return 执行结果列表
     */
    private List<ExecuteMessage> executeCodeInContainer(DockerClient dockerClient, String containerId, List<String> inputList) {
        List<ExecuteMessage> executeMessageList = new ArrayList<>();

        // 1. 遍历每组输入参数并执行代码
        for (String inputArgs : inputList) {
            // 2. 创建计时器
            StopWatch stopWatch = new StopWatch();

            // 3. 将输入参数分割成数组
            String[] inputArgsArray = inputArgs.split(" ");

            // 4. 构建执行命令
            String[] cmdArray = ArrayUtil.append(new String[]{"java", "-cp", "/app", "Main"}, inputArgsArray);

            // 5. 创建执行命令
            ExecCreateCmdResponse execCreateCmdResponse = dockerClient.execCreateCmd(containerId)
                    .withCmd(cmdArray)
                    .withAttachStderr(true)
                    .withAttachStdin(true)
                    .withAttachStdout(true)
                    .exec();
            System.out.println("创建执行命令：" + execCreateCmdResponse);

            // 6. 准备执行结果的存储对象
            ExecuteMessage executeMessage = new ExecuteMessage();
            final String[] message = {null};
            final String[] errorMessage = {null};
            long time = 0L;
            final boolean[] timeout = {true};

            // 7. 获取执行命令的 ID
            String execId = execCreateCmdResponse.getId();

            // 8. 创建执行回调函数
            ExecStartResultCallback execStartResultCallback = createExecResultCallback(message, errorMessage, timeout);

            // 9. 监控容器的内存使用情况
            monitorMemoryUsage(dockerClient, containerId);

            try {
                // 10. 启动并执行命令，等待完成
                stopWatch.start();
                dockerClient.execStartCmd(execId)
                        .exec(execStartResultCallback)
                        .awaitCompletion(TIME_OUT, TimeUnit.MILLISECONDS);
                stopWatch.stop();
                time = stopWatch.getLastTaskTimeMillis();
            } catch (InterruptedException e) {
                // 11. 异常处理
                System.out.println("程序执行异常");
                throw new RuntimeException(e);
            }

            // 12. 将执行结果存储到列表
            executeMessage.setMessage(message[0]);
            executeMessage.setErrorMessage(errorMessage[0]);
            executeMessage.setTime(time);
            executeMessageList.add(executeMessage);
        }

        // 13. 返回所有执行结果
        return executeMessageList;
    }

    /**
     * 创建执行结果回调函数，用于处理命令执行的输出和错误信息
     *
     * @param message      输出消息
     * @param errorMessage 错误消息
     * @param timeout      是否超时
     * @return ExecStartResultCallback 实例
     */
    private ExecStartResultCallback createExecResultCallback(final String[] message, final String[] errorMessage, final boolean[] timeout) {
        return new ExecStartResultCallback() {
            @Override
            public void onComplete() {
                // 1. 完成回调，设置超时为 false
                timeout[0] = false;
                super.onComplete();
            }

            @Override
            public void onNext(Frame frame) {
                // 2. 处理输出帧
                StreamType streamType = frame.getStreamType();
                if (StreamType.STDERR.equals(streamType)) {
                    // 3. 如果是标准错误输出
                    errorMessage[0] = new String(frame.getPayload());
                    System.out.println("输出错误结果：" + errorMessage[0]);
                } else {
                    // 4. 如果是标准输出
                    message[0] = new String(frame.getPayload());
                    System.out.println("输出结果：" + message[0]);
                }
                super.onNext(frame);
            }
        };
    }

    /**
     * 监控容器的内存使用情况
     *
     * @param dockerClient Docker 客户端
     * @param containerId  容器 ID
     */
    private void monitorMemoryUsage(DockerClient dockerClient, String containerId) {
        // 1. 创建获取内存使用情况的命令
        StatsCmd statsCmd = dockerClient.statsCmd(containerId);

        // 2. 创建结果回调函数
        ResultCallback<Statistics> statisticsResultCallback = statsCmd.exec(new ResultCallback<Statistics>() {
            @Override
            public void onNext(Statistics statistics) {
                // 3. 获取并输出内存使用情况
                Long memoryUsage = statistics.getMemoryStats().getUsage();
                System.out.println("内存占用：" + memoryUsage);
            }

            @Override
            public void onStart(Closeable closeable) {
            }

            @Override
            public void onError(Throwable throwable) {
            }

            @Override
            public void onComplete() {
            }

            @Override
            public void close() throws IOException {
            }
        });

    }
}
