package com.enndfp.eojcodesandbox.model.dto;

import lombok.Data;

/**
 * 执行消息
 *
 * @author Enndfp
 */
@Data
public class ExecuteMessage {

    /**
     * 退出值
     */
    private Integer exitValue;

    /**
     * 成功消息
     */
    private String message;

    /**
     * 错误消息
     */
    private String errorMessage;

    /**
     * 执行时间
     */
    private Long time;

    /**
     * 消耗内存
     */
    private Long memory;
}
