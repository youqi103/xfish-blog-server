package com.youqi.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youqi.usercenter.common.ErrorCode;
import com.youqi.usercenter.exception.BusinessException;
import com.youqi.usercenter.mapper.ArticleMapper;
import com.youqi.usercenter.mapper.ArticleStatisticsMapper;
import com.youqi.usercenter.model.dto.ArticleQueryRequest;
import com.youqi.usercenter.model.dto.ArticleRequest;
import com.youqi.usercenter.model.entity.Article;
import com.youqi.usercenter.model.entity.ArticleStatistics;
import com.youqi.usercenter.service.ArticleService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 文章服务实现类
 */
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

    @Resource
    private ArticleStatisticsMapper articleStatisticsMapper;

    @Override
    @Transactional
    public Long addArticle(ArticleRequest articleRequest, Long authorId) {
        // 参数校验
        if (articleRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (StringUtils.isBlank(articleRequest.getTitle())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文章标题不能为空");
        }
        if (StringUtils.isBlank(articleRequest.getContent())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文章内容不能为空");
        }

        // 创建文章
        Article article = new Article();
        BeanUtils.copyProperties(articleRequest, article);
        article.setAuthorId(authorId);
        // 使用请求中的状态，如果没有则默认为草稿
        article.setStatus(articleRequest.getStatus() != null ? articleRequest.getStatus() : "0");
        article.setViewCount(0L);
        article.setLikeCount(0L);
        article.setCommentCount(0L);
        article.setCreatedAt(new Date());
        article.setUpdatedAt(new Date());

        // 转换标签格式：将逗号分隔的字符串转换为 JSON 数组
        if (StringUtils.isNotBlank(articleRequest.getTags())) {
            String[] tagArray = articleRequest.getTags().split(",");
            String jsonTags = convertTagsToJson(tagArray);
            article.setTags(jsonTags);
        }

        boolean result = this.save(article);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "文章创建失败");
        }

        // 创建文章统计记录
        ArticleStatistics statistics = new ArticleStatistics();
        statistics.setArticleId(article.getId());
        statistics.setViewCount(0);
        statistics.setLikeCount(0);
        statistics.setCommentCount(0);
        statistics.setShareCount(0);
        statistics.setCreateTime(new Date());
        statistics.setUpdateTime(new Date());
        articleStatisticsMapper.insert(statistics);

        return article.getId();
    }

    @Override
    @Transactional
    public boolean updateArticle(Long id, ArticleRequest articleRequest, Long authorId) {
        // 参数校验
        if (id == null || articleRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 检查文章是否存在
        Article article = this.getById(id);
        if (article == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "文章不存在");
        }

        // 检查权限（只能修改自己的文章）
        if (!article.getAuthorId().equals(authorId)) {
            throw new BusinessException(ErrorCode.NO_AUTH, "无权限修改该文章");
        }

        // 更新文章字段
        if (StringUtils.isNotBlank(articleRequest.getTitle())) {
            article.setTitle(articleRequest.getTitle());
        }
        if (StringUtils.isNotBlank(articleRequest.getContent())) {
            article.setContent(articleRequest.getContent());
        }
        if (StringUtils.isNotBlank(articleRequest.getSummary())) {
            article.setSummary(articleRequest.getSummary());
        }
        if (StringUtils.isNotBlank(articleRequest.getCoverImage())) {
            article.setCoverImage(articleRequest.getCoverImage());
        }
        if (articleRequest.getCategoryId() != null) {
            article.setCategoryId(articleRequest.getCategoryId());
        }
        if (StringUtils.isNotBlank(articleRequest.getTags())) {
            String[] tagArray = articleRequest.getTags().split(",");
            String jsonTags = convertTagsToJson(tagArray);
            article.setTags(jsonTags);
        }
        if (articleRequest.getStatus() != null) {
            article.setStatus(articleRequest.getStatus());
        }
        article.setUpdatedAt(new Date());

        return this.updateById(article);
    }

    @Override
    @Transactional
    public boolean deleteArticle(Long id, Long authorId) {
        // 参数校验
        if (id == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 检查文章是否存在
        Article article = this.getById(id);
        if (article == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "文章不存在");
        }

        // 检查权限（只能删除自己的文章，管理员可以删除所有文章）
        if (!article.getAuthorId().equals(authorId)) {
            // TODO: 这里需要添加管理员权限判断
            throw new BusinessException(ErrorCode.NO_AUTH, "无权限删除该文章");
        }

        // 删除文章（逻辑删除）
        boolean result = this.removeById(id);
        if (result) {
            // 删除对应的统计记录
            QueryWrapper<ArticleStatistics> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("article_id", id);
            articleStatisticsMapper.delete(queryWrapper);
        }

        return result;
    }

    @Override
    @Transactional
    public boolean publishArticle(Long id, Long authorId) {
        // 参数校验
        if (id == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 检查文章是否存在
        Article article = this.getById(id);
        if (article == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "文章不存在");
        }

        // 检查权限
        if (!article.getAuthorId().equals(authorId)) {
            throw new BusinessException(ErrorCode.NO_AUTH, "无权限发布该文章");
        }

        // 发布文章
        article.setStatus("1"); // 已发布
        article.setPublishedAt(new Date());
        article.setUpdatedAt(new Date());

        return this.updateById(article);
    }

    @Override
    @Transactional
    public boolean offlineArticle(Long id, Long authorId) {
        // 参数校验
        if (id == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 检查文章是否存在
        Article article = this.getById(id);
        if (article == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "文章不存在");
        }

        // 检查权限
        if (!article.getAuthorId().equals(authorId)) {
            throw new BusinessException(ErrorCode.NO_AUTH, "无权限下线该文章");
        }

        // 下线文章
        article.setStatus("2"); // 已下线
        article.setUpdatedAt(new Date());

        return this.updateById(article);
    }

    @Override
    public Article getArticleDetail(Long id) {
        if (id == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        Article article = this.getById(id);
        if (article == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "文章不存在");
        }

        return article;
    }

    @Override
    public Page<Article> listArticles(ArticleQueryRequest queryRequest) {
        if (queryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        QueryWrapper<Article> queryWrapper = this.getQueryWrapper(queryRequest);
        Page<Article> page = new Page<>(queryRequest.getCurrent(), queryRequest.getPageSize());

        return this.page(page, queryWrapper);
    }

    @Override
    public void incrementViewCount(Long articleId) {
        if (articleId == null) {
            return;
        }

        Article article = this.getById(articleId);
        if (article != null) {
            article.setViewCount(article.getViewCount() + 1);
            this.updateById(article);

            // 更新统计表
            QueryWrapper<ArticleStatistics> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("article_id", articleId);
            ArticleStatistics statistics = articleStatisticsMapper.selectOne(queryWrapper);
            if (statistics != null) {
                statistics.setViewCount(statistics.getViewCount() + 1);
                statistics.setUpdateTime(new Date());
                articleStatisticsMapper.updateById(statistics);
            }
        }
    }

    @Override
    public void incrementLikeCount(Long articleId) {
        if (articleId == null) {
            return;
        }

        Article article = this.getById(articleId);
        if (article != null) {
            article.setLikeCount(article.getLikeCount() + 1);
            this.updateById(article);

            // 更新统计表
            QueryWrapper<ArticleStatistics> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("article_id", articleId);
            ArticleStatistics statistics = articleStatisticsMapper.selectOne(queryWrapper);
            if (statistics != null) {
                statistics.setLikeCount(statistics.getLikeCount() + 1);
                statistics.setUpdateTime(new Date());
                articleStatisticsMapper.updateById(statistics);
            }
        }
    }

    @Override
    public void decrementLikeCount(Long articleId) {
        if (articleId == null) {
            return;
        }

        Article article = this.getById(articleId);
        if (article != null && article.getLikeCount() > 0) {
            article.setLikeCount(article.getLikeCount() - 1);
            this.updateById(article);

            // 更新统计表
            QueryWrapper<ArticleStatistics> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("article_id", articleId);
            ArticleStatistics statistics = articleStatisticsMapper.selectOne(queryWrapper);
            if (statistics != null && statistics.getLikeCount() > 0) {
                statistics.setLikeCount(statistics.getLikeCount() - 1);
                statistics.setUpdateTime(new Date());
                articleStatisticsMapper.updateById(statistics);
            }
        }
    }

    @Override
    public void incrementCommentCount(Long articleId) {
        if (articleId == null) {
            return;
        }

        Article article = this.getById(articleId);
        if (article != null) {
            article.setCommentCount(article.getCommentCount() + 1);
            this.updateById(article);

            // 更新统计表
            QueryWrapper<ArticleStatistics> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("article_id", articleId);
            ArticleStatistics statistics = articleStatisticsMapper.selectOne(queryWrapper);
            if (statistics != null) {
                statistics.setCommentCount(statistics.getCommentCount() + 1);
                statistics.setUpdateTime(new Date());
                articleStatisticsMapper.updateById(statistics);
            }
        }
    }

    @Override
    public void decrementCommentCount(Long articleId) {
        if (articleId == null) {
            return;
        }

        Article article = this.getById(articleId);
        if (article != null && article.getCommentCount() > 0) {
            article.setCommentCount(article.getCommentCount() - 1);
            this.updateById(article);

            // 更新统计表
            QueryWrapper<ArticleStatistics> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("article_id", articleId);
            ArticleStatistics statistics = articleStatisticsMapper.selectOne(queryWrapper);
            if (statistics != null && statistics.getCommentCount() > 0) {
                statistics.setCommentCount(statistics.getCommentCount() - 1);
                statistics.setUpdateTime(new Date());
                articleStatisticsMapper.updateById(statistics);
            }
        }
    }

    @Override
    public QueryWrapper<Article> getQueryWrapper(ArticleQueryRequest queryRequest) {
        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();

        if (queryRequest == null) {
            return queryWrapper;
        }

        String title = queryRequest.getTitle();
        Long authorId = queryRequest.getAuthorId();
        Long categoryId = queryRequest.getCategoryId();
        String categoryName = queryRequest.getCategoryName();
        String status = queryRequest.getStatus();
        String tags = queryRequest.getTags();
        String tagName = queryRequest.getTagName();

        // 模糊查询标题
        queryWrapper.like(StringUtils.isNotBlank(title), "title", title);
        // 精确查询作者
        queryWrapper.eq(authorId != null, "author_id", authorId);

        // 查询分类：优先使用categoryId，否则使用categoryName模糊查询
        if (categoryId != null) {
            queryWrapper.eq("category_id", categoryId);
        } else if (StringUtils.isNotBlank(categoryName)) {
            // 使用SQL的IN子查询，通过分类名称查找文章
            queryWrapper.apply("category_id IN (SELECT id FROM categories WHERE name LIKE CONCAT('%', {0}, '%'))",
                    categoryName);
        }

        // 精确查询状态
        queryWrapper.eq(status != null, "status", status);

        // 查询标签：优先使用tags，否则使用tagName模糊查询
        if (StringUtils.isNotBlank(tags)) {
            queryWrapper.like("tags", tags);
        } else if (StringUtils.isNotBlank(tagName)) {
            queryWrapper.like("tags", tagName);
        }

        // 排序：按创建时间倒序
        queryWrapper.orderByDesc("created_at");

        return queryWrapper;
    }

    /**
     * 将标签数组转换为 JSON 格式
     * 
     * @param tagArray 标签数组
     * @return JSON 格式的标签字符串
     */
    private String convertTagsToJson(String[] tagArray) {
        if (tagArray == null || tagArray.length == 0) {
            return null;
        }

        // 去除空白字符
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("[");
        for (int i = 0; i < tagArray.length; i++) {
            String tag = tagArray[i].trim();
            if (tag.isEmpty()) {
                continue;
            }
            if (jsonBuilder.length() > 1) {
                jsonBuilder.append(",");
            }
            jsonBuilder.append("\"").append(tag).append("\"");
        }
        jsonBuilder.append("]");

        String result = jsonBuilder.toString();
        // 如果结果只是 "[]"，返回 null
        if ("[]".equals(result)) {
            return null;
        }
        return result;
    }
}