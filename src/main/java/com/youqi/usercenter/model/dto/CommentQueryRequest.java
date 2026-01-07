package com.youqi.usercenter.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 评论查询请求体
 */
@Data
public class CommentQueryRequest implements Serializable {

    /**
     * 文章id
     */
    private Long articleId;

    /**
     * 父评论id
     */
    private Long parentId;

    /**
     * 状态（0-待审核，1-已通过，2-已拒绝）
     */
    private String status;

    /**
     * 评论人id
     */
    private Long userId;

    /**
     * 当前页码
     */
    private Integer current = 1;

    /**
     * 每页条数
     */
    private Integer pageSize = 10;

    private static final long serialVersionUID = 1L;
}