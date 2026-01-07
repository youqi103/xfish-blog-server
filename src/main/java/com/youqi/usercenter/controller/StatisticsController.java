package com.youqi.usercenter.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youqi.usercenter.common.BaseResponse;
import com.youqi.usercenter.common.ErrorCode;
import com.youqi.usercenter.common.ResultUtils;
import com.youqi.usercenter.exception.BusinessException;
import com.youqi.usercenter.model.dto.ArticleStatisticsQueryRequest;
import com.youqi.usercenter.model.dto.UserStatisticsQueryRequest;
import com.youqi.usercenter.model.entity.ArticleStatistics;
import com.youqi.usercenter.model.entity.UserStatistics;
import com.youqi.usercenter.model.vo.*;
import com.youqi.usercenter.service.StatisticsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 统计控制器
 */
@Api(tags = "统计管理")
@RestController
@RequestMapping("/statistics")
public class StatisticsController {

    @Resource
    private StatisticsService statisticsService;

    // ========== 基础统计接口 ==========

    /**
     * 获取用户统计信息
     */
    @ApiOperation("获取用户统计信息")
    @GetMapping("/user/get")
    public BaseResponse<UserStatistics> getUserStatistics(@RequestParam Long userId) {
        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        UserStatistics statistics = statisticsService.getUserStatistics(userId);
        if (statistics == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "用户统计信息不存在");
        }

        return ResultUtils.success(statistics);
    }

    /**
     * 分页查询用户统计列表
     */
    @ApiOperation("分页查询用户统计列表")
    @GetMapping("/user/list")
    public BaseResponse<Page<UserStatisticsVO>> listUserStatistics(UserStatisticsQueryRequest queryRequest) {
        if (queryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        Page<UserStatisticsVO> statisticsPage = statisticsService.listUserStatistics(queryRequest);
        return ResultUtils.success(statisticsPage);
    }

    /**
     * 获取文章统计信息
     */
    @ApiOperation("获取文章统计信息")
    @GetMapping("/article/get")
    public BaseResponse<ArticleStatistics> getArticleStatistics(@RequestParam Long articleId) {
        if (articleId == null || articleId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        ArticleStatistics statistics = statisticsService.getArticleStatistics(articleId);
        if (statistics == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "文章统计信息不存在");
        }

        return ResultUtils.success(statistics);
    }

    /**
     * 分页查询文章统计列表
     */
    @ApiOperation("分页查询文章统计列表")
    @GetMapping("/article/list")
    public BaseResponse<Page<ArticleStatisticsVO>> listArticleStatistics(ArticleStatisticsQueryRequest queryRequest) {
        if (queryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        Page<ArticleStatisticsVO> statisticsPage = statisticsService.listArticleStatistics(queryRequest);
        return ResultUtils.success(statisticsPage);
    }

    /**
     * 获取热门文章（按访问量排序）
     */
    @ApiOperation("获取热门文章")
    @GetMapping("/article/hot")
    public BaseResponse<Page<ArticleStatisticsVO>> getHotArticles(@RequestParam(defaultValue = "1") Integer current,
                                                                 @RequestParam(defaultValue = "10") Integer pageSize) {
        ArticleStatisticsQueryRequest queryRequest = new ArticleStatisticsQueryRequest();
        queryRequest.setCurrent(current);
        queryRequest.setPageSize(pageSize);
        queryRequest.setSortField("viewCount");
        queryRequest.setSortOrder("desc");

        Page<ArticleStatisticsVO> statisticsPage = statisticsService.listArticleStatistics(queryRequest);
        return ResultUtils.success(statisticsPage);
    }

    /**
     * 获取热门用户（按文章数排序）
     */
    @ApiOperation("获取热门用户")
    @GetMapping("/user/hot")
    public BaseResponse<Page<UserStatisticsVO>> getHotUsers(@RequestParam(defaultValue = "1") Integer current,
                                                           @RequestParam(defaultValue = "10") Integer pageSize) {
        UserStatisticsQueryRequest queryRequest = new UserStatisticsQueryRequest();
        queryRequest.setCurrent(current);
        queryRequest.setPageSize(pageSize);
        queryRequest.setSortField("articleCount");
        queryRequest.setSortOrder("desc");

        Page<UserStatisticsVO> statisticsPage = statisticsService.listUserStatistics(queryRequest);
        return ResultUtils.success(statisticsPage);
    }

    // ========== 综合统计接口 ==========

    /**
     * 获取概览统计
     */
    @ApiOperation("获取概览统计")
    @GetMapping("/overview")
    public BaseResponse<OverviewStatisticsVO> getOverviewStatistics() {
        OverviewStatisticsVO overview = statisticsService.getOverviewStatistics();
        return ResultUtils.success(overview);
    }

    /**
     * 获取访问量趋势
     */
    @ApiOperation("获取访问量趋势")
    @GetMapping("/visit/trend")
    public BaseResponse<List<VisitTrendVO>> getVisitTrendStatistics(
            @RequestParam(required = false) String range) {
        Date[] dates = calculateDateRange(range);
        List<VisitTrendVO> trend = statisticsService.getVisitTrendStatistics(dates[0], dates[1]);
        return ResultUtils.success(trend);
    }

    /**
     * 获取访问来源
     */
    @ApiOperation("获取访问来源")
    @GetMapping("/visit/sources")
    public BaseResponse<List<VisitSourceVO>> getVisitSourceStatistics(
            @RequestParam(required = false) String range) {
        Date[] dates = calculateDateRange(range);
        List<VisitSourceVO> sources = statisticsService.getVisitSourceStatistics(dates[0], dates[1]);
        return ResultUtils.success(sources);
    }

    /**
     * 获取点赞趋势
     */
    @ApiOperation("获取点赞趋势")
    @GetMapping("/like/trend")
    public BaseResponse<List<LikeTrendVO>> getLikeTrendStatistics(
            @RequestParam(required = false) String range) {
        Date[] dates = calculateDateRange(range);
        List<LikeTrendVO> trend = statisticsService.getLikeTrendStatistics(dates[0], dates[1]);
        return ResultUtils.success(trend);
    }

    /**
     * 获取评论统计详情
     */
    @ApiOperation("获取评论统计详情")
    @GetMapping("/comment/analysis")
    public BaseResponse<CommentStatisticsVO> getCommentStatistics(
            @RequestParam(required = false) String range) {
        Date[] dates = calculateDateRange(range);
        CommentStatisticsVO statistics = statisticsService.getCommentStatistics(dates[0], dates[1]);
        return ResultUtils.success(statistics);
    }

    /**
     * 计算日期范围
     * @param range 时间范围（7days, 30days, 90days）
     * @return [startDate, endDate]
     */
    private Date[] calculateDateRange(String range) {
        Calendar calendar = Calendar.getInstance();
        Date endDate = calendar.getTime();

        int days = 7; // 默认7天
        if (range != null) {
            if (range.equals("30days")) {
                days = 30;
            } else if (range.equals("90days")) {
                days = 90;
            }
        }

        calendar.add(Calendar.DAY_OF_MONTH, -days);
        Date startDate = calendar.getTime();

        return new Date[]{startDate, endDate};
    }
}