package com.youqi.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youqi.usercenter.common.ErrorCode;
import com.youqi.usercenter.exception.BusinessException;
import com.youqi.usercenter.mapper.ArticleMapper;
import com.youqi.usercenter.mapper.CommentMapper;
import com.youqi.usercenter.mapper.LikeMapper;
import com.youqi.usercenter.model.entity.Article;
import com.youqi.usercenter.model.entity.Like;
import com.youqi.usercenter.service.LikeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
public class LikeServiceImpl extends ServiceImpl<LikeMapper, Like> implements LikeService {

    @Resource
    private ArticleMapper articleMapper;

    @Resource
    private CommentMapper commentMapper;

    @Override
    @Transactional
    public boolean like(Long userId, String targetType, Long targetId) {
        if (userId == null || targetType == null || targetId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        if (!"article".equals(targetType) && !"comment".equals(targetType)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "目标类型不正确");
        }

        if (isLiked(userId, targetType, targetId)) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "已经点赞过了");
        }

        Like like = new Like();
        like.setUserId(userId);
        like.setTargetType(targetType);
        like.setTargetId(targetId);

        boolean result = this.save(like);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "点赞失败");
        }

        if ("article".equals(targetType)) {
            Article article = articleMapper.selectById(targetId);
            if (article != null) {
                article.setLikeCount(article.getLikeCount() + 1);
                articleMapper.updateById(article);
            }
        } else if ("comment".equals(targetType)) {
            // Comment实体类没有likeCount字段，点赞数通过Like表统计
            // 不需要更新Comment表的点赞数
        }

        return true;
    }

    @Override
    @Transactional
    public boolean unlike(Long userId, String targetType, Long targetId) {
        if (userId == null || targetType == null || targetId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        QueryWrapper<Like> queryWrapper = getQueryWrapper(userId, targetType, targetId);
        Like like = this.getOne(queryWrapper);
        if (like == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "未找到点赞记录");
        }

        boolean result = this.removeById(like.getId());
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "取消点赞失败");
        }

        if ("article".equals(targetType)) {
            Article article = articleMapper.selectById(targetId);
            if (article != null && article.getLikeCount() > 0) {
                article.setLikeCount(article.getLikeCount() - 1);
                articleMapper.updateById(article);
            }
        } else if ("comment".equals(targetType)) {
            // Comment实体类没有likeCount字段，点赞数通过Like表统计
            // 不需要更新Comment表的点赞数
        }

        return true;
    }

    @Override
    public boolean isLiked(Long userId, String targetType, Long targetId) {
        QueryWrapper<Like> queryWrapper = getQueryWrapper(userId, targetType, targetId);
        return this.count(queryWrapper) > 0;
    }

    @Override
    public Long getLikeCount(String targetType, Long targetId) {
        QueryWrapper<Like> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("target_type", targetType);
        queryWrapper.eq("target_id", targetId);
        return this.count(queryWrapper);
    }

    @Override
    public Page<Like> listLikes(Long userId, String targetType, Integer current, Integer size) {
        QueryWrapper<Like> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(userId != null, "user_id", userId);
        queryWrapper.eq(targetType != null, "target_type", targetType);
        queryWrapper.orderByDesc("created_at");
        Page<Like> page = new Page<>(current, size);
        return this.page(page, queryWrapper);
    }

    @Override
    public QueryWrapper<Like> getQueryWrapper(Long userId, String targetType, Long targetId) {
        QueryWrapper<Like> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("target_type", targetType);
        queryWrapper.eq("target_id", targetId);
        return queryWrapper;
    }
}