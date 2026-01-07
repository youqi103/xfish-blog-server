package com.youqi.usercenter.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.youqi.usercenter.model.dto.CategoryQueryRequest;
import com.youqi.usercenter.model.entity.Category;

/**
 * 分类服务接口
 */
public interface CategoryService extends IService<Category> {

  /**
   * 分页查询分类列表
   */
  Page<Category> listCategories(CategoryQueryRequest queryRequest);

  /**
   * 获取所有分类（用于下拉选择）
   */
  java.util.List<Category> getAllCategories();
}
