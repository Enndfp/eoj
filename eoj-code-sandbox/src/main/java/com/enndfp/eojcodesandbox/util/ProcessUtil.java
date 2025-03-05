package com.enndfp.eojcodesandbox.util;

import com.enndfp.eojcodesandbox.model.dto.ExecuteMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.StopWatch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 进程工具类
 *
 * @author Enndfp
 */
public class ProcessUtil {
    private static final Logger LOGGER = Logger.getLogger(ProcessUtil.class.getName());

    /**
     * 运行进程并获取消息
     *
     * @param runProcess 运行进程
     * @param opName     操作名称
     * @return 执行消息
     */
    public static ExecuteMessage runProcessAndGetMessage(Process runProcess, String opName) {
        ExecuteMessage executeMessage = new ExecuteMessage();
        final long initialMemory = getUsedMemory();

        try {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();

            // 等待进程执行完毕
            int exitValue = runProcess.waitFor();
            executeMessage.setExitValue(exitValue);

            // 标准输出流处理
            String normalOutput = captureStream(runProcess.getInputStream());
            // 错误输出流处理
            String errorOutput = captureStream(runProcess.getErrorStream());

            // 记录执行结果
            if (exitValue == 0) {
                LOGGER.info(opName + "成功");
            } else {
                LOGGER.warning(opName + "失败，退出码：" + exitValue);
            }

            executeMessage.setMessage(normalOutput);
            executeMessage.setErrorMessage(errorOutput);

            stopWatch.stop();
            long finalMemory = getUsedMemory();
            long memoryUsage = finalMemory - initialMemory;

            // 转换成KB
            executeMessage.setMemory(memoryUsage / 1024);
            executeMessage.setTime(stopWatch.getTotalTimeMillis());

        } catch (InterruptedException e) {
            LOGGER.log(Level.SEVERE, opName + "进程执行中断", e);
            Thread.currentThread().interrupt();
        }

        return executeMessage;
    }

    /**
     * 捕获输入流内容
     *
     * @param inputStream 输入流
     * @return 捕获的内容字符串
     */
    private static String captureStream(java.io.InputStream inputStream) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            List<String> outputList = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                outputList.add(line);
            }
            return StringUtils.join(outputList, "\n");
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "流捕获异常", e);
            return "";
        }
    }

    /**
     * 获取已使用内存
     *
     * @return 已使用内存
     */
    public static long getUsedMemory() {
        Runtime runtime = Runtime.getRuntime();
        return runtime.totalMemory() - runtime.freeMemory();
    }
}