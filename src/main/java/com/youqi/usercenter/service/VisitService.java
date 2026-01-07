package com.youqi.usercenter.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.youqi.usercenter.model.entity.Visit;

public interface VisitService extends IService<Visit> {

    boolean recordVisit(Visit visit);

    Page<Visit> listVisits(String targetType, Long targetId, Integer current, Integer size);

    Long getVisitCount(String targetType, Long targetId);

    QueryWrapper<Visit> getQueryWrapper(String targetType, Long targetId);
}