package com.youqi.usercenter.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 文章新增/修改请求体
 */
@Data
public class ArticleRequest implements Serializable {

    /**
     * 标题
     */
    private String title;

    /**
     * 内容（Markdown格式）
     */
    private String content;

    /**
     * 摘要
     */
    private String summary;

    /**
     * 封面图片URL
     */
    private String coverImage;

    /**
     * 分类id
     */
    private Long categoryId;

    /**
     * 标签（逗号分隔）
     */
    private String tags;

    /**
     * 状态（0-草稿，1-已发布，2-已下线）
     */
    private String status;

    /**
     * 是否允许评论（0-不允许，1-允许）
     */
    private Integer allowComment;

    /**
     * 是否置顶（0-不置顶，1-置顶）
     */
    private Integer isTop;

    private static final long serialVersionUID = 1L;
}