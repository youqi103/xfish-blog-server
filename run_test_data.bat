@echo off
echo 正在生成测试数据...
mysql -h localhost -u root -p123456 blog_system < "e:\Project portfolio\XFishBlog\blog-backend\backUserCenter\user-center\test_statistics_simple.sql"
echo 测试数据生成完成！
pause
