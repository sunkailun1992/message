-- PostgreSQL 18 业务结构。历史 MySQL 脚本保留，不在 PostgreSQL 启动时执行。
-- 先以对应业务角色执行 utils 的 db/postgresql/common-infra-schema.sql，再执行本文件。
-- ICU 主级比较保留大小写、重音不敏感的常用匹配；迁移前另查唯一键碰撞与尾空格。
-- MySQL ON UPDATE 时间由应用显式更新；不创建 Seata AT 不支持的数据库触发器。
CREATE COLLATION IF NOT EXISTS public.fleet_general_ci (provider = icu, locale = 'und-u-ks-level1', deterministic = false);

CREATE TABLE IF NOT EXISTS message_user_message (
    id varchar(64) COLLATE public.fleet_general_ci NOT NULL,
    message_type varchar(32) COLLATE public.fleet_general_ci NOT NULL,
    sender_user_id varchar(64) COLLATE public.fleet_general_ci,
    sender_name varchar(255) COLLATE public.fleet_general_ci,
    receiver_user_id varchar(64) COLLATE public.fleet_general_ci NOT NULL,
    owner_user_id varchar(64) COLLATE public.fleet_general_ci,
    dept_id varchar(64) COLLATE public.fleet_general_ci,
    title varchar(255) COLLATE public.fleet_general_ci NOT NULL,
    content text COLLATE public.fleet_general_ci NOT NULL,
    send_state varchar(32) COLLATE public.fleet_general_ci NOT NULL DEFAULT 'SENT',
    read_state varchar(32) COLLATE public.fleet_general_ci NOT NULL DEFAULT 'UNREAD',
    send_date_time timestamp(0) without time zone DEFAULT CURRENT_TIMESTAMP,
    read_date_time timestamp(0) without time zone,
    code varchar(255) COLLATE public.fleet_general_ci,
    description varchar(255) COLLATE public.fleet_general_ci,
    create_date_time timestamp(0) without time zone DEFAULT CURRENT_TIMESTAMP,
    create_name varchar(255) COLLATE public.fleet_general_ci,
    modify_date_time timestamp(0) without time zone DEFAULT CURRENT_TIMESTAMP,
    modify_name varchar(255) COLLATE public.fleet_general_ci,
    is_delete boolean DEFAULT false,
    type integer DEFAULT 0,
    state integer DEFAULT 1,
    label varchar(255) COLLATE public.fleet_general_ci,
    sorting integer DEFAULT 0,
    version integer DEFAULT 1,
    tenant_id varchar(64) COLLATE public.fleet_general_ci NOT NULL DEFAULT '1',
    PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS idx_message_user_message_owner_dept ON message_user_message (tenant_id, owner_user_id, dept_id);
CREATE INDEX IF NOT EXISTS idx_message_user_message_read_state ON message_user_message (read_state);
CREATE INDEX IF NOT EXISTS idx_message_user_message_send_time ON message_user_message (send_date_time);
CREATE INDEX IF NOT EXISTS idx_message_user_message_tenant_receiver ON message_user_message (tenant_id, receiver_user_id);
CREATE INDEX IF NOT EXISTS idx_message_user_message_tenant_sender ON message_user_message (tenant_id, sender_user_id);
CREATE INDEX IF NOT EXISTS idx_message_user_message_type ON message_user_message (message_type);
COMMENT ON TABLE message_user_message IS '用户消息表';
COMMENT ON COLUMN message_user_message.message_type IS '消息类型：SYSTEM_NOTICE/NORMAL_MESSAGE';
COMMENT ON COLUMN message_user_message.sender_user_id IS '发送人用户ID';
COMMENT ON COLUMN message_user_message.sender_name IS '发送人名称';
COMMENT ON COLUMN message_user_message.receiver_user_id IS '接收人用户ID';
COMMENT ON COLUMN message_user_message.owner_user_id IS '负责人用户ID';
COMMENT ON COLUMN message_user_message.dept_id IS '归属部门ID';
COMMENT ON COLUMN message_user_message.title IS '消息标题';
COMMENT ON COLUMN message_user_message.content IS '消息内容';
COMMENT ON COLUMN message_user_message.send_state IS '发送状态：SENT/FAILED';
COMMENT ON COLUMN message_user_message.read_state IS '读取状态：UNREAD/READ';
COMMENT ON COLUMN message_user_message.send_date_time IS '发送时间';
COMMENT ON COLUMN message_user_message.read_date_time IS '读取时间';
COMMENT ON COLUMN message_user_message.code IS '编码';
COMMENT ON COLUMN message_user_message.description IS '说明';
COMMENT ON COLUMN message_user_message.create_date_time IS '创建时间';
COMMENT ON COLUMN message_user_message.create_name IS '创建人';
COMMENT ON COLUMN message_user_message.modify_date_time IS '修改时间';
COMMENT ON COLUMN message_user_message.modify_name IS '修改人';
COMMENT ON COLUMN message_user_message.is_delete IS '删除状态';
COMMENT ON COLUMN message_user_message.type IS '类型（0：默认）';
COMMENT ON COLUMN message_user_message.state IS '状态（1：启用，2：禁用）';
COMMENT ON COLUMN message_user_message.label IS '标签';
COMMENT ON COLUMN message_user_message.sorting IS '排序';
COMMENT ON COLUMN message_user_message.version IS '版本号';
COMMENT ON COLUMN message_user_message.tenant_id IS '租户id';
