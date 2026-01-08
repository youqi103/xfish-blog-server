package com.youqi.usercenter.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 评论统计详情视图对象
 */
@Data
public class CommentStatisticsVO implements Serializable {

    /**
     * 评论总数
     */
    private Long totalComments;

    /**
     * 日均评论数
     */
    private Double dailyAverage;

    /**
     * 评论参与率（百分比）
     */
    private Double participationRate;

    /**
     * 平均回复数
     */
    private Double avgReplies;

    /**
     * 评论热度指数
     */
    private Double heatIndex;

    /**
     * 评论状态分布
     */
    private List<CommentStatusDistributionVO> distribution;

    /**
     * 小时分布
     */
    private List<CommentHourDistributionVO> hourDistribution;

    /**
     * 热门评论文章
     */
    private List<HotArticleVO> topArticles;

    private static final long serialVersionUID = 1L;
}