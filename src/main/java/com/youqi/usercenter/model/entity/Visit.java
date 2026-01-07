package com.youqi.usercenter.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

@Data
@TableName(value = "visits")
public class Visit {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String targetType;

    private Long targetId;

    private String ipAddress;

    private String userAgent;

    private String referrer;

    private String sessionId;

    @TableField(fill = FieldFill.INSERT)
    private Date createdAt;
}