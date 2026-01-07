package com.youqi.usercenter.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 文章统计查询请求体
 */
@Data
public class ArticleStatisticsQueryRequest implements Serializable {

    /**
     * 文章id
     */
    private Long articleId;

    /**
     * 排序字段
     */
    private String sortField;

    /**
     * 排序方式（asc/desc）
     */
    private String sortOrder = "desc";

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