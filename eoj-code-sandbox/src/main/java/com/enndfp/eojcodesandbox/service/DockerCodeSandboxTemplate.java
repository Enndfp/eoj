package com.enndfp.eojcodesandbox.service;

import com.enndfp.eojcodesandbox.model.dto.ExecuteMessage;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.*;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.command.ExecStartResultCallback;
import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Docker 代码沙箱基类，封装通用 Docker 操作
 *
 * @author Enndfp
 */
@Slf4j
public abstract class DockerCodeSandboxTemplate extends CodeSandboxTemplate {

    /**
     * Docker 超时时间（毫秒）
     */
    protected static final long DOCKER_TIME_OUT = 5000L;

    /**
     * 第一次初始化标志
     */
    private static Boolean FIRST_INIT = true;

    /**
     * 获取 Docker 镜像名称，由子类提供
     *
     * @return 镜像名称
     */
    protected abstract String getDockerImage();

    /**
     * 在容器中编译代码，由子类实现
     *
     * @param dockerClient Docker 客户端
     * @param containerId  容器 ID
     * @param codeFile     代码文件
     * @return 编译结果
     */
    protected abstract ExecuteMessage compileFileInContainer(DockerClient dockerClient, String containerId, File codeFile);

    /**
     * 在容器中执行代码，由子类实现
     *
     * @param dockerClient Docker 客户端
     * @param containerId  容器 ID
     * @param inputList    输入参数列表
     * @return 执行结果列表
     */
    protected abstract List<ExecuteMessage> executeCodeInContainer(DockerClient dockerClient, String containerId, List<String> inputList);

    /**
     * 实现 CodeSandboxTemplate 的 runFile 方法，使用 Docker 沙箱执行
     *
     * @param codeFile  代码文件
     * @param inputList 输入参数列表
     * @return 执行结果列表
     */
    @Override
    protected List<ExecuteMessage> runFile(File codeFile, List<String> inputList) {
        return runWithDocker(codeFile, inputList);
    }

    /**
     * 执行 Docker 沙箱
     *
     * @param codeFile  代码文件
     * @param inputList 输入参数列表
     * @return 执行结果列表
     */
    public List<ExecuteMessage> runWithDocker(File codeFile, List<String> inputList) {
        // 1. 获取用户代码文件的父路径
        String codeParentPath = codeFile.getParentFile().getAbsolutePath();

        // 2. 获取默认的 Docker 客户端
        DockerClient dockerClient = DockerClientBuilder.getInstance().build();

        try {
            // 3. 拉取镜像（如果需要）
            pullImageIfNeeded(dockerClient, getDockerImage());

            // 4. 创建 Docker 容器
            CreateContainerResponse containerResponse = createDockerContainer(dockerClient, getDockerImage(), codeParentPath);

            // 5. 启动 Docker 容器
            String containerId = containerResponse.getId();
            startContainer(dockerClient, containerId);

            try {
                // 6. 编译并执行代码
                ExecuteMessage compileMessage = compileFileInContainer(dockerClient, containerId, codeFile);
                if (compileMessage.getExitValue() != 0) {
                    // 编译失败，直接返回
                    List<ExecuteMessage> result = new ArrayList<>();
                    result.add(compileMessage);
                    return result;
                }
                return executeCodeInContainer(dockerClient, containerId, inputList);
            } finally {
                // 7. 停止并删除容器
                try {
                    dockerClient.stopContainerCmd(containerId).exec();
                    dockerClient.removeContainerCmd(containerId).exec();
                } catch (Exception e) {
                    log.error("停止或删除容器失败", e);
                }
            }
        } finally {
            // 8. 关闭 Docker 客户端
            try {
                dockerClient.close();
            } catch (IOException e) {
                log.error("关闭 Docker 客户端失败", e);
            }
        }
    }

