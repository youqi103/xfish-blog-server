package com.youqi.usercenter.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 访问来源视图对象
 */
@Data
public class VisitSourceVO implements Serializable {

    /**
     * 来源（直接访问、搜索引擎、外部链接）
     */
    private String source;

    /**
     * 访问次数
     */
    private Long count;

    private static final long serialVersionUID = 1L;
}