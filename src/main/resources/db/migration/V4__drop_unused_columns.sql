-- 删除没人读写的列：message_user_message 的 code、description、label、sorting，服务代码、手写 SQL、接口、rpc-api 与 admin-web 都不用它们。
-- 删之前逐列核对：整列只有 NULL 或列默认值才删；只要有一行别的值就报错，整版回滚，服务停止启动，留给人工判断。
DO $$
DECLARE
    target record;
    column_default text;
    remaining bigint;
BEGIN
    FOR target IN
        SELECT * FROM (VALUES
            ('message_user_message', 'code'),
            ('message_user_message', 'description'),
            ('message_user_message', 'label'),
            ('message_user_message', 'sorting')
        ) AS drops(table_name, column_name)
    LOOP
        SELECT c.column_default INTO column_default
        FROM information_schema.columns c
        WHERE c.table_schema = current_schema() AND c.table_name = target.table_name AND c.column_name = target.column_name;
        IF NOT FOUND THEN
            CONTINUE;
        END IF;
        EXECUTE format('SELECT count(*) FROM %I WHERE %I IS NOT NULL%s', target.table_name, target.column_name,
                CASE WHEN column_default IS NULL THEN ''
                     ELSE format(' AND %I IS DISTINCT FROM (%s)', target.column_name, column_default) END)
            INTO remaining;
        IF remaining > 0 THEN
            RAISE EXCEPTION '%.% 还有 % 行数据，不能删除', target.table_name, target.column_name, remaining;
        END IF;
        EXECUTE format('ALTER TABLE %I DROP COLUMN %I', target.table_name, target.column_name);
    END LOOP;
END
$$;
