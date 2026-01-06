package com.gb;

import com.alibaba.cloud.seata.feign.SeataFeignClientAutoConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * <p>
 * 项目启动项
 * </p>
 *
 * @author 孙凯伦
 * @since 2021-04-02
 */
@EnableCaching
@SpringBootApplication(exclude = {SeataFeignClientAutoConfiguration.class, DataSourceAutoConfiguration.class})
@RestController
@EnableDiscoveryClient
@EnableFeignClients
@EnableAsync
@EnableScheduling
@EnableTransactionManagement
@MapperScan("com.gb.*.mapper")
public class ApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiApplication.class, args);
	}

	@Bean
	public ServerEndpointExporter serverEndpointExporter() {
		return new ServerEndpointExporter();
	}
}
