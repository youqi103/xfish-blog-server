package com.youqi.usercenter.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.youqi.usercenter.model.dto.ArticleStatisticsQueryRequest;
import com.youqi.usercenter.model.dto.UserStatisticsQueryRequest;
import com.youqi.usercenter.model.entity.ArticleStatistics;
import com.youqi.usercenter.model.entity.UserStatistics;
import com.youqi.usercenter.model.vo.*;

import java.util.Date;

/**
 * 统计服务接口
 */
public interface StatisticsService extends IService<UserStatistics> {

    /**
     * 初始化用户统计
     */
    boolean initUserStatistics(Long userId);

    /**
     * 更新用户文章统计
     */
    boolean updateUserArticleCount(Long userId, int delta);

    /**
     * 更新用户评论统计
     */
    boolean updateUserCommentCount(Long userId, int delta);

    /**
     * 更新用户点赞统计
     */
    boolean updateUserLikeCount(Long userId, int delta);

    /**
     * 更新用户被点赞统计
     */
    boolean updateUserLikedCount(Long userId, int delta);

    /**
     * 更新用户访问统计
     */
    boolean updateUserViewCount(Long userId, int delta);

    /**
     * 获取用户统计信息
     */
    UserStatistics getUserStatistics(Long userId);

    /**
     * 分页查询用户统计列表
     */
    Page<UserStatisticsVO> listUserStatistics(UserStatisticsQueryRequest queryRequest);

    /**
     * 初始化文章统计
     */
    boolean initArticleStatistics(Long articleId);

    /**
     * 更新文章访问统计
     */
    boolean updateArticleViewCount(Long articleId, int delta);

    /**
     * 更新文章点赞统计
     */
    boolean updateArticleLikeCount(Long articleId, int delta);

    /**
     * 更新文章评论统计
     */
    boolean updateArticleCommentCount(Long articleId, int delta);

    /**
     * 更新文章分享统计
     */
    boolean updateArticleShareCount(Long articleId, int delta);

    /**
     * 获取文章统计信息
     */
    ArticleStatistics getArticleStatistics(Long articleId);

    /**
     * 分页查询文章统计列表
     */
    Page<ArticleStatisticsVO> listArticleStatistics(ArticleStatisticsQueryRequest queryRequest);

    /**
     * 构建用户统计查询条件
     */
    QueryWrapper<UserStatistics> getUserStatisticsQueryWrapper(UserStatisticsQueryRequest queryRequest);

    /**
     * 构建文章统计查询条件
     */
    QueryWrapper<ArticleStatistics> getArticleStatisticsQueryWrapper(ArticleStatisticsQueryRequest queryRequest);

    // ========== 综合统计方法 ==========

    /**
     * 获取概览统计
     */
    OverviewStatisticsVO getOverviewStatistics();

    /**
     * 获取访问量趋势
     */
    java.util.List<VisitTrendVO> getVisitTrendStatistics(Date startDate, Date endDate);

    /**
     * 获取访问来源
     */
    java.util.List<VisitSourceVO> getVisitSourceStatistics(Date startDate, Date endDate);

    /**
     * 获取点赞趋势
     */
    java.util.List<LikeTrendVO> getLikeTrendStatistics(Date startDate, Date endDate);

    /**
     * 获取评论统计详情
     */
    CommentStatisticsVO getCommentStatistics(Date startDate, Date endDate);
}