package com.kellen.database;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.kellen.message.entity.UserMessage;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/** 服务自己的 Flyway 迁移在独占 schema 上实际执行，并回归布尔、时间与大小写不敏感比较；不连接真实业务库。 */
@EnabledIfEnvironmentVariable(named = "FLEET_PG_TEST_URL", matches = ".+")
class PostgresqlSchemaLiveTest extends PostgresqlSchemaFixture {

    /** 每张表都必须有的全部基础字段；undo_log 与 Flyway 记录表是框架表，除外。 */
    private static final List<String> BASE_COLUMNS = List.of("id", "code", "description", "create_time", "create_by",
            "update_time", "update_by", "is_deleted", "label", "sort", "version", "tenant_id", "create_dept");

    @Test
    void everyTableHasAllBaseColumns() throws Exception {
        migrate(null);
        List<String> tables = strings("SELECT table_name FROM information_schema.tables WHERE table_schema = '" + schema
                + "' AND table_type = 'BASE TABLE' AND table_name NOT IN ('undo_log', 'flyway_schema_history') ORDER BY 1");
        assertThat(tables).isNotEmpty();
        for (String table : tables) {
            assertThat(columns(table)).as(table).containsAll(BASE_COLUMNS)
                    .doesNotContain("creator", "updater", "create_name", "modify_name", "create_date_time", "modify_date_time");
        }
    }

    @Test
    void noTableKeepsSortingColumn() throws Exception {
        // 第6阶段第三部分把排序列 sorting 改名 sort（V7）；迁移后本 schema 里不能再有 sorting 列。
        migrate(null);
        assertThat(number("SELECT count(*) FROM information_schema.columns WHERE table_schema = '" + schema
                + "' AND column_name = 'sorting'")).isZero();
    }

    @Test
    void migratesEmptySchemaToLatestVersion() throws Exception {
        assertThat(migrate(null).success).isTrue();
        assertThat(appliedVersions()).containsExactly("1", "2", "3", "4", "5", "6", "7");
        assertThat(strings("SELECT table_name FROM information_schema.tables WHERE table_schema = '" + schema
                + "' AND table_type = 'BASE TABLE' AND table_name NOT IN ('undo_log', 'flyway_schema_history') ORDER BY 1"))
                .containsExactly("message_user_message");
        assertThat(columns("message_user_message"))
                .contains("id", "code", "description", "create_time", "create_by", "update_time", "update_by", "is_deleted",
                        "label", "sort", "version", "tenant_id", "create_dept", "source_event_id")
                .doesNotContain("create_date_time", "modify_date_time", "create_name", "modify_name", "is_delete", "creator", "updater");
        assertThat(number("SELECT count(*) FROM information_schema.columns WHERE table_schema = '" + schema
                + "' AND column_name = 'create_dept' AND data_type = 'character varying' AND character_maximum_length = 64"
                + " AND is_nullable = 'YES'")).isEqualTo(1);
        assertThat(number("SELECT count(*) FROM information_schema.columns WHERE table_schema = '" + schema
                + "' AND table_name = 'message_user_message' AND column_name = 'source_event_id' AND data_type = 'character varying'"
                + " AND character_maximum_length = 64 AND is_nullable = 'YES'")).isEqualTo(1);
        assertThat(number("SELECT count(*) FROM pg_indexes WHERE schemaname = '" + schema
                + "' AND indexname = 'uk_message_user_message_source_event' AND indexdef LIKE 'CREATE UNIQUE INDEX %'"
                + " AND indexdef LIKE '%(source_event_id, receiver_user_id) WHERE (source_event_id IS NOT NULL)'")).isEqualTo(1);
        assertThat(number("SELECT count(*) FROM information_schema.tables WHERE table_schema = '" + schema
                + "' AND table_name = 'ddl_history'")).isZero();
        assertThat(number("SELECT count(*) FROM information_schema.tables WHERE table_schema = '" + schema
                + "' AND table_name = 'undo_log'")).isEqualTo(1);
    }

    @Test
    void createDeptIsAddedWithoutDroppingAnyColumnOrRow() throws Exception {
        migrate("3");
        execute("INSERT INTO message_user_message(id, message_type, receiver_user_id, title, content, tenant_id, label, sorting) VALUES ('fixture-kept', 'SYSTEM', 'fixture-user', 'synthetic', 'synthetic', 'fixture-tenant', 'kept', 7)");
        migrate(null);
        assertThat(appliedVersions()).containsExactly("1", "2", "3", "4", "5", "6", "7");
        assertThat(number("SELECT count(*) FROM message_user_message WHERE id = 'fixture-kept' AND label = 'kept' AND sort = 7"
                + " AND create_dept IS NULL")).isEqualTo(1);
        assertThat(strings("SELECT col_description(('" + schema + ".message_user_message')::regclass, ordinal_position::int)"
                + " FROM information_schema.columns WHERE table_schema = '" + schema
                + "' AND table_name = 'message_user_message' AND column_name = 'create_dept'")).containsExactly("创建部门ID");
    }

    @Test
    void everyEntityFieldMapsToAColumnOfItsTable() throws Exception {
        migrate(null);
        TableInfo info = TableInfoHelper.initTableInfo(
                new MapperBuilderAssistant(new MybatisConfiguration(), "entity-columns"), UserMessage.class);
        List<String> mapped = new ArrayList<>(List.of(info.getKeyColumn()));
        info.getFieldList().forEach(field -> mapped.add(field.getColumn()));
        assertThat(columns(info.getTableName())).containsAll(mapped);
    }

    @Test
    void secondMigrationRunIsANoOpAndKeepsPostgresqlTypes() throws Exception {
        migrate(null);
        assertThat(migrate(null).migrationsExecuted).isZero();
        assertThat(number("SELECT count(*) FROM information_schema.columns WHERE table_schema = '" + schema + "' AND column_name = 'is_deleted' AND data_type = 'boolean'"))
                .isEqualTo(1);
        assertThat(number("SELECT count(*) FROM information_schema.columns WHERE table_schema = '" + schema + "' AND column_name = 'update_time' AND data_type = 'timestamp without time zone'"))
                .isEqualTo(1);
        assertThat(number("SELECT CASE WHEN 'Café' COLLATE fleet_general_ci = 'CAFE' THEN 1 ELSE 0 END")).isEqualTo(1);
        assertThat(number("SELECT CASE WHEN 'Café' COLLATE fleet_general_ci LIKE 'caf%' THEN 1 ELSE 0 END")).isEqualTo(1);
    }
}
