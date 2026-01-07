package com.youqi.usercenter.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youqi.usercenter.common.BaseResponse;
import com.youqi.usercenter.common.ErrorCode;
import com.youqi.usercenter.common.ResultUtils;
import com.youqi.usercenter.exception.BusinessException;
import com.youqi.usercenter.model.entity.User;
import com.youqi.usercenter.model.entity.Visit;
import com.youqi.usercenter.service.VisitService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Api(tags = "访问记录管理")
@RestController
@RequestMapping("/visit")
public class VisitController {

    @Resource
    private VisitService visitService;

    @ApiOperation("记录访问")
    @PostMapping("/record")
    public BaseResponse<Boolean> recordVisit(@RequestBody Visit visit, HttpServletRequest request) {
        if (visit == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        User loginUser = getLoginUser(request);
        if (loginUser != null) {
            visit.setUserId(loginUser.getId());
        }

        visit.setIpAddress(getIpAddress(request));
        visit.setUserAgent(request.getHeader("User-Agent"));
        visit.setReferrer(request.getHeader("Referer"));
        visit.setSessionId(request.getSession().getId());

        boolean result = visitService.recordVisit(visit);
        return ResultUtils.success(result);
    }

    @ApiOperation("分页查询访问记录")
    @GetMapping("/list")
    public BaseResponse<Page<Visit>> listVisits(@RequestParam(required = false) String targetType,
                                                  @RequestParam(required = false) Long targetId,
                                                  @RequestParam(defaultValue = "1") Integer current,
                                                  @RequestParam(defaultValue = "10") Integer size) {
        Page<Visit> page = visitService.listVisits(targetType, targetId, current, size);
        return ResultUtils.success(page);
    }

    @ApiOperation("获取访问数")
    @GetMapping("/count")
    public BaseResponse<Long> getVisitCount(@RequestParam String targetType, @RequestParam Long targetId) {
        Long count = visitService.getVisitCount(targetType, targetId);
        return ResultUtils.success(count);
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