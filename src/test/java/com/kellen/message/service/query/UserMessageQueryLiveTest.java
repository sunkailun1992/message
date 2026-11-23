package com.kellen.message.service.query;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.MybatisSqlSessionFactoryBean;
import com.kellen.config.mybatis.MyBatisPlusConfig;
import com.kellen.config.mybatis.MyMetaObjectHandler;
import com.kellen.config.mybatis.MybatisPlusSecurityProperties;
import com.kellen.database.PostgresqlSchemaFixture;
import com.kellen.datapermission.DataPermissionProperties;
import com.kellen.message.entity.bo.UserMessageBO;
import com.kellen.message.entity.enums.MessageTypeEnum;
import com.kellen.message.entity.query.UserMessageQuery;
import com.kellen.message.entity.vo.UserMessageVO;
import com.kellen.message.mapper.UserMessageMapper;
import com.kellen.message.service.impl.UserMessageServiceImpl;
import com.kellen.message.service.results.UserMessageServiceResults;
import com.kellen.security.SecurityUser;
import com.kellen.security.config.TenantProperties;
import com.kellen.security.UserContextHolder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.util.List;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 在独立 schema 上验证当前用户消息查询的排序与查询字段：真实的 Mapper、租户插件与 PostgreSQL。
 * 前端传来的字段名只按白名单换成列名，注入的片段与改名前的 sorting 都不进 SQL，查询照常按发送时间倒序返回。
 */
@EnabledIfEnvironmentVariable(named = "FLEET_PG_TEST_URL", matches = ".+")
class UserMessageQueryLiveTest extends PostgresqlSchemaFixture {

    private UserMessageServiceImpl service;

    @BeforeEach
    void wireServiceAndTwoMessages() throws Exception {
        assertThat(migrate(null).success).isTrue();
        Properties connection = new Properties();
        connection.setProperty("currentSchema", schema + ",public");
        DriverManagerDataSource dataSource = new DriverManagerDataSource(System.getenv("FLEET_PG_TEST_URL"),
                System.getenv("FLEET_PG_TEST_USERNAME"), System.getenv("FLEET_PG_TEST_PASSWORD"));
        dataSource.setConnectionProperties(connection);
        TenantProperties tenant = new TenantProperties();
        tenant.setEnabled(true);
        tenant.setIgnoreWithoutTenant(false);
        MybatisConfiguration configuration = new MybatisConfiguration();
        configuration.setMapUnderscoreToCamelCase(true);
        GlobalConfig global = new GlobalConfig().setDbConfig(new GlobalConfig.DbConfig()).setMetaObjectHandler(new MyMetaObjectHandler(tenant));
        GlobalConfigUtils.setGlobalConfig(configuration, global);
        configuration.addMapper(UserMessageMapper.class);
        MybatisSqlSessionFactoryBean factory = new MybatisSqlSessionFactoryBean();
        factory.setDataSource(dataSource);
        factory.setConfiguration(configuration);
        factory.setGlobalConfig(global);
        factory.setPlugins(new MyBatisPlusConfig(tenant, new DataPermissionProperties(), new MybatisPlusSecurityProperties()).mybatisPlusInterceptor());
        UserMessageMapper mapper = new SqlSessionTemplate(factory.getObject()).getMapper(UserMessageMapper.class);
        service = new UserMessageServiceImpl(mapper, new UserMessageServiceQuery(), new UserMessageServiceResults());
        UserContextHolder.set(SecurityUser.builder().userId("u").username("u").tenantId("t").enabled(true).build());
        send("早一条");
        send("晚一条");
        execute("UPDATE message_user_message SET send_date_time = TIMESTAMP '2026-11-01 08:00:00' WHERE title = '早一条'");
        execute("UPDATE message_user_message SET send_date_time = TIMESTAMP '2026-11-02 08:00:00' WHERE title = '晚一条'");
    }

    @AfterEach
    void clearCurrentUser() {
        UserContextHolder.clear();
    }

    private void send(String title) {
        UserMessageBO bo = new UserMessageBO();
        bo.setTenantId("t");
        bo.setReceiverUserIds(List.of("u"));
        bo.setMessageType(MessageTypeEnum.SYSTEM_NOTICE);
        bo.setTitle(title);
        bo.setContent(title + "的内容");
        service.send(bo);
    }

    private List<UserMessageVO> currentMessages(String collationFields, String fields) {
        UserMessageQuery query = new UserMessageQuery();
        query.setTenantId("t");
        query.setCollationFields(collationFields);
        query.setFields(fields);
        return service.currentPage(new Page<>(1, 10), query).getRecords();
    }

    @Test
    void anInjectedSortExpressionIsIgnoredAndTheTableSurvives() throws Exception {
        List<UserMessageVO> records = currentMessages("send_date_time; DROP TABLE message_user_message --", null);

        assertThat(records).extracting(UserMessageVO::getTitle).containsExactly("晚一条", "早一条");
        assertThat(number("SELECT count(*) FROM message_user_message")).isEqualTo(2);
    }

    @Test
    void theOldSortingFieldNoLongerFailsTheQuery() {
        assertThat(currentMessages("sorting", null)).extracting(UserMessageVO::getTitle).containsExactly("晚一条", "早一条");
    }

    @Test
    void anInjectedSelectExpressionCannotFillAnotherColumn() {
        List<UserMessageVO> records = currentMessages(null,
                "title, (select string_agg(table_name, ',') from information_schema.tables) AS content");

        assertThat(records).extracting(UserMessageVO::getTitle).containsExactly("晚一条", "早一条");
        assertThat(records).extracting(UserMessageVO::getContent).containsOnlyNulls();
    }
}
