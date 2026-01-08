package com.youqi.usercenter.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youqi.usercenter.model.entity.Comment;
import com.youqi.usercenter.model.vo.*;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
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

    /**
     * 按日期范围统计评论趋势
     */
    List<CommentTrendVO> selectCommentTrend(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    /**
     * 统计今日评论数
     */
    Long selectTodayCommentCount();

    /**
     * 统计昨日评论数
     */
    Long selectYesterdayCommentCount();

    /**
     * 统计总评论数
     */
    Long selectTotalCommentCount();

    /**
     * 统计评论状态分布
     */
    List<CommentStatusDistributionVO> selectCommentStatusDistribution();

    /**
     * 统计评论小时分布
     */
    List<CommentHourDistributionVO> selectCommentHourDistribution(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    /**
     * 获取热门评论文章
     */
    List<HotArticleVO> selectHotArticlesByComments(@Param("limit") Integer limit);

    /**
     * 统计活跃用户数（近期有评论的用户）
     */
    Long selectActiveUserCount();

    /**
     * 统计昨日活跃用户数
     */
    Long selectYesterdayActiveUserCount();
}