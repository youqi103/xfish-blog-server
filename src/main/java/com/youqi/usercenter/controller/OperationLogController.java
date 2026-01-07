package com.youqi.usercenter.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youqi.usercenter.common.BaseResponse;
import com.youqi.usercenter.common.ErrorCode;
import com.youqi.usercenter.common.ResultUtils;
import com.youqi.usercenter.exception.BusinessException;
import com.youqi.usercenter.model.entity.OperationLog;
import com.youqi.usercenter.model.entity.User;
import com.youqi.usercenter.service.OperationLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Api(tags = "操作日志管理")
@RestController
@RequestMapping("/operation-log")
public class OperationLogController {

    @Resource
    private OperationLogService operationLogService;

    @ApiOperation("记录操作日志")
    @PostMapping("/record")
    public BaseResponse<Boolean> logOperation(@RequestParam String action,
                                                @RequestParam(required = false) String targetType,
                                                @RequestParam(required = false) Long targetId,
                                                @RequestParam(required = false) String details,
                                                HttpServletRequest request) {
        User loginUser = getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }

        String ipAddress = getIpAddress(request);
        String userAgent = request.getHeader("User-Agent");

        boolean result = operationLogService.logOperation(loginUser.getId(), action, targetType, targetId, ipAddress, userAgent, details);
        return ResultUtils.success(result);
    }

    @ApiOperation("分页查询操作日志")
    @GetMapping("/list")
    public BaseResponse<Page<OperationLog>> listOperationLogs(@RequestParam(required = false) Long userId,
                                                                 @RequestParam(required = false) String action,
                                                                 @RequestParam(required = false) String targetType,
                                                                 @RequestParam(defaultValue = "1") Integer current,
                                                                 @RequestParam(defaultValue = "10") Integer size,
                                                                 HttpServletRequest request) {
//        User loginUser = getLoginUser(request);
//        if (loginUser == null) {
//            throw new BusinessException(ErrorCode.NOT_LOGIN);
//        }

        if (userId == null) {
//            userId = loginUser.getId();
            userId = 1L; // 临时使用固定用户ID进行测试
        }

        Page<OperationLog> page = operationLogService.listOperationLogs(userId, action, targetType, current, size);
        return ResultUtils.success(page);
    }

    private User getLoginUser(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        return user;
    }

    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}