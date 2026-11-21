package com.kellen.message.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kellen.message.controller.UserMessageController;
import com.kellen.message.service.UserMessageService;
import com.kellen.message.support.FakeIssuer;
import com.kellen.security.SecurityUser;
import com.kellen.security.oauth2.FleetResourceServerAutoConfiguration;
import com.kellen.security.oauth2.SecurityContextResult;
import com.kellen.security.oauth2.SecurityContextRpcService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.context.ConfigurationPropertiesAutoConfiguration;
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.http.converter.autoconfigure.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.jackson.autoconfigure.JacksonAutoConfiguration;
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;
import org.springframework.boot.security.autoconfigure.web.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.security.autoconfigure.web.servlet.ServletWebSecurityAutoConfiguration;
import org.springframework.boot.security.oauth2.server.resource.autoconfigure.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.tomcat.autoconfigure.servlet.TomcatServletWebServerAutoConfiguration;
import org.springframework.boot.webmvc.autoconfigure.DispatcherServletAutoConfiguration;
import org.springframework.boot.webmvc.autoconfigure.WebMvcAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

/**
 * 用户消息接口的租户边界（第 5 阶段最终复核 Important 4）：管理列表、发送与三个“当前用户”接口带的 {@code tenantId}
 * 只能等于调用方的活动租户，平台超级管理员可以点名任一租户；越界在进入业务服务之前回 403。
 * 真实 Tomcat、真实资源服务器与 Boot 的 issuer-uri 解码器；签发方与 user 服务的业务上下文是本地替身。
 */
@SpringBootTest(classes = UserMessageTenantBoundaryHttpTest.Config.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {"spring.config.name=message-tenant-boundary-isolated", "spring.cloud.nacos.config.enabled=false",
                "spring.cloud.nacos.discovery.enabled=false", "spring.cloud.nacos.config.import-check.enabled=false",
                "spring.security.oauth2.resourceserver.jwt.jws-algorithms=RS256",
                "spring.security.oauth2.resourceserver.jwt.audiences=github-api"})
class UserMessageTenantBoundaryHttpTest {

    static final FakeIssuer ISSUER = FakeIssuer.start();

    @LocalServerPort
    int port;

    @MockitoBean
    UserMessageService messages;

    final HttpClient http = HttpClient.newBuilder().proxy(HttpClient.Builder.NO_PROXY).build();

    final ObjectMapper json = new ObjectMapper();

    @DynamicPropertySource
    static void issuer(DynamicPropertyRegistry registry) {
        registry.add("spring.security.oauth2.resourceserver.jwt.issuer-uri", ISSUER::issuer);
    }

    @AfterAll
    static void stop() {
        ISSUER.close();
    }

    @BeforeEach
    void reset() {
        Mockito.reset(messages);
    }

    @Test
    void everyEndpointRejectsAnotherTenantBeforeTheService() throws Exception {
        for (String[] request : requests("tenant-b")) {
            HttpResponse<String> response = call(request, "user-a");
            assertThat(response.statusCode()).as(String.join(" ", request)).isEqualTo(403);
        }
        verifyNoInteractions(messages);
    }

    @Test
    void ownActiveTenantReachesTheServiceWithThatTenant() throws Exception {
        for (String[] request : requests("tenant-a")) {
            assertThat(call(request, "user-a").statusCode()).as(String.join(" ", request)).isEqualTo(200);
        }
        verify(messages).list(any());
        verify(messages).page(any(), any());
        verify(messages).send(any());
        verify(messages).currentPage(any(), any());
        verify(messages).currentUnreadCount(eq("tenant-a"), isNull());
        verify(messages).readCurrentMessage("tenant-a", "msg-1");
    }

    @Test
    void platformSuperAdminMayTargetAnotherTenant() throws Exception {
        for (String[] request : requests("tenant-a")) {
            assertThat(call(request, "platform-admin").statusCode()).as(String.join(" ", request)).isEqualTo(200);
        }
        verify(messages).readCurrentMessage("tenant-a", "msg-1");
    }

    private static List<String[]> requests(String tenant) {
        return List.of(new String[]{"GET", "/messages/user-messages/options?tenantId=" + tenant},
                new String[]{"GET", "/messages/user-messages?current=1&size=10&tenantId=" + tenant},
                new String[]{"POST", "/messages/user-messages", tenant},
                new String[]{"GET", "/messages/user-messages/current?current=1&size=10&tenantId=" + tenant},
                new String[]{"GET", "/messages/user-messages/current/unread-count?tenantId=" + tenant},
                new String[]{"PATCH", "/messages/user-messages/msg-1/read?tenantId=" + tenant});
    }

    private HttpResponse<String> call(String[] request, String userId) throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + request[1]))
                .header("Authorization", "Bearer " + ISSUER.token(userId, "github-api", "github-gateway"));
        if (request[0].equals("POST")) {
            builder.header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(json.writeValueAsString(Map.of(
                    "tenantId", request[2], "receiverUserIds", List.of("receiver-1"), "messageType", "SYSTEM_NOTICE",
                    "title", "title", "content", "content"))));
        } else {
            builder.method(request[0], HttpRequest.BodyPublishers.noBody());
        }
        return http.send(builder.build(), HttpResponse.BodyHandlers.ofString());
    }

    /**
     * 测试上下文：真实 Spring Security、utils 资源服务器与本服务的真实控制器；user 服务的业务上下文是替身
     * （user-a 的活动租户是 tenant-a；platform-admin 在 tenant-p，是平台超级管理员）。
     */
    @Configuration(proxyBeanMethods = false)
    @ImportAutoConfiguration({TomcatServletWebServerAutoConfiguration.class, DispatcherServletAutoConfiguration.class,
            WebMvcAutoConfiguration.class, HttpMessageConvertersAutoConfiguration.class, JacksonAutoConfiguration.class,
            PropertyPlaceholderAutoConfiguration.class, ConfigurationPropertiesAutoConfiguration.class,
            SecurityAutoConfiguration.class, ServletWebSecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class,
            FleetResourceServerAutoConfiguration.class, OAuth2ResourceServerAutoConfiguration.class})
    @Import(UserMessageController.class)
    static class Config {

        @Bean
        SecurityContextRpcService securityContextRpcService() {
            return (userId, requestedTenantId) -> switch (userId) {
                case "user-a" -> SecurityContextResult.ok(user(userId, "tenant-a", List.of("message:user-message:list", "message:user-message:send")));
                case "platform-admin" -> SecurityContextResult.ok(user(userId, "tenant-p", List.of("user:auth:manage", "ROLE_PLATFORM_SUPER_ADMIN")));
                default -> SecurityContextResult.of(SecurityContextResult.Status.USER_NOT_FOUND);
            };
        }

        private static SecurityUser user(String userId, String tenantId, List<String> authorities) {
            return SecurityUser.builder().userId(userId).username(userId).tenantId(tenantId).deptId("dept").dataScope("SELF")
                    .dataScopeDeptIds(List.of()).authorities(authorities).enabled(true).tenantIds(List.of(tenantId)).build();
        }
    }
}
