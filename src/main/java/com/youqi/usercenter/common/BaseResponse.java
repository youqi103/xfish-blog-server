package com.youqi.usercenter.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 通用返回类
 */
@Data
public class BaseResponse<T> implements Serializable {

    private int code;

    private T data;

    private String message;

    private String description;

    private String timestamp;

    private String path;

    // 新增成功状态标识
    private boolean success;

    public BaseResponse(int code, T data, String message, String description) {
        this.code = code;
        this.data = data;
        this.message = message;
        this.description = description;
        this.timestamp = String.valueOf(System.currentTimeMillis());
        // 设置成功状态（code=0为成功）
        this.success = code == 0;
    }

    public BaseResponse(int code, T data, String message) {
        this(code, data, message, "");
    }

    public BaseResponse(int code, String message) {
        this(code, null, message, "");
    }

    public BaseResponse(ErrorCode errorCode) {
        this(errorCode.getCode(), null, errorCode.getMessage(), errorCode.getDescription());
    }

    public BaseResponse(ErrorCode errorCode, String customMessage) {
        this(errorCode.getCode(), null, customMessage, errorCode.getDescription());
    }

    public BaseResponse(ErrorCode errorCode, String customMessage, String customDescription) {
        this(errorCode.getCode(), null, customMessage, customDescription);
    }

    public void setPath(String path) {
        this.path = path;
    }
}