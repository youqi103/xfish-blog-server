package com.youqi.usercenter.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 评论审核请求体
 */
@Data
public class CommentAuditRequest implements Serializable {

    /**
     * 评论id
     */
    private Long commentId;

    /**
     * 审核状态（1-已通过，2-已拒绝）
     */
    private Integer auditStatus;

    /**
     * 审核意见
     */
    private String auditRemark;

    private static final long serialVersionUID = 1L;
}