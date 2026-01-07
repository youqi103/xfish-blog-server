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
import com.youqi.usercenter.model.vo.ArticleStatisticsVO;
import com.youqi.usercenter.model.vo.UserStatisticsVO;
import com.youqi.usercenter.service.StatisticsService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 统计控制器
 */
@RestController
@RequestMapping("/statistics")
public class StatisticsController {

    @Resource
    private StatisticsService statisticsService;

    /**
     * 获取用户统计信息
     */
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
}