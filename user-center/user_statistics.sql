CREATE TABLE article_statistics
(
    id            BIGINT UNSIGNED AUTO_INCREMENT NOT NULL COMMENT '主键ID',
    article_id    BIGINT UNSIGNED                NOT NULL COMMENT '文章ID',
    view_count    INT UNSIGNED DEFAULT 0         NULL COMMENT '访问量',
    like_count    INT UNSIGNED DEFAULT 0         NULL COMMENT '点赞数',
    comment_count INT UNSIGNED DEFAULT 0         NULL COMMENT '评论数',
    share_count   INT UNSIGNED DEFAULT 0         NULL COMMENT '分享数',
    create_time   timestamp    DEFAULT NOW()     NULL COMMENT '创建时间',
    update_time   timestamp    DEFAULT NOW()     NULL COMMENT '更新时间',
    CONSTRAINT `PRIMARY` PRIMARY KEY (id)
) COMMENT ='文章统计表';

CREATE TABLE articles
(
    id            BIGINT UNSIGNED AUTO_INCREMENT  NOT NULL COMMENT '主键ID',
    title         VARCHAR(200)                    NOT NULL COMMENT '文章标题',
    content       LONGTEXT                        NOT NULL COMMENT '文章内容(Markdown)',
    summary       VARCHAR(500)                    NULL COMMENT '文章摘要',
    cover_image   VARCHAR(255)                    NULL COMMENT '封面图URL',
    author_id     BIGINT UNSIGNED                 NOT NULL COMMENT '作者ID',
    category_id   BIGINT UNSIGNED                 NULL COMMENT '分类ID',
    tags          JSON                            NULL COMMENT '标签数组',
    view_count    BIGINT UNSIGNED DEFAULT 0       NULL COMMENT '浏览量',
    like_count    BIGINT UNSIGNED DEFAULT 0       NULL COMMENT '点赞数',
    comment_count BIGINT UNSIGNED DEFAULT 0       NULL COMMENT '评论数',
    status        ENUM            DEFAULT 'draft' NULL COMMENT '文章状态',
    is_markdown   TINYINT(1)      DEFAULT 1       NULL COMMENT '是否为Markdown格式',
    published_at  timestamp                       NULL COMMENT '发布时间',
    created_at    timestamp       DEFAULT NOW()   NULL COMMENT '创建时间',
    updated_at    timestamp       DEFAULT NOW()   NULL COMMENT '更新时间',
    CONSTRAINT `PRIMARY` PRIMARY KEY (id)
) COMMENT ='文章表';

CREATE TABLE categories
(
    id            BIGINT UNSIGNED AUTO_INCREMENT NOT NULL COMMENT '主键ID',
    name          VARCHAR(50)                    NOT NULL COMMENT '分类名称',
    `description` VARCHAR(255)                   NULL COMMENT '分类描述',
    sort_order    INT       DEFAULT 0            NULL COMMENT '排序',
    created_at    timestamp DEFAULT NOW()        NULL COMMENT '创建时间',
    updated_at    timestamp DEFAULT NOW()        NULL COMMENT '更新时间',
    CONSTRAINT `PRIMARY` PRIMARY KEY (id)
) COMMENT ='文章分类表';

CREATE TABLE comments
(
    id         BIGINT UNSIGNED AUTO_INCREMENT NOT NULL COMMENT '主键ID',
    article_id BIGINT UNSIGNED                NOT NULL COMMENT '所属文章ID',
    user_id    BIGINT UNSIGNED                NULL COMMENT '用户ID(匿名评论时为NULL)',
    parent_id  BIGINT UNSIGNED                NULL COMMENT '父评论ID(用于回复)',
    content    LONGTEXT                       NOT NULL COMMENT '评论内容',
    status     ENUM      DEFAULT 'pending'    NULL COMMENT '审核状态',
    ip_address VARCHAR(45)                    NULL COMMENT 'IP地址',
    user_agent LONGTEXT                       NULL COMMENT '用户代理',
    created_at timestamp DEFAULT NOW()        NULL COMMENT '创建时间',
    updated_at timestamp DEFAULT NOW()        NULL COMMENT '更新时间',
    CONSTRAINT `PRIMARY` PRIMARY KEY (id)
) COMMENT ='评论表';

