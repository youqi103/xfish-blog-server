package com.youqi.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youqi.usercenter.common.ErrorCode;
import com.youqi.usercenter.exception.BusinessException;
import com.youqi.usercenter.mapper.CategoryMapper;
import com.youqi.usercenter.model.dto.CategoryQueryRequest;
import com.youqi.usercenter.model.entity.Category;
import com.youqi.usercenter.service.CategoryService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 分类服务实现类
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

  @Override
  public Page<Category> listCategories(CategoryQueryRequest queryRequest) {
    if (queryRequest == null) {
      throw new BusinessException(ErrorCode.PARAMS_ERROR);
    }

    QueryWrapper<Category> queryWrapper = new QueryWrapper<>();

    // 模糊查询分类名称
    String name = queryRequest.getName();
    queryWrapper.like(StringUtils.isNotBlank(name), "name", name);

    // 按排序字段排序
    queryWrapper.orderByAsc("sort_order");
    queryWrapper.orderByDesc("created_at");

    Page<Category> page = new Page<>(queryRequest.getCurrent(), queryRequest.getPageSize());
    return this.page(page, queryWrapper);
  }

  @Override
  public java.util.List<Category> getAllCategories() {
    QueryWrapper<Category> queryWrapper = new QueryWrapper<>();
    queryWrapper.orderByAsc("sort_order");
    queryWrapper.orderByDesc("created_at");
    return this.list(queryWrapper);
  }
}
