package com.youqi.usercenter.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

/**
 * 用户实体类
 */
@Data
@TableName(value = "users")
public class User {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 密码哈希
     */
    private String passwordHash;

    /**
     * 昵称
     */
    private String nickname;

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

    /**
     * 注册时间
     */
    private Date registeredAt;

    /**
     * 最后登录时间
     */
    private Date lastLoginAt;

    /**
     * 创建时间
     */
    private Date createdAt;

    /**
     * 更新时间
     */
    private Date updatedAt;
}