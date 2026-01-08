-- ========================================
-- 简化版行为统计测试数据生成脚本（最终修复版）
-- ========================================

-- 设置日期变量
SET @start_date = DATE_SUB(CURDATE(), INTERVAL 30 DAY);
SET @end_date = CURDATE();

-- ========================================
-- 1. 生成访问记录数据（使用有效的用户ID 6-18）
-- ========================================
INSERT INTO visits (user_id, target_type, target_id, ip_address, user_agent, referrer, created_at)
SELECT 
    FLOOR(6 + RAND() * 13),  -- 用户ID范围：6-18
    'article',
    FLOOR(1 + RAND() * 50),
    CONCAT('192.168.', FLOOR(1 + RAND() * 255), '.', FLOOR(1 + RAND() * 255)),
    'Mozilla/5.0 (Windows NT 10.0; Win64; x64)',
    CASE 
        WHEN RAND() < 0.5 THEN NULL
        WHEN RAND() < 0.75 THEN 'https://www.google.com/search?q=blog'
        WHEN RAND() < 0.85 THEN 'https://www.baidu.com/s?wd=blog'
        WHEN RAND() < 0.95 THEN 'https://www.bing.com/search?q=blog'
        ELSE 'https://example.com/blog-link'
    END,
    DATE_ADD(@start_date, INTERVAL FLOOR(RAND() * 30) DAY)
FROM 
    (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION 
     SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10) t1,
    (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION 
     SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10) t2,
    (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION 
     SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10) t3
WHERE 
    DATE_ADD(@start_date, INTERVAL FLOOR(RAND() * 30) DAY) <= @end_date
LIMIT 500;

-- ========================================
-- 2. 生成点赞记录数据（添加时间戳避免重复）
-- ========================================
INSERT INTO likes (user_id, target_type, target_id, created_at)
SELECT 
    FLOOR(6 + RAND() * 13),
    CASE 
        WHEN RAND() < 0.7 THEN 'article'
        ELSE 'comment'
    END,
    FLOOR(1 + RAND() * 50),
    DATE_ADD(@start_date, INTERVAL FLOOR(RAND() * 30 * 24 * 3600) SECOND)
FROM 
    (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION 
     SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10) t1,
    (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION 
     SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10) t2,
    (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION 
     SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10) t3
WHERE 
    DATE_ADD(@start_date, INTERVAL FLOOR(RAND() * 30) DAY) <= @end_date
LIMIT 300;

-- ========================================
-- 3. 生成评论记录数据（添加时间戳避免重复）
-- ========================================
INSERT INTO comments (article_id, user_id, parent_id, content, status, ip_address, user_agent, created_at)
SELECT 
    FLOOR(1 + RAND() * 50),
    FLOOR(6 + RAND() * 13),
    CASE 
        WHEN RAND() < 0.7 THEN NULL
        ELSE FLOOR(1 + RAND() * 100)
    END,
    CONCAT('这是一条测试评论内容 ', FLOOR(RAND() * 1000)),
    CASE 
        WHEN RAND() < 0.6 THEN 'approved'
        WHEN RAND() < 0.8 THEN 'pending'
        WHEN RAND() < 0.9 THEN 'rejected'
        ELSE 'spam'
    END,
    CONCAT('192.168.', FLOOR(1 + RAND() * 255), '.', FLOOR(1 + RAND() * 255)),
    'Mozilla/5.0 (Windows NT 10.0; Win64; x64)',
    DATE_ADD(@start_date, INTERVAL FLOOR(RAND() * 30 * 24 * 3600) SECOND)
FROM 
    (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION 
     SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10) t1,
    (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION 
     SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10) t2,
    (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION 
     SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10) t3
WHERE 
    DATE_ADD(@start_date, INTERVAL FLOOR(RAND() * 30) DAY) <= @end_date
LIMIT 200;

-- ========================================
-- 4. 更新文章统计信息
-- ========================================
INSERT IGNORE INTO article_statistics (article_id, view_count, like_count, comment_count, share_count, created_at, updated_at)
SELECT 
    id,
    FLOOR(50 + RAND() * 500),
    FLOOR(10 + RAND() * 100),
    FLOOR(5 + RAND() * 50),
    FLOOR(2 + RAND() * 20),
    NOW(),
    NOW()
FROM articles
WHERE id <= 50;

-- ========================================
-- 5. 验证数据生成情况
-- ========================================
SELECT '=== 数据生成完成 ===' as status;

SELECT 
    '访问记录' as data_type,
    COUNT(*) as count,
    MIN(created_at) as earliest_date,
    MAX(created_at) as latest_date
FROM visits
WHERE created_at >= @start_date

UNION ALL

SELECT 
    '点赞记录' as data_type,
    COUNT(*) as count,
    MIN(created_at) as earliest_date,
    MAX(created_at) as latest_date
FROM likes
WHERE created_at >= @start_date

UNION ALL

SELECT 
    '评论记录' as data_type,
    COUNT(*) as count,
    MIN(created_at) as earliest_date,
    MAX(created_at) as latest_date
FROM comments
WHERE created_at >= @start_date;
