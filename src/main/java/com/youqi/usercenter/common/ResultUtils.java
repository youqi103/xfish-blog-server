package com.youqi.usercenter.common;

/**
 * 返回工具类
 */
public class ResultUtils {

    /**
     * 成功
     */
    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(0, data, "ok");
    }

    /**
     * 成功（带自定义消息）
     */
    public static <T> BaseResponse<T> success(T data, String message) {
        return new BaseResponse<>(0, data, message);
    }

    /**
     * 失败
     */
    public static BaseResponse<Object> error(ErrorCode errorCode) {
        return new BaseResponse<>(errorCode);
    }

    /**
     * 失败（带自定义消息）
     */
    public static BaseResponse<Object> error(ErrorCode errorCode, String message) {
        return new BaseResponse<>(errorCode, message);
    }

    /**
     * 失败（带自定义消息和描述）
     */
    public static BaseResponse<Object> error(ErrorCode errorCode, String message, String description) {
        return new BaseResponse<>(errorCode, message, description);
    }

    /**
     * 失败（自定义错误码）
     */
    public static BaseResponse<Object> error(int code, String message) {
        return new BaseResponse<>(code, null, message);
    }

    /**
     * 失败（自定义错误码和描述）
     */
    public static BaseResponse<Object> error(int code, String message, String description) {
        return new BaseResponse<>(code, null, message, description);
    }

    /**
     * 参数错误
     */
    public static BaseResponse<Object> paramsError(String message) {
        return new BaseResponse<>(ErrorCode.PARAMS_ERROR, message);
    }

    /**
     * 参数错误（带详细描述）
     */
    public static BaseResponse<Object> paramsError(String message, String description) {
        return new BaseResponse<>(ErrorCode.PARAMS_ERROR, message, description);
    }

    /**
     * 权限错误
     */
    public static BaseResponse<Object> noAuthError(String message) {
        return new BaseResponse<>(ErrorCode.NO_AUTH, message);
    }

    /**
     * 未登录错误
     */
    public static BaseResponse<Object> notLoginError() {
        return new BaseResponse<>(ErrorCode.NOT_LOGIN);
    }

    /**
     * 系统错误
     */
    public static BaseResponse<Object> systemError(String message) {
        return new BaseResponse<>(ErrorCode.SYSTEM_ERROR, message);
    }

    /**
     * 操作失败
     */
    public static BaseResponse<Object> operationError(String message) {
        return new BaseResponse<>(ErrorCode.OPERATION_ERROR, message);
    }

    /**
     * 数据不存在
     */
    public static BaseResponse<Object> notFoundError(String message) {
        return new BaseResponse<>(ErrorCode.NOT_FOUND, message);
    }
}