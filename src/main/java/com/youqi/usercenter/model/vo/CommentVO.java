package com.youqi.usercenter.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 评论视图对象
 * 包含前端需要的所有字段
 */
@Data
public class CommentVO implements Serializable {

    /**
     * 评论ID
     */
    private Long id;

    /**
     * 文章ID
     */
    private Long articleId;

    /**
     * 文章标题
     */
    private String articleTitle;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名称（兼容字段，与authorName相同）
     */
    private String userName;

    /**
     * 作者名称
     */
    private String authorName;

    /**
     * 作者邮箱
     */
    private String authorEmail;

    /**
     * 作者网站
     */
    private String authorWebsite;

    /**
     * 作者头像
     */
    private String authorAvatar;

    /**
     * 父评论ID
     */
    private Long parentId;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 状态（0-待审核，1-已通过，2-已拒绝，3-已删除）
     */
    private Integer status;

    /**
     * IP地址
     */
    private String ipAddress;

    /**
     * 用户代理
     */
    private String userAgent;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 回复数
     */
    private Integer replyCount;

    /**
     * 举报数
     */
    private Integer reportCount;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 子评论列表
     */
    private List<CommentVO> children;

    private static final long serialVersionUID = 1L;
}
