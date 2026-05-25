package com.kellen.bean;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author lijinhao
 */
@Configuration
public class GongBaoConfig {

    /**
     * 服务名称
     */
    public static String serverName;

    @Value("${spring.application.name}")
    public void setServerName(String serverName) {
        GongBaoConfig.serverName = serverName;
    }

}
