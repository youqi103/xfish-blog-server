package com.youqi.usercenter.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 访问趋势视图对象
 */
@Data
public class VisitTrendVO implements Serializable {

    /**
     * 日期
     */
    private String date;

    /**
     * 访问量
     */
    private Long visits;

    private static final long serialVersionUID = 1L;
}