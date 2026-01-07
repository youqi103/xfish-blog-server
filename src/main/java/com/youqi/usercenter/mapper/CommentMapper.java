package com.youqi.usercenter.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youqi.usercenter.model.entity.Comment;
import com.youqi.usercenter.model.vo.CommentVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 评论Mapper
 */
public interface CommentMapper extends BaseMapper<Comment> {

    /**
     * 分页查询评论列表（包含关联信息）
     */
    IPage<CommentVO> selectCommentVOPage(Page<CommentVO> page, @Param("articleId") Long articleId,
                                       @Param("parentId") Long parentId, @Param("status") String status,
                                       @Param("userId") Long userId);

    /**
     * 查询文章的所有评论（包含关联信息）
     */
    @Select("SELECT c.*, a.title as article_title, u.nickname as user_name " +
            "FROM comments c " +
            "LEFT JOIN articles a ON c.article_id = a.id " +
            "LEFT JOIN users u ON c.user_id = u.id " +
            "WHERE c.article_id = #{articleId} AND c.status = '1' " +
            "ORDER BY c.created_at DESC")
    List<CommentVO> selectCommentVOByArticleId(@Param("articleId") Long articleId);
}