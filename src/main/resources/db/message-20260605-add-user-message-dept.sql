SET @add_dept_column_sql := IF(
    (
        SELECT COUNT(1)
        FROM information_schema.columns
        WHERE table_schema = DATABASE()
          AND table_name = 'message_user_message'
          AND column_name = 'dept_id'
    ) = 0,
    'ALTER TABLE message_user_message ADD COLUMN dept_id varchar(64) DEFAULT NULL COMMENT ''归属部门ID'' AFTER owner_user_id',
    'SELECT 1'
);
PREPARE add_dept_column_stmt FROM @add_dept_column_sql;
EXECUTE add_dept_column_stmt;
DEALLOCATE PREPARE add_dept_column_stmt;

SET @add_owner_dept_index_sql := IF(
    (
        SELECT COUNT(1)
        FROM information_schema.statistics
        WHERE table_schema = DATABASE()
          AND table_name = 'message_user_message'
          AND index_name = 'idx_message_user_message_owner_dept'
    ) = 0,
    'ALTER TABLE message_user_message ADD KEY idx_message_user_message_owner_dept (tenant_id, owner_user_id, dept_id)',
    'SELECT 1'
);
PREPARE add_owner_dept_index_stmt FROM @add_owner_dept_index_sql;
EXECUTE add_owner_dept_index_stmt;
DEALLOCATE PREPARE add_owner_dept_index_stmt;