    /**
     * 拉取 Docker 镜像
     *
     * @param dockerClient Docker 客户端
     * @param image        镜像名称
     */
    protected void pullImageIfNeeded(DockerClient dockerClient, String image) {
        // 1. 判断是否需要拉取镜像（仅在第一次初始化时拉取）
        if (FIRST_INIT) {
            log.info("开始拉取 Docker 镜像: {}", image);
            // 2. 拉取镜像命令
            PullImageCmd pullImageCmd = dockerClient.pullImageCmd(image);

            // 3. 设置拉取镜像进度回调
            PullImageResultCallback callback = new PullImageResultCallback() {
                @Override
                public void onNext(PullResponseItem item) {
                    // 4. 输出镜像拉取进度
                    log.info("下载镜像：{}", item.getStatus());
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
                log.error("拉取镜像异常", e);
                throw new RuntimeException(e);
            }
        }
        // 8. 输出镜像下载完成信息
        log.info("镜像已准备完成");
    }

    /**
     * 创建 Docker 容器
     *
     * @param dockerClient   Docker 客户端
     * @param image          镜像名称
     * @param codeParentPath 用户代码的父路径
     * @return 创建的容器响应
     */
    protected CreateContainerResponse createDockerContainer(DockerClient dockerClient, String image, String codeParentPath) {
        // 1. 创建容器命令
        CreateContainerCmd containerCmd = dockerClient.createContainerCmd(image);

        // 2. 配置容器的 Host 配置（内存、CPU 等）
        HostConfig hostConfig = new HostConfig();
        hostConfig.withMemory(100 * 1000 * 1000L)  // 设置容器内存
                .withMemorySwap(0L)  // 禁用交换内存
                .withCpuCount(1L)  // 设置 CPU 核心数
                .setBinds(new Bind(codeParentPath, new Volume("/app")));  // 绑定用户代码目录到容器

        // 3. 执行创建容器命令并返回容器响应
        CreateContainerResponse response = containerCmd.withHostConfig(hostConfig)
                .withNetworkDisabled(true) // 禁用网络
                .withReadonlyRootfs(true) // 根文件系统只读
                .withAttachStdin(true)
                .withAttachStderr(true)
                .withAttachStdout(true)
                .withTty(true)
                .exec();

        log.info("创建 Docker 容器成功，容器 ID: {}", response.getId());
        return response;
    }

    /**
     * 启动 Docker 容器
     *
     * @param dockerClient Docker 客户端
     * @param containerId  容器 ID
     */
    protected void startContainer(DockerClient dockerClient, String containerId) {
        // 启动容器命令
        dockerClient.startContainerCmd(containerId).exec();
        log.info("启动容器成功，容器 ID: {}", containerId);
    }

    /**
     * 创建执行结果回调函数，用于处理命令执行的输出和错误信息
     *
     * @param message      输出消息
     * @param errorMessage 错误消息
     * @param timeout      是否超时
     * @return ExecStartResultCallback 实例
     */
    protected ExecStartResultCallback createExecResultCallback(final String[] message, final String[] errorMessage, final boolean[] timeout) {
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
                    log.info("输出错误结果：{}", errorMessage[0]);
                } else {
                    // 4. 如果是标准输出
                    message[0] = new String(frame.getPayload());
                    log.info("输出结果：{}", message[0]);
                }
                super.onNext(frame);
            }
        };
    }

    /**
     * 自定义统计回调类
     */
    protected static class StatisticsCallback implements ResultCallback<Statistics>, Closeable {
        private final Long[] memoryUsage;
        private boolean closed = false;

        public StatisticsCallback(Long[] memoryUsage) {
            this.memoryUsage = memoryUsage;
        }

        @Override
        public void onNext(Statistics statistics) {
            if (statistics != null && statistics.getMemoryStats() != null && statistics.getMemoryStats().getUsage() != null) {
                memoryUsage[0] = Math.max(memoryUsage[0], statistics.getMemoryStats().getUsage());
            }
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
            closed = true;
        }

        public boolean isClosed() {
            return closed;
        }
    }

    /**
     * 监控容器内存使用情况
     *
     * @param dockerClient Docker 客户端
     * @param containerId  容器 ID
     * @param memoryUsage  记录内存使用的数组
     * @return 统计回调
     */
    protected StatisticsCallback monitorMemoryUsage(DockerClient dockerClient, String containerId, Long[] memoryUsage) {
        // 1. 创建获取内存使用情况的命令
        StatsCmd statsCmd = dockerClient.statsCmd(containerId).withNoStream(false);

        // 2. 创建结果回调函数
        StatisticsCallback callback = new StatisticsCallback(memoryUsage);
        statsCmd.exec(callback);
        return callback;
    }
}
