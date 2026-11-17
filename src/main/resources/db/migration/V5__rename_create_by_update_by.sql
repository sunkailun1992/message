-- 审计列改为主流项目的多数命名（RuoYi-Vue、RuoYi-Vue-Plus、JeecgBoot）：creator→create_by、updater→update_by。
-- 只改列名，列注释、默认值与数据随列保留。

-- 用户消息表。
ALTER TABLE message_user_message RENAME COLUMN creator TO create_by;
ALTER TABLE message_user_message RENAME COLUMN updater TO update_by;
