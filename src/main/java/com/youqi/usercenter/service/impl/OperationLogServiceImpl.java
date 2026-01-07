package com.youqi.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youqi.usercenter.common.ErrorCode;
import com.youqi.usercenter.exception.BusinessException;
import com.youqi.usercenter.mapper.OperationLogMapper;
import com.youqi.usercenter.model.entity.OperationLog;
import com.youqi.usercenter.service.OperationLogService;
import org.springframework.stereotype.Service;

@Service
public class OperationLogServiceImpl extends ServiceImpl<OperationLogMapper, OperationLog> implements OperationLogService {

    @Override
    public boolean logOperation(Long userId, String action, String targetType, Long targetId, String ipAddress, String userAgent, String details) {
        if (userId == null || action == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        OperationLog operationLog = new OperationLog();
        operationLog.setUserId(userId);
        operationLog.setAction(action);
        operationLog.setTargetType(targetType);
        operationLog.setTargetId(targetId);
        operationLog.setIpAddress(ipAddress);
        operationLog.setUserAgent(userAgent);
        operationLog.setDetails(details);

        return this.save(operationLog);
    }

    @Override
    public Page<OperationLog> listOperationLogs(Long userId, String action, String targetType, Integer current, Integer size) {
        QueryWrapper<OperationLog> queryWrapper = getQueryWrapper(userId, action, targetType);
        queryWrapper.orderByDesc("created_at");
        Page<OperationLog> page = new Page<>(current, size);
        return this.page(page, queryWrapper);
    }

    @Override
    public QueryWrapper<OperationLog> getQueryWrapper(Long userId, String action, String targetType) {
        QueryWrapper<OperationLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(userId != null, "user_id", userId);
        queryWrapper.eq(action != null, "action", action);
        queryWrapper.eq(targetType != null, "target_type", targetType);
        return queryWrapper;
    }
}