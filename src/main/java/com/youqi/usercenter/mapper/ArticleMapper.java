package com.youqi.usercenter.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youqi.usercenter.model.entity.Article;

/**
 * 文章Mapper
 */
public interface ArticleMapper extends BaseMapper<Article> {

    /**
     * 统计文章总数
     */
    Long selectTotalArticleCount();

    /**
     * 统计总访问量（从文章表汇总）
     */
    Long selectTotalViewCount();

    /**
     * 统计用户总数
     */
    Long selectTotalUserCount();

    /**
     * 统计活跃用户数（近期有活动的用户）
     */
    Long selectActiveUserCount();

    /**
     * 统计昨日活跃用户数
     */
    Long selectYesterdayActiveUserCount();

    /**
     * 统计今日新增用户数
     */
    Long selectTodayNewUserCount();

    /**
     * 统计昨日新增用户数
     */
    Long selectYesterdayNewUserCount();
}