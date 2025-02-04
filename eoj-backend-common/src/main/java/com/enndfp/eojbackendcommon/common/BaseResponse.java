package com.enndfp.eojbackendcommon.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 通用返回类
 *
 * @param <T> 返回数据类型
 * @author Enndfp
 */
@Data
public class BaseResponse<T> implements Serializable {

    /**
     * 状态码
     */
    private int code;

    /**
     * 返回数据
     */
    private T data;

    /**
     * 返回消息
     */
    private String message;

    /**
     * 构造函数
     *
     * @param code    状态码
     * @param data    返回数据
     * @param message 返回消息
     */
    public BaseResponse(int code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    /**
     * 构造函数
     *
     * @param code 状态码
     * @param data 返回数据
     */
    public BaseResponse(int code, T data) {
        this(code, data, "");
    }

    /**
     * 构造函数
     *
     * @param errorCode 错误码
     */
    public BaseResponse(ErrorCode errorCode) {
        this(errorCode.getCode(), null, errorCode.getMessage());
    }
}
