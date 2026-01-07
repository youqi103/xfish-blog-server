package com.youqi.usercenter.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.youqi.usercenter.model.dto.CommentAuditRequest;
import com.youqi.usercenter.model.dto.CommentQueryRequest;
import com.youqi.usercenter.model.dto.CommentRequest;
import com.youqi.usercenter.model.entity.Comment;
import com.youqi.usercenter.model.vo.CommentVO;

import java.util.List;

/**
 * 评论服务接口
 */
public interface CommentService extends IService<Comment> {

    /**
     * 新增评论
     */
    Long addComment(CommentRequest commentRequest, Long userId);

    /**
     * 修改评论
     */
    boolean updateComment(Long id, CommentRequest commentRequest, Long userId);

    /**
     * 删除评论
     */
    boolean deleteComment(Long id, Long userId);

    /**
     * 更新评论状态
     */
    boolean updateCommentStatus(CommentAuditRequest auditRequest, Long auditorId);

    /**
     * 审核评论
     */
    boolean auditComment(CommentAuditRequest auditRequest, Long auditorId);

    /**
     * 批量审核评论
     */
    boolean batchAuditComments(List<Long> commentIds, Integer auditStatus, Long auditorId);

    /**
     * 处理举报
     */
    boolean handleReport(CommentAuditRequest auditRequest, Long auditorId);

    /**
     * 回复评论
     */
    Long replyComment(CommentRequest commentRequest, Long userId);

    /**
     * 获取评论详情
     */
    Comment getCommentDetail(Long id);

    /**
     * 分页查询评论列表
     */
    Page<CommentVO> listComments(CommentQueryRequest queryRequest);

    /**
     * 获取文章的评论树
     */
    List<CommentVO> getCommentTree(Long articleId);

    /**
     * 增加点赞数
     */
    void incrementLikeCount(Long commentId);

    /**
     * 减少点赞数
     */
    void decrementLikeCount(Long commentId);

    /**
     * 增加回复数
     */
    void incrementReplyCount(Long commentId);

    /**
     * 减少回复数
     */
    void decrementReplyCount(Long commentId);

    /**
     * 构建查询条件
     */
    QueryWrapper<Comment> getQueryWrapper(CommentQueryRequest queryRequest);
}