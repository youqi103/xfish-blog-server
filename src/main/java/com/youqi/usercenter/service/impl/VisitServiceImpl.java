package com.youqi.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youqi.usercenter.common.ErrorCode;
import com.youqi.usercenter.exception.BusinessException;
import com.youqi.usercenter.mapper.ArticleMapper;
import com.youqi.usercenter.mapper.VisitMapper;
import com.youqi.usercenter.model.entity.Article;
import com.youqi.usercenter.model.entity.Visit;
import com.youqi.usercenter.service.VisitService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
public class VisitServiceImpl extends ServiceImpl<VisitMapper, Visit> implements VisitService {

    @Resource
    private ArticleMapper articleMapper;

    @Override
    @Transactional
    public boolean recordVisit(Visit visit) {
        if (visit == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        if (visit.getTargetType() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "目标类型不能为空");
        }

        if (!"article".equals(visit.getTargetType()) && !"page".equals(visit.getTargetType())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "目标类型不正确");
        }

        boolean result = this.save(visit);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "记录访问失败");
        }

        if ("article".equals(visit.getTargetType()) && visit.getTargetId() != null) {
            Article article = articleMapper.selectById(visit.getTargetId());
            if (article != null) {
                article.setViewCount(article.getViewCount() + 1);
                articleMapper.updateById(article);
            }
        }

        return true;
    }

    @Override
    public Page<Visit> listVisits(String targetType, Long targetId, Integer current, Integer size) {
        QueryWrapper<Visit> queryWrapper = getQueryWrapper(targetType, targetId);
        queryWrapper.orderByDesc("created_at");
        Page<Visit> page = new Page<>(current, size);
        return this.page(page, queryWrapper);
    }

    @Override
    public Long getVisitCount(String targetType, Long targetId) {
        QueryWrapper<Visit> queryWrapper = getQueryWrapper(targetType, targetId);
        return this.count(queryWrapper);
    }

    @Override
    public QueryWrapper<Visit> getQueryWrapper(String targetType, Long targetId) {
        QueryWrapper<Visit> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(targetType != null, "target_type", targetType);
        queryWrapper.eq(targetId != null, "target_id", targetId);
        return queryWrapper;
    }
}