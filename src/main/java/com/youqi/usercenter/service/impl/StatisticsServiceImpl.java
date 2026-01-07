package com.youqi.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youqi.usercenter.common.ErrorCode;
import com.youqi.usercenter.exception.BusinessException;
import com.youqi.usercenter.mapper.ArticleStatisticsMapper;
import com.youqi.usercenter.mapper.UserStatisticsMapper;
import com.youqi.usercenter.model.dto.ArticleStatisticsQueryRequest;
import com.youqi.usercenter.model.dto.UserStatisticsQueryRequest;
import com.youqi.usercenter.model.entity.ArticleStatistics;
import com.youqi.usercenter.model.entity.UserStatistics;
import com.youqi.usercenter.model.vo.ArticleStatisticsVO;
import com.youqi.usercenter.model.vo.UserStatisticsVO;
import com.youqi.usercenter.service.StatisticsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
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
            initUserStatistics(userId);
            statistics = getUserStatistics(userId);
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
            initUserStatistics(userId);
            statistics = getUserStatistics(userId);
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
            initUserStatistics(userId);
            statistics = getUserStatistics(userId);
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
            initUserStatistics(userId);
            statistics = getUserStatistics(userId);
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
            initUserStatistics(userId);
            statistics = getUserStatistics(userId);
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
            initArticleStatistics(articleId);
            statistics = getArticleStatistics(articleId);
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
            initArticleStatistics(articleId);
            statistics = getArticleStatistics(articleId);
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
            initArticleStatistics(articleId);
            statistics = getArticleStatistics(articleId);
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
            initArticleStatistics(articleId);
            statistics = getArticleStatistics(articleId);
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
}