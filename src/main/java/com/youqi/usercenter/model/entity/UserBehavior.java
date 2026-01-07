package com.youqi.usercenter.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 用户行为记录表实体类
 */
@Data
@TableName(value = "user_behavior")
public class UserBehavior {
    /**
     * 行为记录ID（主键）
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID（关联sys_user.id，NULL为匿名访客）
     */
    private Long userId;

    /**
     * 行为类型：1-文章访问，2-文章点赞，3-评论发布
     */
    private Integer behaviorType;

    /**
     * 目标ID（如文章ID、评论ID，对应behavior_type）
     */
    private Long targetId;

    /**
     * 用户IP地址
     */
    private String ipAddress;

    /**
     * 行为发生时间
     */
    private Date createTime;
}