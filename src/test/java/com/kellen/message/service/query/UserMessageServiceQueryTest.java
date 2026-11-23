package com.kellen.message.service.query;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kellen.message.entity.UserMessage;
import com.kellen.message.entity.query.UserMessageQuery;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 用户消息查询条件的排序与查询字段：前端传来的字段名只按白名单换成数据库列名，原样字符串不进 SQL。
 * 排序列与查询列是 SQL 标识符，不能用预编译参数绑定，只能白名单映射（OWASP SQL Injection Prevention Cheat Sheet）。
 *
 * @author sunkailun
 * @className UserMessageServiceQueryTest
 */
class UserMessageServiceQueryTest {

    /**
     * 被测的查询条件增强。
     */
    private final UserMessageServiceQuery serviceQuery = new UserMessageServiceQuery();

    /**
     * 按查询参数生成的排序片段（多个空白压成一个）。
     *
     * @param query 用户消息查询参数
     * @return ORDER BY 片段
     */
    private String orderBy(UserMessageQuery query) {
        return serviceQuery.query(query, new QueryWrapper<UserMessage>()).getSqlSegment().strip().replaceAll("\\s+", " ");
    }

    @Test
    void anInjectedSortExpressionNeverReachesOrderBy() {
        UserMessageQuery query = new UserMessageQuery();
        query.setCollationFields("send_date_time; DROP TABLE message_user_message --");

        assertThat(orderBy(query)).isEqualTo("ORDER BY send_date_time DESC");
    }

    @Test
    void theOldSortingFieldFallsBackToTheDefaultOrderInsteadOfFailingInTheDatabase() {
        UserMessageQuery query = new UserMessageQuery();
        query.setCollationFields("sorting");

        assertThat(orderBy(query)).isEqualTo("ORDER BY send_date_time DESC");
    }

    @Test
    void knownFieldsMapToTheirColumnsByPropertyOrColumnName() {
        UserMessageQuery byProperty = new UserMessageQuery();
        byProperty.setCollationFields("readDateTime");
        byProperty.setCollation(true);
        UserMessageQuery byColumn = new UserMessageQuery();
        byColumn.setCollationFields(" read_date_time ");

        assertThat(orderBy(byProperty)).isEqualTo("ORDER BY read_date_time ASC");
        assertThat(orderBy(byColumn)).isEqualTo("ORDER BY read_date_time DESC");
    }

    @Test
    void selectKeepsOnlyWhitelistedColumnsOnceEach() {
        UserMessageQuery query = new UserMessageQuery();
        query.setFields("title, content,(select password from auth_user),title,sendDateTime");

        QueryWrapper<UserMessage> wrapper = serviceQuery.query(query, new QueryWrapper<>());

        assertThat(wrapper.getSqlSelect()).isEqualTo("title,content,send_date_time");
    }

    @Test
    void withoutRequestedFieldsTheDefaultSelectStays() {
        UserMessageQuery query = new UserMessageQuery();
        query.setFields("(select 1), drop");

        assertThat(serviceQuery.query(query, new QueryWrapper<>()).getSqlSelect()).isNull();
        assertThat(serviceQuery.query(new UserMessageQuery(), new QueryWrapper<>()).getSqlSelect()).isNull();
    }
}
