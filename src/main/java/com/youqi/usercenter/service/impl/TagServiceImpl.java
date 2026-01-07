package com.youqi.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youqi.usercenter.common.ErrorCode;
import com.youqi.usercenter.exception.BusinessException;
import com.youqi.usercenter.mapper.TagMapper;
import com.youqi.usercenter.model.dto.TagQueryRequest;
import com.youqi.usercenter.model.entity.Tag;
import com.youqi.usercenter.service.TagService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 标签服务实现类
 */
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {

  @Override
  public Page<Tag> listTags(TagQueryRequest queryRequest) {
    if (queryRequest == null) {
      throw new BusinessException(ErrorCode.PARAMS_ERROR);
    }

    QueryWrapper<Tag> queryWrapper = new QueryWrapper<>();

    // 模糊查询标签名称
    String name = queryRequest.getName();
    queryWrapper.like(StringUtils.isNotBlank(name), "name", name);

    // 按文章数降序排序
    queryWrapper.orderByDesc("article_count");
    queryWrapper.orderByDesc("created_at");

    Page<Tag> page = new Page<>(queryRequest.getCurrent(), queryRequest.getPageSize());
    return this.page(page, queryWrapper);
  }

  @Override
  public List<Tag> getHotTags(Integer limit) {
    if (limit == null || limit <= 0) {
      limit = 10;
    }

    QueryWrapper<Tag> queryWrapper = new QueryWrapper<>();
    queryWrapper.orderByDesc("article_count");
    queryWrapper.last("LIMIT " + limit);

    return this.list(queryWrapper);
  }

  @Override
  public List<String> getTagSuggestions(String keyword) {
    if (StringUtils.isBlank(keyword)) {
      return new ArrayList<>();
    }

    QueryWrapper<Tag> queryWrapper = new QueryWrapper<>();
    queryWrapper.like("name", keyword);
    queryWrapper.orderByDesc("article_count");
    queryWrapper.last("LIMIT 10");

    List<Tag> tags = this.list(queryWrapper);
    return tags.stream().map(Tag::getName).collect(Collectors.toList());
  }

  @Override
  public void incrementArticleCount(Long tagId) {
    if (tagId == null) {
      return;
    }

    Tag tag = this.getById(tagId);
    if (tag != null) {
      tag.setArticleCount(tag.getArticleCount() + 1);
      this.updateById(tag);
    }
  }

  @Override
  public void decrementArticleCount(Long tagId) {
    if (tagId == null) {
      return;
    }

    Tag tag = this.getById(tagId);
    if (tag != null && tag.getArticleCount() > 0) {
      tag.setArticleCount(tag.getArticleCount() - 1);
      this.updateById(tag);
    }
  }
}
