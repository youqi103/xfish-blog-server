package com.youqi.usercenter.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 文章查询请求体
 */
@Data
public class ArticleQueryRequest implements Serializable {

    /**
     * 标题（模糊查询）
     */
    private String title;

    /**
     * 作者id
     */
    private Long authorId;

    /**
     * 分类id
     */
    private Long categoryId;

    /**
     * 分类名称（模糊查询）
     */
    private String categoryName;

    /**
     * 状态（0-草稿，1-已发布，2-已下线）
     */
    private String status;

    /**
     * 标签
     */
    private String tags;

    /**
     * 标签名称（模糊查询）
     */
    private String tagName;

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