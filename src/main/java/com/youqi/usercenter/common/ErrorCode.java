package com.youqi.usercenter.common;

import lombok.Getter;

/**
 * 错误码
 */
@Getter
public enum ErrorCode {

    SUCCESS(0, "ok", "操作成功"),

    // 参数错误 (40000-40099)
    PARAMS_ERROR(40000, "请求参数错误", "请求参数格式不正确或缺少必要参数"),
    NULL_ERROR(40001, "请求数据为空", "请求体或关键参数为空"),
    PARAMS_VALIDATION_ERROR(40002, "参数校验失败", "参数格式不符合要求"),
    PARAMS_LENGTH_ERROR(40003, "参数长度错误", "参数长度不符合要求"),

    // 认证授权错误 (40100-40199)
    NOT_LOGIN(40100, "未登录", "用户未登录或登录已过期"),
    NO_AUTH(40101, "无权限", "用户没有执行此操作的权限"),
    TOKEN_EXPIRED(40102, "令牌过期", "访问令牌已过期"),
    TOKEN_INVALID(40103, "令牌无效", "访问令牌格式错误或无效"),

    // 禁止访问 (40300-40399)
    FORBIDDEN(40300, "禁止访问", "访问被拒绝"),
    ACCOUNT_DISABLED(40301, "账号已禁用", "用户账号已被管理员禁用"),
    IP_LIMITED(40302, "IP限制", "当前IP地址访问受限"),

    // 资源不存在 (40400-40499)
    NOT_FOUND(40400, "请求数据不存在", "请求的资源不存在"),
    USER_NOT_FOUND(40401, "用户不存在", "指定的用户ID不存在"),
    ARTICLE_NOT_FOUND(40402, "文章不存在", "指定的文章ID不存在"),
    COMMENT_NOT_FOUND(40403, "评论不存在", "指定的评论ID不存在"),

    // 系统错误 (50000-50099)
    SYSTEM_ERROR(50000, "系统内部异常", "服务器内部错误，请联系管理员"),
    OPERATION_ERROR(50001, "操作失败", "数据库操作失败"),
    DATABASE_ERROR(50002, "数据库错误", "数据库操作异常"),
    NETWORK_ERROR(50003, "网络错误", "网络连接异常"),
    FILE_UPLOAD_ERROR(50004, "文件上传失败", "文件上传过程中发生错误"),

    // 业务逻辑错误 (50100-50199)
    USERNAME_EXISTS(50100, "用户名已存在", "该用户名已被注册"),
    EMAIL_EXISTS(50101, "邮箱已存在", "该邮箱已被注册"),
    PHONE_EXISTS(50102, "手机号已存在", "该手机号已被注册"),
    PASSWORD_ERROR(50103, "密码错误", "用户名或密码不正确"),
    ACCOUNT_LOCKED(50104, "账号锁定", "账号因多次登录失败被锁定"),
    VERIFICATION_CODE_ERROR(50105, "验证码错误", "验证码不正确或已过期");

    private final int code;

    private final String message;

    private final String description;

    ErrorCode(int code, String message, String description) {
        this.code = code;
        this.message = message;
        this.description = description;
    }
}