-- 用户消息表加来源事件 ID：由消息事件生成的站内信（如账号已创建后的欢迎提醒）记下事件 ID，
-- 同一事件对同一接收人只写一行，RocketMQ 重复投递时由唯一索引去重。人工发送的消息此列为空，不受唯一约束。
-- 只加列和部分唯一索引，不删列、不改已有数据。

-- 用户消息表。
ALTER TABLE message_user_message ADD COLUMN source_event_id varchar(64) COLLATE fleet_general_ci;
COMMENT ON COLUMN message_user_message.source_event_id IS '来源事件ID';
CREATE UNIQUE INDEX IF NOT EXISTS uk_message_user_message_source_event
    ON message_user_message (source_event_id, receiver_user_id) WHERE source_event_id IS NOT NULL;
