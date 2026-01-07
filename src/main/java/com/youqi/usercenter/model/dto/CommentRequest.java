package com.youqi.usercenter.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 评论新增/修改请求体
 */
@Data
public class CommentRequest implements Serializable {

    /**
     * 文章id
     */
    private Long articleId;

    /**
     * 父评论id（为0表示顶级评论）
     */
    private Long parentId = 0L;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 评论人昵称
     */
    private String userNickname;

    /**
     * 评论人邮箱
     */
    private String userEmail;

    /**
     * 评论人网站
     */
    private String userWebsite;

    private static final long serialVersionUID = 1L;
}