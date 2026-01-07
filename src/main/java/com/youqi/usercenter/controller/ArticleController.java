package com.youqi.usercenter.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youqi.usercenter.common.BaseResponse;
import com.youqi.usercenter.common.ErrorCode;
import com.youqi.usercenter.common.ResultUtils;
import com.youqi.usercenter.contant.UserConstant;
import com.youqi.usercenter.exception.BusinessException;
import com.youqi.usercenter.model.dto.ArticleQueryRequest;
import com.youqi.usercenter.model.dto.ArticleRequest;
import com.youqi.usercenter.model.entity.Article;
import com.youqi.usercenter.model.entity.User;
import com.youqi.usercenter.service.ArticleService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 文章控制器
 */
@RestController
@RequestMapping("/article")
public class ArticleController {

    @Resource
    private ArticleService articleService;

    /**
     * 新增文章
     */
    @PostMapping("/add")
    public BaseResponse<Long> addArticle(@RequestBody ArticleRequest articleRequest, HttpServletRequest request) {
        // 获取当前登录用户
        User loginUser = getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }

        Long articleId = articleService.addArticle(articleRequest, loginUser.getId());
        return ResultUtils.success(articleId);
    }

    /**
     * 修改文章
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateArticle(@RequestParam Long id, @RequestBody ArticleRequest articleRequest, HttpServletRequest request) {
        // 获取当前登录用户
        User loginUser = getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }

        boolean result = articleService.updateArticle(id, articleRequest, loginUser.getId());
        return ResultUtils.success(result);
    }

    /**
     * 删除文章
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteArticle(@RequestParam Long id, HttpServletRequest request) {
        // 获取当前登录用户
        User loginUser = getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }

        boolean result = articleService.deleteArticle(id, loginUser.getId());
        return ResultUtils.success(result);
    }

    /**
     * 发布文章
     */
    @PostMapping("/publish")
    public BaseResponse<Boolean> publishArticle(@RequestParam Long id, HttpServletRequest request) {
        // 获取当前登录用户
        User loginUser = getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }

        boolean result = articleService.publishArticle(id, loginUser.getId());
        return ResultUtils.success(result);
    }

    /**
     * 下线文章
     */
    @PostMapping("/offline")
    public BaseResponse<Boolean> offlineArticle(@RequestParam Long id, HttpServletRequest request) {
        // 获取当前登录用户
        User loginUser = getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }

        boolean result = articleService.offlineArticle(id, loginUser.getId());
        return ResultUtils.success(result);
    }

    /**
     * 获取文章详情
     */
    @GetMapping("/get")
    public BaseResponse<Article> getArticleDetail(@RequestParam Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        Article article = articleService.getArticleDetail(id);
        
        // 增加浏览量
        articleService.incrementViewCount(id);
        
        return ResultUtils.success(article);
    }

    /**
     * 分页查询文章列表
     */
    @GetMapping("/list")
    public BaseResponse<Page<Article>> listArticles(ArticleQueryRequest queryRequest) {
        if (queryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        Page<Article> articlePage = articleService.listArticles(queryRequest);
        return ResultUtils.success(articlePage);
    }

    /**
     * 点赞文章
     */
    @PostMapping("/like")
    public BaseResponse<Boolean> likeArticle(@RequestParam Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        articleService.incrementLikeCount(id);
        return ResultUtils.success(true);
    }

    /**
     * 取消点赞文章
     */
    @PostMapping("/unlike")
    public BaseResponse<Boolean> unlikeArticle(@RequestParam Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        articleService.decrementLikeCount(id);
        return ResultUtils.success(true);
    }

    /**
     * 获取当前登录用户
     */
    private User getLoginUser(HttpServletRequest request) {
        // 从session中获取当前登录用户
        Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        if (userObj == null) {
            return null;
        }
        return (User) userObj;
    }
}