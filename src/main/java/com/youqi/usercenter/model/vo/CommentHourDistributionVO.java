package com.youqi.usercenter.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 评论小时分布视图对象
 */
@Data
public class CommentHourDistributionVO implements Serializable {

    /**
     * 小时（0-23）
     */
    private Integer hour;

    /**
     * 评论数
     */
    private Long count;

    private static final long serialVersionUID = 1L;
}