package com.youqi.usercenter.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youqi.usercenter.common.BaseResponse;
import com.youqi.usercenter.common.ErrorCode;
import com.youqi.usercenter.common.ResultUtils;
import com.youqi.usercenter.exception.BusinessException;
import com.youqi.usercenter.model.dto.TagQueryRequest;
import com.youqi.usercenter.model.entity.Tag;
import com.youqi.usercenter.service.TagService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 标签控制器
 */
@RestController
@RequestMapping("/tag")
public class TagController {

  @Resource
  private TagService tagService;

  /**
   * 分页查询标签列表
   */
  @GetMapping("/list")
  public BaseResponse<Page<Tag>> listTags(TagQueryRequest queryRequest) {
    if (queryRequest == null) {
      throw new BusinessException(ErrorCode.PARAMS_ERROR);
    }

    Page<Tag> tagPage = tagService.listTags(queryRequest);
    return ResultUtils.success(tagPage);
  }

  /**
   * 获取热门标签（按文章数排序）
   */
  @GetMapping("/hot")
  public BaseResponse<List<Tag>> getHotTags(@RequestParam(defaultValue = "10") Integer limit) {
    List<Tag> tags = tagService.getHotTags(limit);
    return ResultUtils.success(tags);
  }

  /**
   * 获取标签搜索建议
   */
  @GetMapping("/suggest")
  public BaseResponse<List<String>> getTagSuggestions(@RequestParam String keyword) {
    if (keyword == null || keyword.trim().isEmpty()) {
      throw new BusinessException(ErrorCode.PARAMS_ERROR, "搜索关键词不能为空");
    }

    List<String> suggestions = tagService.getTagSuggestions(keyword.trim());
    return ResultUtils.success(suggestions);
  }
}
