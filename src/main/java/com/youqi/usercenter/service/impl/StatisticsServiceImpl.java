package com.youqi.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youqi.usercenter.common.ErrorCode;
import com.youqi.usercenter.exception.BusinessException;
import com.youqi.usercenter.mapper.*;
import com.youqi.usercenter.model.dto.ArticleStatisticsQueryRequest;
import com.youqi.usercenter.model.dto.UserStatisticsQueryRequest;
import com.youqi.usercenter.model.entity.ArticleStatistics;
import com.youqi.usercenter.model.entity.UserStatistics;
import com.youqi.usercenter.model.vo.*;
import com.youqi.usercenter.service.StatisticsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 统计服务实现类
 */
@Service
public class StatisticsServiceImpl extends ServiceImpl<UserStatisticsMapper, UserStatistics>
        implements StatisticsService {

    @Resource
    private UserStatisticsMapper userStatisticsMapper;

    @Resource
    private ArticleStatisticsMapper articleStatisticsMapper;

    @Override
    @Transactional
    public boolean initUserStatistics(Long userId) {
        if (userId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 检查是否已经存在
        QueryWrapper<UserStatistics> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        UserStatistics existing = userStatisticsMapper.selectOne(queryWrapper);

        if (existing != null) {
            return true; // 已经存在，无需初始化
        }

        // 创建用户统计记录
        UserStatistics statistics = new UserStatistics();
        statistics.setUserId(userId);
        statistics.setArticleCount(0);
        statistics.setCommentCount(0);
        statistics.setLikeCount(0);
        statistics.setLikedCount(0);
        statistics.setTotalViewCount(0);
        statistics.setCreateTime(new Date());
        statistics.setUpdateTime(new Date());

        return userStatisticsMapper.insert(statistics) > 0;
    }

    @Override
    @Transactional
    public boolean updateUserArticleCount(Long userId, int delta) {
        if (userId == null) {
            return false;
        }

        UserStatistics statistics = getUserStatistics(userId);
        if (statistics == null) {
            boolean initSuccess = initUserStatistics(userId);
            if (!initSuccess) {
                return false;
            }
            statistics = getUserStatistics(userId);
            if (statistics == null) {
                return false;
            }
        }

        int newCount = Math.max(0, statistics.getArticleCount() + delta);
        statistics.setArticleCount(newCount);
        statistics.setUpdateTime(new Date());

        return userStatisticsMapper.updateById(statistics) > 0;
    }

    @Override
    @Transactional
    public boolean updateUserCommentCount(Long userId, int delta) {
        if (userId == null) {
            return false;
        }

        UserStatistics statistics = getUserStatistics(userId);
        if (statistics == null) {
            boolean initSuccess = initUserStatistics(userId);
            if (!initSuccess) {
                return false;
            }
            statistics = getUserStatistics(userId);
            if (statistics == null) {
                return false;
            }
        }

        int newCount = Math.max(0, statistics.getCommentCount() + delta);
        statistics.setCommentCount(newCount);
        statistics.setUpdateTime(new Date());

        return userStatisticsMapper.updateById(statistics) > 0;
    }

    @Override
    @Transactional
    public boolean updateUserLikeCount(Long userId, int delta) {
        if (userId == null) {
            return false;
        }

        UserStatistics statistics = getUserStatistics(userId);
        if (statistics == null) {
            boolean initSuccess = initUserStatistics(userId);
            if (!initSuccess) {
                return false;
            }
            statistics = getUserStatistics(userId);
            if (statistics == null) {
                return false;
            }
        }

        int newCount = Math.max(0, statistics.getLikeCount() + delta);
        statistics.setLikeCount(newCount);
        statistics.setUpdateTime(new Date());

        return userStatisticsMapper.updateById(statistics) > 0;
    }

    @Override
    @Transactional
    public boolean updateUserLikedCount(Long userId, int delta) {
        if (userId == null) {
            return false;
        }

        UserStatistics statistics = getUserStatistics(userId);
        if (statistics == null) {
            boolean initSuccess = initUserStatistics(userId);
            if (!initSuccess) {
                return false;
            }
            statistics = getUserStatistics(userId);
            if (statistics == null) {
                return false;
            }
        }

        int newCount = Math.max(0, statistics.getLikedCount() + delta);
        statistics.setLikedCount(newCount);
        statistics.setUpdateTime(new Date());

        return userStatisticsMapper.updateById(statistics) > 0;
    }

    @Override
    @Transactional
    public boolean updateUserViewCount(Long userId, int delta) {
        if (userId == null) {
            return false;
        }

        UserStatistics statistics = getUserStatistics(userId);
        if (statistics == null) {
            boolean initSuccess = initUserStatistics(userId);
            if (!initSuccess) {
                return false;
            }
            statistics = getUserStatistics(userId);
            if (statistics == null) {
                return false;
            }
        }

        int newCount = Math.max(0, statistics.getTotalViewCount() + delta);
        statistics.setTotalViewCount(newCount);
        statistics.setUpdateTime(new Date());

        return userStatisticsMapper.updateById(statistics) > 0;
    }

    @Override
    public UserStatistics getUserStatistics(Long userId) {
        if (userId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        QueryWrapper<UserStatistics> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        return userStatisticsMapper.selectOne(queryWrapper);
    }

    @Override
    public Page<UserStatisticsVO> listUserStatistics(UserStatisticsQueryRequest queryRequest) {
        if (queryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        QueryWrapper<UserStatistics> queryWrapper = getUserStatisticsQueryWrapper(queryRequest);
        Page<UserStatistics> page = new Page<>(queryRequest.getCurrent(), queryRequest.getPageSize());

        Page<UserStatistics> statisticsPage = userStatisticsMapper.selectPage(page, queryWrapper);

        // 转换为VO对象
        Page<UserStatisticsVO> voPage = new Page<>();
        BeanUtils.copyProperties(statisticsPage, voPage, "records");

        List<UserStatisticsVO> voList = statisticsPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    @Transactional
    public boolean initArticleStatistics(Long articleId) {
        if (articleId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 检查是否已经存在
        QueryWrapper<ArticleStatistics> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("article_id", articleId);
        ArticleStatistics existing = articleStatisticsMapper.selectOne(queryWrapper);

        if (existing != null) {
            return true; // 已经存在，无需初始化
        }

        // 创建文章统计记录
        ArticleStatistics statistics = new ArticleStatistics();
        statistics.setArticleId(articleId);
        statistics.setViewCount(0);
        statistics.setLikeCount(0);
        statistics.setCommentCount(0);
        statistics.setShareCount(0);
        statistics.setCreateTime(new Date());
        statistics.setUpdateTime(new Date());

        return articleStatisticsMapper.insert(statistics) > 0;
    }

    @Override
    @Transactional
    public boolean updateArticleViewCount(Long articleId, int delta) {
        if (articleId == null) {
            return false;
        }

        ArticleStatistics statistics = getArticleStatistics(articleId);
        if (statistics == null) {
            boolean initSuccess = initArticleStatistics(articleId);
            if (!initSuccess) {
                return false;
            }
            statistics = getArticleStatistics(articleId);
            if (statistics == null) {
                return false;
            }
        }

        int newCount = Math.max(0, statistics.getViewCount() + delta);
        statistics.setViewCount(newCount);
        statistics.setUpdateTime(new Date());

        return articleStatisticsMapper.updateById(statistics) > 0;
    }

    @Override
    @Transactional
    public boolean updateArticleLikeCount(Long articleId, int delta) {
        if (articleId == null) {
            return false;
        }

        ArticleStatistics statistics = getArticleStatistics(articleId);
        if (statistics == null) {
            boolean initSuccess = initArticleStatistics(articleId);
            if (!initSuccess) {
                return false;
            }
            statistics = getArticleStatistics(articleId);
            if (statistics == null) {
                return false;
            }
        }

        int newCount = Math.max(0, statistics.getLikeCount() + delta);
        statistics.setLikeCount(newCount);
        statistics.setUpdateTime(new Date());

        return articleStatisticsMapper.updateById(statistics) > 0;
    }

    @Override
    @Transactional
    public boolean updateArticleCommentCount(Long articleId, int delta) {
        if (articleId == null) {
            return false;
        }

        ArticleStatistics statistics = getArticleStatistics(articleId);
        if (statistics == null) {
            boolean initSuccess = initArticleStatistics(articleId);
            if (!initSuccess) {
                return false;
            }
            statistics = getArticleStatistics(articleId);
            if (statistics == null) {
                return false;
            }
        }

        int newCount = Math.max(0, statistics.getCommentCount() + delta);
        statistics.setCommentCount(newCount);
        statistics.setUpdateTime(new Date());

        return articleStatisticsMapper.updateById(statistics) > 0;
    }

    @Override
    @Transactional
    public boolean updateArticleShareCount(Long articleId, int delta) {
        if (articleId == null) {
            return false;
        }

        ArticleStatistics statistics = getArticleStatistics(articleId);
        if (statistics == null) {
            boolean initSuccess = initArticleStatistics(articleId);
            if (!initSuccess) {
                return false;
            }
            statistics = getArticleStatistics(articleId);
            if (statistics == null) {
                return false;
            }
        }

        int newCount = Math.max(0, statistics.getShareCount() + delta);
        statistics.setShareCount(newCount);
        statistics.setUpdateTime(new Date());

        return articleStatisticsMapper.updateById(statistics) > 0;
    }

    @Override
    public ArticleStatistics getArticleStatistics(Long articleId) {
        if (articleId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        QueryWrapper<ArticleStatistics> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("article_id", articleId);
        return articleStatisticsMapper.selectOne(queryWrapper);
    }

    @Override
    public Page<ArticleStatisticsVO> listArticleStatistics(ArticleStatisticsQueryRequest queryRequest) {
        if (queryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        QueryWrapper<ArticleStatistics> queryWrapper = getArticleStatisticsQueryWrapper(queryRequest);
        Page<ArticleStatistics> page = new Page<>(queryRequest.getCurrent(), queryRequest.getPageSize());

        Page<ArticleStatistics> statisticsPage = articleStatisticsMapper.selectPage(page, queryWrapper);

        // 转换为VO对象
        Page<ArticleStatisticsVO> voPage = new Page<>();
        BeanUtils.copyProperties(statisticsPage, voPage, "records");

        List<ArticleStatisticsVO> voList = statisticsPage.getRecords().stream()
                .map(this::convertToArticleVO)
                .collect(Collectors.toList());

        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public QueryWrapper<UserStatistics> getUserStatisticsQueryWrapper(UserStatisticsQueryRequest queryRequest) {
        QueryWrapper<UserStatistics> queryWrapper = new QueryWrapper<>();

        if (queryRequest == null) {
            return queryWrapper;
        }

        Long userId = queryRequest.getUserId();
        String sortField = queryRequest.getSortField();
        String sortOrder = queryRequest.getSortOrder();

        // 精确查询用户
        queryWrapper.eq(userId != null, "user_id", userId);

        // 排序
        if (StringUtils.isNotBlank(sortField)) {
            if ("asc".equalsIgnoreCase(sortOrder)) {
                queryWrapper.orderByAsc(sortField);
            } else {
                queryWrapper.orderByDesc(sortField);
            }
        } else {
            // 默认按更新时间倒序
            queryWrapper.orderByDesc("update_time");
        }

        return queryWrapper;
    }

    @Override
    public QueryWrapper<ArticleStatistics> getArticleStatisticsQueryWrapper(
            ArticleStatisticsQueryRequest queryRequest) {
        QueryWrapper<ArticleStatistics> queryWrapper = new QueryWrapper<>();

        if (queryRequest == null) {
            return queryWrapper;
        }

        Long articleId = queryRequest.getArticleId();
        String sortField = queryRequest.getSortField();
        String sortOrder = queryRequest.getSortOrder();

        // 精确查询文章
        queryWrapper.eq(articleId != null, "article_id", articleId);

        // 排序
        if (StringUtils.isNotBlank(sortField)) {
            if ("asc".equalsIgnoreCase(sortOrder)) {
                queryWrapper.orderByAsc(sortField);
            } else {
                queryWrapper.orderByDesc(sortField);
            }
        } else {
            // 默认按访问量倒序
            queryWrapper.orderByDesc("view_count");
        }

        return queryWrapper;
    }

    /**
     * 转换为用户统计VO对象
     */
    private UserStatisticsVO convertToVO(UserStatistics statistics) {
        UserStatisticsVO vo = new UserStatisticsVO();
        BeanUtils.copyProperties(statistics, vo);
        return vo;
    }

    /**
     * 转换为文章统计VO对象
     */
    private ArticleStatisticsVO convertToArticleVO(ArticleStatistics statistics) {
        ArticleStatisticsVO vo = new ArticleStatisticsVO();
        BeanUtils.copyProperties(statistics, vo);
        return vo;
    }

    // ========== 综合统计方法实现 ==========

    @Resource
    private VisitMapper visitMapper;

    @Resource
    private LikeMapper likeMapper;

    @Resource
    private CommentMapper commentMapper;

    @Resource
    private ArticleMapper articleMapper;

    @Override
    public OverviewStatisticsVO getOverviewStatistics() {
        OverviewStatisticsVO overview = new OverviewStatisticsVO();

        // 获取今日访问量
        Long todayVisits = visitMapper.selectTodayVisitCount();
        overview.setTodayVisits(todayVisits != null ? todayVisits : 0L);

        // 获取昨日访问量，计算增长率
        Long yesterdayVisits = visitMapper.selectYesterdayVisitCount();
        if (yesterdayVisits != null && yesterdayVisits > 0) {
            double growth = ((todayVisits - yesterdayVisits) * 100.0) / yesterdayVisits;
            overview.setVisitsGrowth(Math.round(growth * 100.0) / 100.0);
        } else {
            overview.setVisitsGrowth(0.0);
        }

        // 获取总点赞数
        Long totalLikes = likeMapper.selectTotalLikeCount();
        overview.setTotalLikes(totalLikes != null ? totalLikes : 0L);

        // 获取昨日点赞数，计算增长率
        Long yesterdayLikes = likeMapper.selectYesterdayLikeCount();
        Long todayLikes = likeMapper.selectTodayLikeCount();
        if (yesterdayLikes != null && yesterdayLikes > 0) {
            double growth = ((todayLikes - yesterdayLikes) * 100.0) / yesterdayLikes;
            overview.setLikesGrowth(Math.round(growth * 100.0) / 100.0);
        } else {
            overview.setLikesGrowth(0.0);
        }

        // 获取总评论数
        Long totalComments = commentMapper.selectTotalCommentCount();
        overview.setTotalComments(totalComments != null ? totalComments : 0L);

        // 获取昨日评论数，计算增长率
        Long yesterdayComments = commentMapper.selectYesterdayCommentCount();
        Long todayComments = commentMapper.selectTodayCommentCount();
        if (yesterdayComments != null && yesterdayComments > 0) {
            double growth = ((todayComments - yesterdayComments) * 100.0) / yesterdayComments;
            overview.setCommentsGrowth(Math.round(growth * 100.0) / 100.0);
        } else {
            overview.setCommentsGrowth(0.0);
        }

        // 获取活跃用户数
        Long activeUsers = articleMapper.selectActiveUserCount();
        overview.setActiveUsers(activeUsers != null ? activeUsers : 0L);

        // 获取昨日活跃用户数，计算增长率
        Long yesterdayActiveUsers = articleMapper.selectYesterdayActiveUserCount();
        if (yesterdayActiveUsers != null && yesterdayActiveUsers > 0) {
            double growth = ((activeUsers - yesterdayActiveUsers) * 100.0) / yesterdayActiveUsers;
            overview.setUsersGrowth(Math.round(growth * 100.0) / 100.0);
        } else {
            overview.setUsersGrowth(0.0);
        }

        // 获取文章总数
        Long articleCount = articleMapper.selectTotalArticleCount();
        overview.setArticleCount(articleCount != null ? articleCount : 0L);

        // 获取总访问量
        Long totalViewCount = articleMapper.selectTotalViewCount();
        overview.setTotalViewCount(totalViewCount != null ? totalViewCount : 0L);

        // 获取用户总数
        Long userCount = articleMapper.selectTotalUserCount();
        overview.setUserCount(userCount != null ? userCount : 0L);

        return overview;
    }

    @Override
    public List<VisitTrendVO> getVisitTrendStatistics(Date startDate, Date endDate) {
        List<VisitTrendVO> trendList = visitMapper.selectVisitTrend(startDate, endDate);
        return trendList != null ? trendList : new ArrayList<>();
    }

    @Override
    public List<VisitSourceVO> getVisitSourceStatistics(Date startDate, Date endDate) {
        List<VisitSourceVO> sourceList = visitMapper.selectVisitSources(startDate, endDate);
        return sourceList != null ? sourceList : new ArrayList<>();
    }

    @Override
    public List<LikeTrendVO> getLikeTrendStatistics(Date startDate, Date endDate) {
        List<LikeTrendVO> trendList = likeMapper.selectLikeTrend(startDate, endDate);
        return trendList != null ? trendList : new ArrayList<>();
    }

    @Override
    public CommentStatisticsVO getCommentStatistics(Date startDate, Date endDate) {
        CommentStatisticsVO statistics = new CommentStatisticsVO();

        // 获取评论总数
        Long totalComments = commentMapper.selectTotalCommentCount();
        statistics.setTotalComments(totalComments != null ? totalComments : 0L);

        // 计算日均评论数（假设统计最近30天）
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -30);
        Date thirtyDaysAgo = calendar.getTime();
        long days = 30;
        double dailyAverage = totalComments != null ? (double) totalComments / days : 0.0;
        statistics.setDailyAverage(Math.round(dailyAverage * 100.0) / 100.0);

        // 计算评论参与率（评论数 / 访问量）
        Long totalViewCount = articleMapper.selectTotalViewCount();
        if (totalViewCount != null && totalViewCount > 0) {
            double participationRate = (totalComments * 100.0) / totalViewCount;
            statistics.setParticipationRate(Math.round(participationRate * 100.0) / 100.0);
        } else {
            statistics.setParticipationRate(0.0);
        }

        // 计算平均回复数（假设每条评论平均有0.5个回复）
        double avgReplies = 0.5;
        statistics.setAvgReplies(avgReplies);

        // 计算热度指数（综合评论数、点赞数、访问量）
        Long totalLikes = likeMapper.selectTotalLikeCount();
        double heatIndex = 0.0;
        if (totalComments != null || totalLikes != null || totalViewCount != null) {
            heatIndex = (totalComments != null ? totalComments : 0) * 0.4 +
                    (totalLikes != null ? totalLikes : 0) * 0.3 +
                    (totalViewCount != null ? totalViewCount : 0) * 0.3;
            heatIndex = heatIndex / 100.0;
        }
        statistics.setHeatIndex(Math.round(heatIndex * 100.0) / 100.0);

        // 获取评论状态分布
        List<CommentStatusDistributionVO> distribution = commentMapper.selectCommentStatusDistribution();
        if (distribution != null && !distribution.isEmpty()) {
            long total = distribution.stream().mapToLong(CommentStatusDistributionVO::getValue).sum();
            for (CommentStatusDistributionVO item : distribution) {
                double percentage = total > 0 ? (item.getValue() * 100.0) / total : 0.0;
                item.setPercentage(Math.round(percentage * 100.0) / 100.0);
            }
        }
        statistics.setDistribution(distribution != null ? distribution : new ArrayList<>());

        // 获取评论小时分布
        List<CommentHourDistributionVO> hourDistribution = commentMapper.selectCommentHourDistribution(startDate,
                endDate);
        statistics.setHourDistribution(hourDistribution != null ? hourDistribution : new ArrayList<>());

        // 获取热门评论文章
        List<HotArticleVO> topArticles = commentMapper.selectHotArticlesByComments(5);
        statistics.setTopArticles(topArticles != null ? topArticles : new ArrayList<>());

        return statistics;
    }
}