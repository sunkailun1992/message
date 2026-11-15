-- 审计列按实体基类改名：create_date_time→create_time、modify_date_time→update_time、create_name→creator、
-- modify_name→updater、is_delete→is_deleted。列注释、默认值与索引随列保留，数据不变。

-- 用户消息表。
ALTER TABLE message_user_message RENAME COLUMN create_date_time TO create_time;
ALTER TABLE message_user_message RENAME COLUMN modify_date_time TO update_time;
ALTER TABLE message_user_message RENAME COLUMN create_name TO creator;
ALTER TABLE message_user_message RENAME COLUMN modify_name TO updater;
ALTER TABLE message_user_message RENAME COLUMN is_delete TO is_deleted;
