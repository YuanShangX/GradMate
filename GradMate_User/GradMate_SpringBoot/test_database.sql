-- 研小伴数据库测试脚本

-- 1. 检查表是否存在
SHOW TABLES LIKE 'chat%';

-- 2. 查看chat_session表结构
DESC chat_session;

-- 3. 查看chat_message表结构
DESC chat_message;

-- 4. 查看会话数据
SELECT * FROM chat_session ORDER BY update_time DESC LIMIT 5;

-- 5. 查看消息数据
SELECT 
    id,
    session_id,
    user_id,
    role,
    LEFT(content, 50) as content_preview,
    tokens,
    create_time
FROM chat_message 
ORDER BY create_time DESC 
LIMIT 10;

-- 6. 统计数据
SELECT 
    '会话总数' as metric,
    COUNT(*) as count
FROM chat_session
UNION ALL
SELECT 
    '消息总数' as metric,
    COUNT(*) as count
FROM chat_message
UNION ALL
SELECT 
    '用户数' as metric,
    COUNT(DISTINCT user_id) as count
FROM chat_session;

-- 7. 查看每个用户的会话数和消息数
SELECT 
    cs.user_id,
    COUNT(DISTINCT cs.session_id) as session_count,
    SUM(cs.message_count) as total_messages,
    MAX(cs.update_time) as last_activity
FROM chat_session cs
GROUP BY cs.user_id
ORDER BY last_activity DESC;
