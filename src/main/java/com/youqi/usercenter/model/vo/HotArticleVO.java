package com.youqi.usercenter.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 热门文章视图对象
 */
@Data
public class HotArticleVO implements Serializable {

    /**
     * 文章ID
     */
    private Long id;

    /**
     * 文章标题
     */
    private String title;

    /**
     * 评论数
     */
    private Long commentCount;

    private static final long serialVersionUID = 1L;
}