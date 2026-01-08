package com.youqi.usercenter.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 概览统计视图对象
 */
@Data
public class OverviewStatisticsVO implements Serializable {

    /**
     * 今日访问量
     */
    private Long todayVisits;

    /**
     * 访问量增长率（百分比）
     */
    private Double visitsGrowth;

    /**
     * 总点赞数
     */
    private Long totalLikes;

    /**
     * 点赞增长率（百分比）
     */
    private Double likesGrowth;

    /**
     * 总评论数
     */
    private Long totalComments;

    /**
     * 评论增长率（百分比）
     */
    private Double commentsGrowth;

    /**
     * 活跃用户数
     */
    private Long activeUsers;

    /**
     * 用户增长率（百分比）
     */
    private Double usersGrowth;

    /**
     * 文章总数
     */
    private Long articleCount;

    /**
     * 总访问量
     */
    private Long totalViewCount;

    /**
     * 用户总数
     */
    private Long userCount;

    private static final long serialVersionUID = 1L;
}