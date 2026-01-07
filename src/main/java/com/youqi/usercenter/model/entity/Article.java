package com.youqi.usercenter.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

@Data
@TableName(value = "articles")
public class Article {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;

    private String content;

    private String summary;

    private String coverImage;

    private Long authorId;

    private Long categoryId;

    private String tags;

    private Long viewCount;

    private Long likeCount;

    private Long commentCount;

    private String status;

    private Integer isMarkdown;

    private Date publishedAt;

    @TableField(fill = FieldFill.INSERT)
    private Date createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updatedAt;
}