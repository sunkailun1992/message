package com.kellen.message.controller;

import com.kellen.rpc.user.UserRpcDTO;
import com.kellen.rpc.user.UserRpcService;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ProtocolConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.spring.ServiceBean;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.apache.dubbo.config.spring.reference.ReferenceBeanManager;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserRpcDiagnosticStartupTest {

    @Test
    void initializesUserReferenceOnlyWhenAuthorizedDiagnosticCallReachesProvider() {
        try (var context = new AnnotationConfigApplicationContext(LocalDubboConfiguration.class)) {
            var references = context.getBean(ReferenceBeanManager.class).getReferences();
            assertThat(references).hasSize(1);
            var reference = references.iterator().next().getReferenceConfig();
            assertThat(reference.configInitialized()).as("user RPC after startup").isFalse();

            var controller = context.getBean(UserRpcTestController.class);
            var securityContext = SecurityContextHolder.createEmptyContext();
            SecurityContextHolder.setContext(securityContext);
            try {
                securityContext.setAuthentication(new TestingAuthenticationToken("diagnostic", null, "user:read"));
                assertThatThrownBy(() -> controller.getUserByRpc("sample-user", "sample-tenant"))
                        .isInstanceOf(AccessDeniedException.class);
                assertThat(reference.configInitialized()).as("user RPC after access denied").isFalse();

                securityContext.setAuthentication(new TestingAuthenticationToken("diagnostic", null, "user:auth:manage"));
                var response = controller.getUserByRpc("sample-user", "sample-tenant");

                assertThat(reference.configInitialized()).as("user RPC after first authorized call").isTrue();
                assertThat(response.getSuccess()).isTrue();
                assertThat(response.getData().getId()).isEqualTo("sample-user");
                assertThat(response.getData().getTenantId()).isEqualTo("sample-tenant");
            } finally {
                SecurityContextHolder.clearContext();
            }
        }
    }

    @Configuration(proxyBeanMethods = false)
    @EnableDubbo(scanBasePackages = "com.kellen.message.diagnosticstartupfixture")
    @EnableMethodSecurity
    @Import(UserRpcTestController.class)
    static class LocalDubboConfiguration {

        @Bean
        ApplicationConfig dubboApplication() {
            var application = new ApplicationConfig("message-user-rpc-startup-test");
            application.setQosEnable(false);
            return application;
        }

        @Bean
        RegistryConfig dubboRegistry() {
            return new RegistryConfig(RegistryConfig.NO_AVAILABLE);
        }

        @Bean
        ProtocolConfig dubboProtocol() {
            return new ProtocolConfig("injvm");
        }

        @Bean
        ServiceBean<UserRpcService> userProvider(ApplicationContext context) {
            var service = new ServiceBean<UserRpcService>(context);
            service.setInterface(UserRpcService.class);
            service.setScope("local");
            service.setRef((tenantId, userId) -> {
                var user = new UserRpcDTO();
                user.setTenantId(tenantId);
                user.setId(userId);
                return user;
            });
            return service;
        }
    }
}
