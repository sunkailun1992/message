-- 实体租户基类新增创建部门：用户消息表加 create_dept，插入时由 utils 自动填当前登录用户的部门，没有登录用户（系统发送、服务调用）时为空。
-- 只加列，不删列、不改已有数据；已有行的创建部门未知，保持为空。表里原有的 dept_id 是消息归属部门，含义不同，保持不变。

-- 用户消息表。
ALTER TABLE message_user_message ADD COLUMN create_dept varchar(64) COLLATE fleet_general_ci;
COMMENT ON COLUMN message_user_message.create_dept IS '创建部门ID';
