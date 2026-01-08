-- ========================================
-- 行为统计测试数据生成脚本
-- ========================================

-- 设置日期变量（用于生成测试数据）
SET @start_date = DATE_SUB(CURDATE(), INTERVAL 30 DAY);
SET @end_date = CURDATE();

-- ========================================
-- 1. 生成访问记录数据（visits表）
-- ========================================
INSERT INTO visits (id, user_id, ip_address, user_agent, referrer, created_at, updated_at)
SELECT 
    NULL as id,
    FLOOR(1 + RAND() * 20) as user_id, -- 随机用户ID
    CONCAT('192.168.', FLOOR(1 + RAND() * 255), '.', FLOOR(1 + RAND() * 255)) as ip_address,
    'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36' as user_agent,
    CASE 
        WHEN RAND() < 0.5 THEN NULL
        WHEN RAND() < 0.75 THEN 'https://www.google.com/search?q=blog'
        WHEN RAND() < 0.85 THEN 'https://www.baidu.com/s?wd=blog'
        WHEN RAND() < 0.95 THEN 'https://www.bing.com/search?q=blog'
        ELSE 'https://example.com/blog-link'
    END as referrer,
    DATE_ADD(@start_date, INTERVAL FLOOR(RAND() * 30) DAY) as created_at,
    NOW() as updated_at
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
-- 2. 生成点赞记录数据（likes表）
-- ========================================
INSERT INTO likes (id, user_id, target_type, target_id, created_at, updated_at)
SELECT 
    NULL as id,
    FLOOR(1 + RAND() * 20) as user_id,
    CASE 
        WHEN RAND() < 0.7 THEN 'article'
        ELSE 'comment'
    END as target_type,
    FLOOR(1 + RAND() * 50) as target_id,
    DATE_ADD(@start_date, INTERVAL FLOOR(RAND() * 30) DAY) as created_at,
    NOW() as updated_at
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
-- 3. 生成评论记录数据（comments表）
-- ========================================
INSERT INTO comments (id, article_id, user_id, parent_id, content, status, ip_address, user_agent, created_at, updated_at)
SELECT 
    NULL as id,
    FLOOR(1 + RAND() * 50) as article_id,
    FLOOR(1 + RAND() * 20) as user_id,
    CASE 
        WHEN RAND() < 0.7 THEN NULL
        ELSE FLOOR(1 + RAND() * 100)
    END as parent_id,
    CONCAT('这是一条测试评论内容 ', FLOOR(RAND() * 1000)) as content,
    CASE 
        WHEN RAND() < 0.6 THEN 'approved'
        WHEN RAND() < 0.8 THEN 'pending'
        WHEN RAND() < 0.9 THEN 'rejected'
        ELSE 'spam'
    END as status,
    CONCAT('192.168.', FLOOR(1 + RAND() * 255), '.', FLOOR(1 + RAND() * 255)) as ip_address,
    'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36' as user_agent,
    DATE_ADD(@start_date, INTERVAL FLOOR(RAND() * 30) DAY) as created_at,
    NOW() as updated_at
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
-- 4. 更新文章统计信息（article_statistics表）
-- ========================================
-- 确保文章有统计数据
INSERT IGNORE INTO article_statistics (article_id, view_count, like_count, comment_count, share_count, created_at, updated_at)
SELECT 
    id as article_id,
    FLOOR(50 + RAND() * 500) as view_count,
    FLOOR(10 + RAND() * 100) as like_count,
    FLOOR(5 + RAND() * 50) as comment_count,
    FLOOR(2 + RAND() * 20) as share_count,
    NOW() as created_at,
    NOW() as updated_at
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

-- ========================================
-- 6. 预览统计数据（用于验证）
-- ========================================
SELECT '=== 访问量趋势（最近7天）===' as preview;
SELECT 
    DATE_FORMAT(created_at, '%Y-%m-%d') as date,
    COUNT(*) as visits
FROM visits
WHERE created_at >= DATE_SUB(CURDATE(), INTERVAL 7 DAY)
GROUP BY DATE_FORMAT(created_at, '%Y-%m-%d')
ORDER BY date ASC;

SELECT '=== 访问来源分布 ===' as preview;
SELECT 
    CASE 
        WHEN referrer IS NULL OR referrer = '' THEN '直接访问'
        WHEN referrer LIKE '%google%' OR referrer LIKE '%baidu%' OR referrer LIKE '%bing%' THEN '搜索引擎'
        ELSE '外部链接'
    END as source,
    COUNT(*) as count
FROM visits
GROUP BY source
ORDER BY count DESC;

SELECT '=== 点赞趋势（最近7天）===' as preview;
SELECT 
    DATE_FORMAT(created_at, '%Y-%m-%d') as date,
    COUNT(*) as likes
FROM likes
WHERE created_at >= DATE_SUB(CURDATE(), INTERVAL 7 DAY)
GROUP BY DATE_FORMAT(created_at, '%Y-%m-%d')
ORDER BY date ASC;

SELECT '=== 评论状态分布 ===' as preview;
SELECT 
    CASE status
        WHEN 'pending' THEN '待审核'
        WHEN 'approved' THEN '已发布'
        WHEN 'rejected' THEN '已拒绝'
        WHEN 'spam' THEN '已删除'
        ELSE status
    END as type,
    COUNT(*) as value
FROM comments
GROUP BY status;

SELECT '=== 评论小时分布 ===' as preview;
SELECT 
    HOUR(created_at) as hour,
    COUNT(*) as count
FROM comments
WHERE created_at >= DATE_SUB(CURDATE(), INTERVAL 7 DAY)
GROUP BY HOUR(created_at)
ORDER BY hour ASC;

SELECT '=== 热门评论文章（前5名）===' as preview;
SELECT 
    a.id,
    a.title,
    a.comment_count
FROM articles a
WHERE a.status = '1' AND a.comment_count > 0
ORDER BY a.comment_count DESC
LIMIT 5;
