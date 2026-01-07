package com.youqi.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youqi.usercenter.common.ErrorCode;
import com.youqi.usercenter.exception.BusinessException;
import com.youqi.usercenter.mapper.CommentMapper;
import com.youqi.usercenter.model.dto.CommentAuditRequest;
import com.youqi.usercenter.model.dto.CommentQueryRequest;
import com.youqi.usercenter.model.dto.CommentRequest;
import com.youqi.usercenter.model.entity.Comment;
import com.youqi.usercenter.model.vo.CommentVO;
import com.youqi.usercenter.service.ArticleService;
import com.youqi.usercenter.service.CommentService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 评论服务实现类
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    @Resource
    private ArticleService articleService;

    @Override
    @Transactional
    public Long addComment(CommentRequest commentRequest, Long userId) {
        // 参数校验
        if (commentRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (commentRequest.getArticleId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文章ID不能为空");
        }
        if (StringUtils.isBlank(commentRequest.getContent())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "评论内容不能为空");
        }
        if (StringUtils.isBlank(commentRequest.getUserNickname())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "昵称不能为空");
        }
        if (StringUtils.isBlank(commentRequest.getUserEmail())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "邮箱不能为空");
        }

        // 检查文章是否存在
        if (articleService.getById(commentRequest.getArticleId()) == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "文章不存在");
        }

        // 创建评论
        Comment comment = new Comment();
        BeanUtils.copyProperties(commentRequest, comment);
        comment.setUserId(userId);
        comment.setStatus("pending"); // 默认待审核
        comment.setCreatedAt(new Date());
        comment.setUpdatedAt(new Date());

        boolean result = this.save(comment);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "评论创建失败");
        }

        // 更新文章的评论数
        articleService.incrementCommentCount(commentRequest.getArticleId());

        // 如果是回复评论，更新父评论的回复数
        if (commentRequest.getParentId() != null && commentRequest.getParentId() > 0) {
            incrementReplyCount(commentRequest.getParentId());
        }

        return comment.getId();
    }

    @Override
    @Transactional
    public boolean updateComment(Long id, CommentRequest commentRequest, Long userId) {
        // 参数校验
        if (id == null || commentRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 检查评论是否存在
        Comment comment = this.getById(id);
        if (comment == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "评论不存在");
        }

        // 检查权限（只能修改自己的评论）
        if (!comment.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.NO_AUTH, "无权限修改该评论");
        }

        // 更新评论
        BeanUtils.copyProperties(commentRequest, comment);
        comment.setUpdatedAt(new Date());

        return this.updateById(comment);
    }

    @Override
    @Transactional
    public boolean deleteComment(Long id, Long userId) {
        // 参数校验
        if (id == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 检查评论是否存在
        Comment comment = this.getById(id);
        if (comment == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "评论不存在");
        }

        // 检查权限（只能删除自己的评论，管理员可以删除所有评论）
        if (!comment.getUserId().equals(userId)) {
            // TODO: 这里需要添加管理员权限判断
            throw new BusinessException(ErrorCode.NO_AUTH, "无权限删除该评论");
        }

        // 删除评论（逻辑删除）
        boolean result = this.removeById(id);
        if (result) {
            // 更新文章的评论数
            articleService.decrementCommentCount(comment.getArticleId());

            // 如果是回复评论，更新父评论的回复数
            if (comment.getParentId() != null && comment.getParentId() > 0) {
                decrementReplyCount(comment.getParentId());
            }
        }

        return result;
    }

    @Override
    @Transactional
    public boolean updateCommentStatus(CommentAuditRequest auditRequest, Long auditorId) {
        // 参数校验
        if (auditRequest == null || auditRequest.getCommentId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (auditRequest.getAuditStatus() == null ||
                (auditRequest.getAuditStatus() != 1 && auditRequest.getAuditStatus() != 2)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "审核状态不正确");
        }

        // 检查评论是否存在
        Comment comment = this.getById(auditRequest.getCommentId());
        if (comment == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "评论不存在");
        }

        // 更新审核状态：1-已通过(approved), 2-已拒绝(rejected)
        String status = auditRequest.getAuditStatus() == 1 ? "approved" : "rejected";
        comment.setStatus(status);
        comment.setUpdatedAt(new Date());

        return this.updateById(comment);
    }

    @Override
    @Transactional
    public boolean auditComment(CommentAuditRequest auditRequest, Long auditorId) {
        // 参数校验
        if (auditRequest == null || auditRequest.getCommentId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (auditRequest.getAuditStatus() == null ||
                (auditRequest.getAuditStatus() != 1 && auditRequest.getAuditStatus() != 2)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "审核状态不正确");
        }

        // 检查评论是否存在
        Comment comment = this.getById(auditRequest.getCommentId());
        if (comment == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "评论不存在");
        }

        // 更新审核状态：1-已通过(approved), 2-已拒绝(rejected)
        String status = auditRequest.getAuditStatus() == 1 ? "approved" : "rejected";
        comment.setStatus(status);
        comment.setUpdatedAt(new Date());

        return this.updateById(comment);
    }

    @Override
    @Transactional
    public boolean handleReport(CommentAuditRequest auditRequest, Long auditorId) {
        // 参数校验
        if (auditRequest == null || auditRequest.getCommentId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 检查评论是否存在
        Comment comment = this.getById(auditRequest.getCommentId());
        if (comment == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "评论不存在");
        }

        // 根据处理类型执行不同操作
        Integer handleType = auditRequest.getAuditStatus();
        if (handleType == 2) {
            // 删除评论
            comment.setStatus("spam"); // 标记为垃圾评论
        } else if (handleType == 3) {
            // 警告用户，保持评论状态
            // 可以记录警告信息到操作日志
        }
        // handleType == 1 表示忽略举报，不做任何操作

        comment.setUpdatedAt(new Date());
        return this.updateById(comment);
    }

    @Override
    @Transactional
    public Long replyComment(CommentRequest commentRequest, Long userId) {
        // 参数校验
        if (commentRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (commentRequest.getArticleId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文章ID不能为空");
        }
        if (commentRequest.getParentId() == null || commentRequest.getParentId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "父评论ID不能为空");
        }
        if (StringUtils.isBlank(commentRequest.getContent())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "评论内容不能为空");
        }

        // 检查文章是否存在
        if (articleService.getById(commentRequest.getArticleId()) == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "文章不存在");
        }

        // 检查父评论是否存在
        Comment parentComment = this.getById(commentRequest.getParentId());
        if (parentComment == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "父评论不存在");
        }

        // 创建回复评论
        Comment comment = new Comment();
        BeanUtils.copyProperties(commentRequest, comment);
        comment.setUserId(userId);
        comment.setStatus("approved"); // 管理员回复直接通过审核
        comment.setCreatedAt(new Date());
        comment.setUpdatedAt(new Date());

        boolean result = this.save(comment);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "回复创建失败");
        }

        // 更新文章的评论数
        articleService.incrementCommentCount(commentRequest.getArticleId());

        // 更新父评论的回复数
        incrementReplyCount(commentRequest.getParentId());

        return comment.getId();
    }

    @Override
    @Transactional
    public boolean batchAuditComments(List<Long> commentIds, Integer auditStatus, Long auditorId) {
        // 参数校验
        if (commentIds == null || commentIds.isEmpty() || auditStatus == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (auditStatus != 1 && auditStatus != 2) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "审核状态不正确");
        }

        // 批量更新审核状态：1-已通过(approved), 2-已拒绝(rejected)
        String status = auditStatus == 1 ? "approved" : "rejected";
        List<Comment> comments = this.listByIds(commentIds);
        if (comments.isEmpty()) {
            return true;
        }

        Date now = new Date();
        for (Comment comment : comments) {
            comment.setStatus(status);
            comment.setUpdatedAt(now);
        }

        return this.updateBatchById(comments);
    }

    @Override
    public Comment getCommentDetail(Long id) {
        if (id == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        Comment comment = this.getById(id);
        if (comment == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "评论不存在");
        }

        return comment;
    }

    @Override
    public Page<CommentVO> listComments(CommentQueryRequest queryRequest) {
        if (queryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 创建分页对象
        Page<CommentVO> page = new Page<>(queryRequest.getCurrent(), queryRequest.getPageSize());

        // 使用自定义SQL查询
        Page<CommentVO> resultPage = (Page<CommentVO>) baseMapper.selectCommentVOPage(
                page,
                queryRequest.getArticleId(),
                queryRequest.getParentId(),
                queryRequest.getStatus(),
                queryRequest.getUserId()
        );

        return resultPage;
    }

    @Override
    public List<CommentVO> getCommentTree(Long articleId) {
        if (articleId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 查询所有已审核的评论
        List<CommentVO> allComments = baseMapper.selectCommentVOByArticleId(articleId);

        // 构建评论树
        Map<Long, List<CommentVO>> parentIdToComments = allComments.stream()
                .collect(Collectors.groupingBy(comment -> comment.getParentId() != null ? comment.getParentId() : 0L));

        // 获取顶级评论
        List<CommentVO> topLevelComments = parentIdToComments.getOrDefault(0L, new ArrayList<>());

        // 递归构建评论树
        for (CommentVO comment : topLevelComments) {
            buildCommentTree(comment, parentIdToComments);
        }

        return topLevelComments;
    }

    /**
     * 递归构建评论树
     */
    private void buildCommentTree(CommentVO comment, Map<Long, List<CommentVO>> parentIdToComments) {
        List<CommentVO> children = parentIdToComments.get(comment.getId());
        if (children != null && !children.isEmpty()) {
            comment.setChildren(children);
            for (CommentVO child : children) {
                buildCommentTree(child, parentIdToComments);
            }
        }
    }

    @Override
    public void incrementLikeCount(Long commentId) {
        // Comment实体类没有likeCount字段，此方法暂时不做处理
        // 可以通过关联的Like表来统计点赞数
    }

    @Override
    public void decrementLikeCount(Long commentId) {
        // Comment实体类没有likeCount字段，此方法暂时不做处理
        // 可以通过关联的Like表来统计点赞数
    }

    @Override
    public void incrementReplyCount(Long commentId) {
        // Comment实体类没有replyCount字段，此方法暂时不做处理
        // 可以通过查询parent_id来统计回复数
    }

    @Override
    public void decrementReplyCount(Long commentId) {
        // Comment实体类没有replyCount字段，此方法暂时不做处理
        // 可以通过查询parent_id来统计回复数
    }

    @Override
    public QueryWrapper<Comment> getQueryWrapper(CommentQueryRequest queryRequest) {
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();

        if (queryRequest == null) {
            return queryWrapper;
        }

        Long articleId = queryRequest.getArticleId();
        Long parentId = queryRequest.getParentId();
        String status = queryRequest.getStatus();
        Long userId = queryRequest.getUserId();

        // 精确查询文章
        queryWrapper.eq(articleId != null, "article_id", articleId);
        // 精确查询父评论
        queryWrapper.eq(parentId != null, "parent_id", parentId);
        // 精确查询状态
        queryWrapper.eq(StringUtils.isNotBlank(status), "status", status);
        // 精确查询用户
        queryWrapper.eq(userId != null, "user_id", userId);

        // 排序：按创建时间倒序
        queryWrapper.orderByDesc("created_at");

        return queryWrapper;
    }
}