CREATE TABLE likes
(
    id          BIGINT UNSIGNED AUTO_INCREMENT NOT NULL COMMENT '主键ID',
    user_id     BIGINT UNSIGNED                NOT NULL COMMENT '用户ID',
    target_type ENUM                           NOT NULL COMMENT '目标类型',
    target_id   BIGINT UNSIGNED                NOT NULL COMMENT '目标ID',
    created_at  timestamp DEFAULT NOW()        NULL COMMENT '创建时间',
    CONSTRAINT `PRIMARY` PRIMARY KEY (id)
) COMMENT ='点赞记录表';

CREATE TABLE operation_logs
(
    id          BIGINT UNSIGNED AUTO_INCREMENT NOT NULL COMMENT '主键ID',
    user_id     BIGINT UNSIGNED                NOT NULL COMMENT '操作用户ID',
    action      VARCHAR(50)                    NOT NULL COMMENT '操作类型',
    target_type VARCHAR(50)                    NULL COMMENT '目标类型',
    target_id   BIGINT UNSIGNED                NULL COMMENT '目标ID',
    ip_address  VARCHAR(45)                    NULL COMMENT 'IP地址',
    user_agent  LONGTEXT                       NULL COMMENT '用户代理',
    details     JSON                           NULL COMMENT '操作详情',
    created_at  timestamp DEFAULT NOW()        NULL COMMENT '操作时间',
    CONSTRAINT `PRIMARY` PRIMARY KEY (id)
) COMMENT ='操作日志表';

CREATE TABLE system_configs
(
    id            BIGINT UNSIGNED AUTO_INCREMENT NOT NULL COMMENT '主键ID',
    config_key    VARCHAR(100)                   NOT NULL COMMENT '配置键',
    config_value  LONGTEXT                       NULL COMMENT '配置值',
    `description` VARCHAR(255)                   NULL COMMENT '配置说明',
    created_at    timestamp DEFAULT NOW()        NULL COMMENT '创建时间',
    updated_at    timestamp DEFAULT NOW()        NULL COMMENT '更新时间',
    CONSTRAINT `PRIMARY` PRIMARY KEY (id)
) COMMENT ='系统配置表';

CREATE TABLE users
(
    id            BIGINT UNSIGNED AUTO_INCREMENT NOT NULL COMMENT '主键ID',
    username      VARCHAR(50)                    NOT NULL COMMENT '用户名',
    email         VARCHAR(100)                   NOT NULL COMMENT '邮箱',
    password_hash VARCHAR(255)                   NOT NULL COMMENT '密码哈希',
    nickname      VARCHAR(50)                    NOT NULL COMMENT '昵称',
    avatar        VARCHAR(255)                   NULL COMMENT '头像URL',
    `role`        ENUM      DEFAULT 'user'       NULL COMMENT '用户角色',
    status        ENUM      DEFAULT 'active'     NULL COMMENT '账户状态',
    registered_at timestamp DEFAULT NOW()        NULL COMMENT '注册时间',
    last_login_at timestamp                      NULL COMMENT '最后登录时间',
    created_at    timestamp DEFAULT NOW()        NULL COMMENT '创建时间',
    updated_at    timestamp DEFAULT NOW()        NULL COMMENT '更新时间',
    CONSTRAINT `PRIMARY` PRIMARY KEY (id)
) COMMENT ='用户表';

