package com.youqi.usercenter.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 评论状态分布视图对象
 */
@Data
public class CommentStatusDistributionVO implements Serializable {

    /**
     * 状态名称
     */
    private String type;

    /**
     * 数量
     */
    private Long value;

    /**
     * 百分比
     */
    private Double percentage;

    private static final long serialVersionUID = 1L;
}