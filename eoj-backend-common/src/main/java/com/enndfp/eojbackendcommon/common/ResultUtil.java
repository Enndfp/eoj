package com.enndfp.eojbackendcommon.common;

/**
 * 返回工具类
 *
 * @author Enndfp
 */
public class ResultUtil {

    /**
     * 成功
     *
     * @param data 数据
     * @param <T>  数据类型
     * @return BaseResponse
     */
    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(0, data, "ok");
    }

    /**
     * 失败
     *
     * @param errorCode 错误码
     * @return BaseResponse
     */
    public static BaseResponse error(ErrorCode errorCode) {
        return new BaseResponse<>(errorCode);
    }

    /**
     * 失败
     *
     * @param code    状态码
     * @param message 消息
     * @return BaseResponse
     */
    public static BaseResponse error(int code, String message) {
        return new BaseResponse(code, null, message);
    }

    /**
     * 失败
     *
     * @param errorCode 错误码
     * @return BaseResponse
     */
    public static BaseResponse error(ErrorCode errorCode, String message) {
        return new BaseResponse(errorCode.getCode(), null, message);
    }
}
