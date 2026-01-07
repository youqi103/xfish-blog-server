package com.youqi.usercenter.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户新增/修改请求体
 */
@Data
public class UserRequest implements Serializable {

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 头像URL
     */
    private String avatar;

    /**
     * 用户角色（admin-管理员，user-普通用户）
     */
    private String role;

    /**
     * 账户状态（active-正常，inactive-未激活，banned-已封禁）
     */
    private String status;

    private static final long serialVersionUID = 1L;
}