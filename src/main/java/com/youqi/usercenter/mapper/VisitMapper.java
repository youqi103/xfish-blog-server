package com.youqi.usercenter.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youqi.usercenter.model.entity.Visit;
import com.youqi.usercenter.model.vo.VisitSourceVO;
import com.youqi.usercenter.model.vo.VisitTrendVO;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface VisitMapper extends BaseMapper<Visit> {

    /**
     * 按日期范围统计访问量趋势
     */
    List<VisitTrendVO> selectVisitTrend(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    /**
     * 统计访问来源分布
     */
    List<VisitSourceVO> selectVisitSources(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    /**
     * 统计今日访问量
     */
    Long selectTodayVisitCount();

    /**
     * 统计昨日访问量
     */
    Long selectYesterdayVisitCount();

    /**
     * 统计总访问量
     */
    Long selectTotalVisitCount();
}