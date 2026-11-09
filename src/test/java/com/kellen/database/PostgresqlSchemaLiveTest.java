package com.kellen.database;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import static org.assertj.core.api.Assertions.assertThat;

/** PostgreSQL 实际执行、重复建表及布尔/时间/字符比较回归，不连接真实业务库。 */
@EnabledIfEnvironmentVariable(named = "FLEET_PG_TEST_URL", matches = ".+")
class PostgresqlSchemaLiveTest extends PostgresqlSchemaFixture {
    @Test
    void createsTheCurrentBusinessSchemaTwiceWithoutMysqlSyntax() throws Exception {
        script("db/postgresql/message-schema.sql");
        script("db/postgresql/message-schema.sql");
        assertThat(number("SELECT count(*) FROM information_schema.tables WHERE table_schema = '" + schema + "' AND table_type = 'BASE TABLE'"))
                .isEqualTo(1);
        assertThat(number("SELECT count(*) FROM information_schema.columns WHERE table_schema = '" + schema + "' AND column_name = 'is_delete' AND data_type = 'boolean'"))
                .isEqualTo(1);
        assertThat(number("SELECT count(*) FROM information_schema.columns WHERE table_schema = '" + schema + "' AND column_name = 'modify_date_time' AND data_type = 'timestamp without time zone'"))
                .isEqualTo(1);
        assertThat(number("SELECT CASE WHEN 'Café' COLLATE public.fleet_general_ci = 'CAFE' THEN 1 ELSE 0 END")).isEqualTo(1);
        assertThat(number("SELECT CASE WHEN 'Café' COLLATE public.fleet_general_ci LIKE 'caf%' THEN 1 ELSE 0 END")).isEqualTo(1);
    }
}
