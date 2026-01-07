package com.youqi.usercenter.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youqi.usercenter.common.BaseResponse;
import com.youqi.usercenter.common.ErrorCode;
import com.youqi.usercenter.common.ResultUtils;
import com.youqi.usercenter.exception.BusinessException;
import com.youqi.usercenter.model.entity.Like;
import com.youqi.usercenter.model.entity.User;
import com.youqi.usercenter.service.LikeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Api(tags = "点赞管理")
@RestController
@RequestMapping("/like")
public class LikeController {

    @Resource
    private LikeService likeService;

    @ApiOperation("点赞")
    @PostMapping("/add")
    public BaseResponse<Boolean> like(@RequestParam String targetType, @RequestParam Long targetId, HttpServletRequest request) {
        User loginUser = getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        boolean result = likeService.like(loginUser.getId(), targetType, targetId);
        return ResultUtils.success(result);
    }

    @ApiOperation("取消点赞")
    @PostMapping("/cancel")
    public BaseResponse<Boolean> unlike(@RequestParam String targetType, @RequestParam Long targetId, HttpServletRequest request) {
        User loginUser = getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        boolean result = likeService.unlike(loginUser.getId(), targetType, targetId);
        return ResultUtils.success(result);
    }

    @ApiOperation("检查是否已点赞")
    @GetMapping("/check")
    public BaseResponse<Boolean> isLiked(@RequestParam String targetType, @RequestParam Long targetId, HttpServletRequest request) {
        User loginUser = getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        boolean result = likeService.isLiked(loginUser.getId(), targetType, targetId);
        return ResultUtils.success(result);
    }

    @ApiOperation("获取点赞数")
    @GetMapping("/count")
    public BaseResponse<Long> getLikeCount(@RequestParam String targetType, @RequestParam Long targetId) {
        Long count = likeService.getLikeCount(targetType, targetId);
        return ResultUtils.success(count);
    }

    @ApiOperation("分页查询点赞记录")
    @GetMapping("/list")
    public BaseResponse<Page<Like>> listLikes(@RequestParam(required = false) String targetType,
                                               @RequestParam(defaultValue = "1") Integer current,
                                               @RequestParam(defaultValue = "10") Integer size,
                                               HttpServletRequest request) {
        User loginUser = getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        Page<Like> page = likeService.listLikes(loginUser.getId(), targetType, current, size);
        return ResultUtils.success(page);
    }

    private User getLoginUser(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        return user;
    }
}