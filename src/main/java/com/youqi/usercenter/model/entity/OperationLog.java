package com.youqi.usercenter.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

@Data
@TableName(value = "operation_logs")
public class OperationLog {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String action;

    private String targetType;

    private Long targetId;

    private String ipAddress;

    private String userAgent;

    private String details;

    @TableField(fill = FieldFill.INSERT)
    private Date createdAt;
}