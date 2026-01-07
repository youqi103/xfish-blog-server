package com.youqi.usercenter.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 评论趋势视图对象
 */
@Data
public class CommentTrendVO implements Serializable {

    /**
     * 日期
     */
    private String date;

    /**
     * 评论数
     */
    private Long comments;

    private static final long serialVersionUID = 1L;
}