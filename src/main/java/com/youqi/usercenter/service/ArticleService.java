package com.youqi.usercenter.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.youqi.usercenter.model.dto.ArticleQueryRequest;
import com.youqi.usercenter.model.dto.ArticleRequest;
import com.youqi.usercenter.model.entity.Article;

/**
 * 文章服务接口
 */
public interface ArticleService extends IService<Article> {

    /**
     * 新增文章
     */
    Long addArticle(ArticleRequest articleRequest, Long authorId);

    /**
     * 修改文章
     */
    boolean updateArticle(Long id, ArticleRequest articleRequest, Long authorId);

    /**
     * 删除文章
     */
    boolean deleteArticle(Long id, Long authorId);

    /**
     * 发布文章
     */
    boolean publishArticle(Long id, Long authorId);

    /**
     * 下线文章
     */
    boolean offlineArticle(Long id, Long authorId);

    /**
     * 获取文章详情
     */
    Article getArticleDetail(Long id);

    /**
     * 分页查询文章列表
     */
    Page<Article> listArticles(ArticleQueryRequest queryRequest);

    /**
     * 增加浏览量
     */
    void incrementViewCount(Long articleId);

    /**
     * 增加点赞数
     */
    void incrementLikeCount(Long articleId);

    /**
     * 减少点赞数
     */
    void decrementLikeCount(Long articleId);

    /**
     * 增加评论数
     */
    void incrementCommentCount(Long articleId);

    /**
     * 减少评论数
     */
    void decrementCommentCount(Long articleId);

    /**
     * 构建查询条件
     */
    QueryWrapper<Article> getQueryWrapper(ArticleQueryRequest queryRequest);
}