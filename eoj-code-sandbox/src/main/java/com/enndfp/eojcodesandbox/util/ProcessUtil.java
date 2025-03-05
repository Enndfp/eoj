package com.enndfp.eojcodesandbox.util;

import com.enndfp.eojcodesandbox.model.ExecuteMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.StopWatch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * 进程工具类
 *
 * @author Enndfp
 */
public class ProcessUtil {

    /**
     * 运行进程并获取消息
     *
     * @param runProcess 运行进程
     * @param opName     操作名称
     * @return 执行消息
     */
    public static ExecuteMessage runProcessAndGetMessage(Process runProcess, String opName) {
        ExecuteMessage executeMessage = new ExecuteMessage();
        long initialMemory = getUsedMemory();

        try {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            // 等待进程执行完毕
            int exitValue = runProcess.waitFor();
            executeMessage.setExitValue(exitValue);
            if (exitValue == 0) {
                // 获取进程的正常输出信息（如开发者调试信息）
                System.out.println(opName + "成功");
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(runProcess.getInputStream(), StandardCharsets.UTF_8));
                List<String> outputList = new ArrayList<>();
                String compileOutputLine;
                while ((compileOutputLine = bufferedReader.readLine()) != null) {
                    outputList.add(compileOutputLine);
                }
                executeMessage.setMessage(StringUtils.join(outputList, "\n"));
            } else {
                System.out.println(opName + "失败，退出码：" + exitValue);
                // 获取进程的正常输出信息（如开发者调试信息）
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(runProcess.getInputStream(), StandardCharsets.UTF_8));
                List<String> outputList = new ArrayList<>();
                String compileOutputLine;
                while ((compileOutputLine = bufferedReader.readLine()) != null) {
                    outputList.add(compileOutputLine);
                }
                executeMessage.setMessage(StringUtils.join(outputList, "\n"));

                // 获取进程的错误输出信息
                BufferedReader errorBufferedReader = new BufferedReader(new InputStreamReader(runProcess.getErrorStream(), StandardCharsets.UTF_8));
                List<String> errorOutputList = new ArrayList<>();
                String errorCompileOutputLine;
                while ((errorCompileOutputLine = errorBufferedReader.readLine()) != null) {
                    errorOutputList.add(errorCompileOutputLine);
                }
                executeMessage.setErrorMessage(StringUtils.join(errorOutputList, "\n"));
            }
            stopWatch.stop();
            long finalMemory = getUsedMemory();
            long memoryUsage = finalMemory - initialMemory;
            // 转换成KB
            executeMessage.setMemory(memoryUsage / 1024);
            executeMessage.setTime(stopWatch.getTotalTimeMillis());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return executeMessage;
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
