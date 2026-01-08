package com.youqi.usercenter.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 点赞趋势视图对象
 */
@Data
public class LikeTrendVO implements Serializable {

    /**
     * 日期
     */
    private String date;

    /**
     * 点赞数
     */
    private Long likes;

    private static final long serialVersionUID = 1L;
}