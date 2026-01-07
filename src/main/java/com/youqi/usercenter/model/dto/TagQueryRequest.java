package com.youqi.usercenter.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 标签查询请求体
 */
@Data
public class TagQueryRequest implements Serializable {

  /**
   * 标签名称（模糊查询）
   */
  private String name;

  /**
   * 当前页码
   */
  private Integer current = 1;

  /**
   * 每页条数
   */
  private Integer pageSize = 10;

  private static final long serialVersionUID = 1L;
}
