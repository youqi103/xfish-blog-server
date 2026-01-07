package com.youqi.usercenter.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youqi.usercenter.model.entity.Like;
import com.youqi.usercenter.model.vo.HotArticleVO;
import com.youqi.usercenter.model.vo.LikeTrendVO;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface LikeMapper extends BaseMapper<Like> {

    /**
     * 按日期范围统计点赞趋势
     */
    List<LikeTrendVO> selectLikeTrend(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    /**
     * 统计今日点赞数
     */
    Long selectTodayLikeCount();

    /**
     * 统计昨日点赞数
     */
    Long selectYesterdayLikeCount();

    /**
     * 统计总点赞数
     */
    Long selectTotalLikeCount();

    /**
     * 获取热门文章（按点赞数排序）
     */
    List<HotArticleVO> selectHotArticlesByLikes(@Param("limit") Integer limit);
}