package com.youqi.usercenter.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youqi.usercenter.common.BaseResponse;
import com.youqi.usercenter.common.ErrorCode;
import com.youqi.usercenter.common.ResultUtils;
import com.youqi.usercenter.exception.BusinessException;
import com.youqi.usercenter.model.dto.CategoryQueryRequest;
import com.youqi.usercenter.model.entity.Category;
import com.youqi.usercenter.service.CategoryService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 分类控制器
 */
@RestController
@RequestMapping("/category")
public class CategoryController {

  @Resource
  private CategoryService categoryService;

  /**
   * 分页查询分类列表
   */
  @GetMapping("/list")
  public BaseResponse<Page<Category>> listCategories(CategoryQueryRequest queryRequest) {
    if (queryRequest == null) {
      throw new BusinessException(ErrorCode.PARAMS_ERROR);
    }

    Page<Category> categoryPage = categoryService.listCategories(queryRequest);
    return ResultUtils.success(categoryPage);
  }

  /**
   * 获取所有分类（用于下拉选择）
   */
  @GetMapping("/all")
  public BaseResponse<List<Category>> getAllCategories() {
    List<Category> categories = categoryService.getAllCategories();
    return ResultUtils.success(categories);
  }
}
