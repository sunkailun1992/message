-- 排序列 sorting 改名 sort（第6阶段第三部分，对应 utils 2.0.9 的 EntityBase.sort）。
-- 只改列名，列注释、默认值与数据随列保留；本库没有名字或定义里带 sorting 的索引。
-- 表只由本服务读写、前后端同批发布，按《Refactoring Databases》的 Rename Column 直接改名，不留过渡列。

-- 用户消息表。
ALTER TABLE message_user_message RENAME COLUMN sorting TO sort;
