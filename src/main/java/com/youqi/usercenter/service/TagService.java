package com.youqi.usercenter.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.youqi.usercenter.model.dto.TagQueryRequest;
import com.youqi.usercenter.model.entity.Tag;

import java.util.List;

/**
 * 标签服务接口
 */
public interface TagService extends IService<Tag> {

  /**
   * 分页查询标签列表
   */
  Page<Tag> listTags(TagQueryRequest queryRequest);

  /**
   * 获取热门标签（按文章数排序）
   */
  List<Tag> getHotTags(Integer limit);

  /**
   * 获取标签搜索建议
   */
  List<String> getTagSuggestions(String keyword);

  /**
   * 增加标签文章数
   */
  void incrementArticleCount(Long tagId);

  /**
   * 减少标签文章数
   */
  void decrementArticleCount(Long tagId);
}
