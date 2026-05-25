package com.kellen.bean;

import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import com.xxl.job.core.util.NetUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * XXL-JOB executor configuration.
 */
@Slf4j
@Configuration
public class XxlJobConfig {

    @Value("${xxl.job.admin.addresses:}")
    private String adminAddresses;

    @Value("${xxl.job.accessToken:}")
    private String accessToken;

    @Value("${xxl.job.executor.appname:${spring.application.name:}}")
    private String appname;

    @Value("${xxl.job.executor.ip:}")
    private String ip;

    @Value("${xxl.job.executor.port:0}")
    private int port;

    @Value("${server.port:0}")
    private int serverPort;

    @Value("${xxl.job.executor.logpath:./logs/xxl-job/jobhandler}")
    private String logPath;

    @Value("${xxl.job.executor.logretentiondays:30}")
    private int logRetentionDays;

    @Bean
    public XxlJobSpringExecutor xxlJobExecutor() {
        int actualPort = resolveExecutorPort();
        log.info(">>>>>>>>>>> xxl-job config init, adminAddresses:{}, appname:{}, ip:{}, port:{}",
                adminAddresses, appname, ip, actualPort);
        XxlJobSpringExecutor executor = new XxlJobSpringExecutor();
        executor.setAdminAddresses(adminAddresses);
        executor.setAccessToken(accessToken);
        executor.setAppname(appname);
        executor.setIp(ip);
        executor.setPort(actualPort);
        executor.setLogPath(logPath);
        executor.setLogRetentionDays(logRetentionDays);
        return executor;
    }

    private int resolveExecutorPort() {
        if (port > 0) {
            return port;
        }
        int basePort = serverPort > 0 ? serverPort + 10000 : 10000;
        return NetUtil.findAvailablePort(basePort);
    }
}
