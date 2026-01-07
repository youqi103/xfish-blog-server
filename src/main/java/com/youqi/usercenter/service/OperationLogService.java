package com.youqi.usercenter.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.youqi.usercenter.model.entity.OperationLog;

public interface OperationLogService extends IService<OperationLog> {

    boolean logOperation(Long userId, String action, String targetType, Long targetId, String ipAddress, String userAgent, String details);

    Page<OperationLog> listOperationLogs(Long userId, String action, String targetType, Integer current, Integer size);

    QueryWrapper<OperationLog> getQueryWrapper(Long userId, String action, String targetType);
}