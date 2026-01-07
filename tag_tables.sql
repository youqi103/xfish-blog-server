-- 标签表
CREATE TABLE tags (
    id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    name VARCHAR(50) NOT NULL UNIQUE COMMENT '标签名称',
    article_count INT UNSIGNED DEFAULT 0 COMMENT '文章数量',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_name (name),
    INDEX idx_article_count (article_count)
) ENGINE=InnoDB COMMENT='标签表';

-- 文章标签关联表（多对多关系）
CREATE TABLE article_tags (
    id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    article_id BIGINT UNSIGNED NOT NULL COMMENT '文章ID',
    tag_id BIGINT UNSIGNED NOT NULL COMMENT '标签ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_article_tag (article_id, tag_id) COMMENT '文章标签唯一索引',
    INDEX idx_article_id (article_id) COMMENT '文章ID索引',
    INDEX idx_tag_id (tag_id) COMMENT '标签ID索引',
    FOREIGN KEY (article_id) REFERENCES articles(id) ON DELETE CASCADE COMMENT '文章外键',
    FOREIGN KEY (tag_id) REFERENCES tags(id) ON DELETE CASCADE COMMENT '标签外键'
) ENGINE=InnoDB COMMENT='文章标签关联表';
