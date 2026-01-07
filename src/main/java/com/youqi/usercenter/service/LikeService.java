package com.youqi.usercenter.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.youqi.usercenter.model.entity.Like;

public interface LikeService extends IService<Like> {

    boolean like(Long userId, String targetType, Long targetId);

    boolean unlike(Long userId, String targetType, Long targetId);

    boolean isLiked(Long userId, String targetType, Long targetId);

    Long getLikeCount(String targetType, Long targetId);

    Page<Like> listLikes(Long userId, String targetType, Integer current, Integer size);

    QueryWrapper<Like> getQueryWrapper(Long userId, String targetType, Long targetId);
}