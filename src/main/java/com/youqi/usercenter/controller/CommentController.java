package com.youqi.usercenter.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youqi.usercenter.common.BaseResponse;
import com.youqi.usercenter.common.ErrorCode;
import com.youqi.usercenter.common.ResultUtils;
import com.youqi.usercenter.exception.BusinessException;
import com.youqi.usercenter.model.dto.CommentAuditRequest;
import com.youqi.usercenter.model.dto.CommentQueryRequest;
import com.youqi.usercenter.model.dto.CommentRequest;
import com.youqi.usercenter.model.entity.Comment;
import com.youqi.usercenter.model.entity.User;
import com.youqi.usercenter.model.vo.CommentVO;
import com.youqi.usercenter.service.CommentService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 评论控制器
 */
@RestController
@RequestMapping("/comment")
public class CommentController {

    @Resource
    private CommentService commentService;

    /**
     * 新增评论
     */
    @PostMapping("/add")
    public BaseResponse<Long> addComment(@RequestBody CommentRequest commentRequest, HttpServletRequest request) {
        // 暂时使用固定用户ID进行测试
        Long userId = 1L;

        Long commentId = commentService.addComment(commentRequest, userId);
        return ResultUtils.success(commentId);
    }

    /**
     * 修改评论
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateComment(@RequestParam Long id, @RequestBody CommentRequest commentRequest, HttpServletRequest request) {
        // 暂时使用固定用户ID进行测试
        Long userId = 1L;

        boolean result = commentService.updateComment(id, commentRequest, userId);
        return ResultUtils.success(result);
    }

    /**
     * 删除评论
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteComment(@RequestParam Long id, HttpServletRequest request) {
        // 暂时使用固定用户ID进行测试
        Long userId = 1L;

        boolean result = commentService.deleteComment(id, userId);
        return ResultUtils.success(result);
    }

    /**
     * 审核评论
     */
    @PostMapping("/audit")
    public BaseResponse<Boolean> auditComment(@RequestBody CommentAuditRequest auditRequest, HttpServletRequest request) {
        // 暂时使用固定用户ID进行测试
        Long userId = 1L;

        boolean result = commentService.auditComment(auditRequest, userId);
        return ResultUtils.success(result);
    }

    /**
     * 批量审核评论
     */
    @PostMapping("/batchAudit")
    public BaseResponse<Boolean> batchAuditComments(@RequestParam("commentIds") List<Long> commentIds,
                                                 @RequestParam("auditStatus") Integer auditStatus,
                                                 HttpServletRequest request) {
        // 暂时使用固定用户ID进行测试
        Long userId = 1L;

        boolean result = commentService.batchAuditComments(commentIds, auditStatus, userId);
        return ResultUtils.success(result);
    }

    /**
     * 获取评论详情
     */
    @GetMapping("/get")
    public BaseResponse<Comment> getCommentDetail(@RequestParam Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        Comment comment = commentService.getCommentDetail(id);
        return ResultUtils.success(comment);
    }

    /**
     * 分页查询评论列表
     */
    @GetMapping("/list")
    public BaseResponse<Page<CommentVO>> listComments(CommentQueryRequest queryRequest) {
        if (queryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        Page<CommentVO> commentPage = commentService.listComments(queryRequest);
        return ResultUtils.success(commentPage);
    }

    /**
     * 获取文章的评论树
     */
    @GetMapping("/tree")
   public BaseResponse<List<Comment>> getCommentTree(@RequestParam Long articleId) {
       if (articleId == null || articleId <= 0) {
           throw new BusinessException(ErrorCode.PARAMS_ERROR);
       }

        List<CommentVO> commentTree = commentService.getCommentTree(articleId);
return ResultUtils.success((List<Comment>) (List<?>) commentTree);
   }

    /**
     * 点赞评论
     */
    @PostMapping("/like")
    public BaseResponse<Boolean> likeComment(@RequestParam Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        commentService.incrementLikeCount(id);
        return ResultUtils.success(true);
    }

    /**
     * 取消点赞评论
     */
    @PostMapping("/unlike")
    public BaseResponse<Boolean> unlikeComment(@RequestParam Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        commentService.decrementLikeCount(id);
        return ResultUtils.success(true);
    }

    /**
     * 更新评论状态
     */
    @PostMapping("/status")
    public BaseResponse<Boolean> updateCommentStatus(@RequestBody CommentAuditRequest auditRequest, HttpServletRequest request) {
        // 暂时使用固定用户ID进行测试
        Long userId = 1L;

        boolean result = commentService.updateCommentStatus(auditRequest, userId);
        return ResultUtils.success(result);
    }



    /**
     * 处理举报
     */
    @PutMapping("/report")
    public BaseResponse<Boolean> handleReport(@RequestBody CommentAuditRequest auditRequest, HttpServletRequest request) {
        // 暂时使用固定用户ID进行测试
        Long userId = 1L;

        boolean result = commentService.handleReport(auditRequest, userId);
        return ResultUtils.success(result);
    }

    /**
     * 回复评论
     */
    @PostMapping("/reply")
    public BaseResponse<Long> replyComment(@RequestBody CommentRequest commentRequest, HttpServletRequest request) {
        // 暂时使用固定用户ID进行测试
        Long userId = 1L;

        Long commentId = commentService.replyComment(commentRequest, userId);
        return ResultUtils.success(commentId);
    }

    /**
     * 获取当前登录用户
     */
    private User getLoginUser(HttpServletRequest request) {
        // 这里应该从session或token中获取当前登录用户
        // 暂时返回null，需要集成认证系统
        return null;
    }
}