CREATE TABLE visits
(
    id          BIGINT UNSIGNED AUTO_INCREMENT NOT NULL COMMENT '主键ID',
    user_id     BIGINT UNSIGNED                NULL COMMENT '用户ID(匿名访问时为NULL)',
    target_type ENUM                           NOT NULL COMMENT '访问目标类型',
    target_id   BIGINT UNSIGNED                NULL COMMENT '目标ID(页面访问时为NULL)',
    ip_address  VARCHAR(45)                    NOT NULL COMMENT 'IP地址',
    user_agent  LONGTEXT                       NULL COMMENT '用户代理',
    referrer    VARCHAR(500)                   NULL COMMENT '来源URL',
    session_id  VARCHAR(100)                   NULL COMMENT '会话ID',
    created_at  timestamp DEFAULT NOW()        NULL COMMENT '访问时间',
    CONSTRAINT `PRIMARY` PRIMARY KEY (id)
) COMMENT ='访问记录表';

ALTER TABLE system_configs
    ADD CONSTRAINT config_key UNIQUE (config_key);

ALTER TABLE users
    ADD CONSTRAINT email UNIQUE (email);

ALTER TABLE categories
    ADD CONSTRAINT name UNIQUE (name);

ALTER TABLE likes
    ADD CONSTRAINT uk_user_target UNIQUE (user_id, target_type, target_id);

ALTER TABLE users
    ADD CONSTRAINT username UNIQUE (username);

CREATE INDEX idx_action ON operation_logs (action);

CREATE INDEX idx_created_at ON visits (created_at);

CREATE INDEX idx_created_at ON visits (created_at);

CREATE INDEX idx_created_at ON visits (created_at);

CREATE INDEX idx_created_at ON visits (created_at);

CREATE INDEX idx_created_at ON visits (created_at);

CREATE INDEX idx_fulltext ON articles (title, content);

CREATE INDEX idx_ip_address ON visits (ip_address);

CREATE INDEX idx_published_at ON articles (published_at);

CREATE INDEX idx_role ON users (`role`);

CREATE INDEX idx_session_id ON visits (session_id);

CREATE INDEX idx_sort_order ON categories (sort_order);

CREATE INDEX idx_status ON users (status);

CREATE INDEX idx_status ON users (status);

CREATE INDEX idx_status ON users (status);

CREATE INDEX idx_target ON visits (target_type, target_id);

CREATE INDEX idx_target ON visits (target_type, target_id);

CREATE INDEX idx_target ON visits (target_type, target_id);

ALTER TABLE article_statistics
    ADD CONSTRAINT article_statistics_ibfk_1 FOREIGN KEY (article_id) REFERENCES articles (id) ON DELETE CASCADE;

CREATE INDEX idx_article_id ON comments (article_id);

ALTER TABLE articles
    ADD CONSTRAINT articles_ibfk_1 FOREIGN KEY (author_id) REFERENCES users (id) ON DELETE CASCADE;

CREATE INDEX idx_author_id ON articles (author_id);

ALTER TABLE articles
    ADD CONSTRAINT articles_ibfk_2 FOREIGN KEY (category_id) REFERENCES categories (id) ON DELETE SET NULL;

CREATE INDEX idx_category_id ON articles (category_id);

ALTER TABLE comments
    ADD CONSTRAINT comments_ibfk_1 FOREIGN KEY (article_id) REFERENCES articles (id) ON DELETE CASCADE;

CREATE INDEX idx_article_id ON comments (article_id);

ALTER TABLE comments
    ADD CONSTRAINT comments_ibfk_2 FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE SET NULL;

CREATE INDEX idx_user_id ON visits (user_id);

ALTER TABLE comments
    ADD CONSTRAINT comments_ibfk_3 FOREIGN KEY (parent_id) REFERENCES comments (id) ON DELETE CASCADE;

CREATE INDEX idx_parent_id ON comments (parent_id);

ALTER TABLE likes
    ADD CONSTRAINT likes_ibfk_1 FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE;

ALTER TABLE operation_logs
    ADD CONSTRAINT operation_logs_ibfk_1 FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE;

CREATE INDEX idx_user_id ON visits (user_id);

ALTER TABLE visits
    ADD CONSTRAINT visits_ibfk_1 FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE SET NULL;

CREATE INDEX idx_user_id ON visits (user_id);