package com.zakiis.file.portal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import com.zakiis.common.TraceIdUtil;
import com.zakiis.file.portal.boot.autoconfigure.properties.FilePortalProperties;
import com.zakiis.file.portal.service.ApplicationCacheService;

@SpringBootApplication
@EnableDiscoveryClient
@EnableConfigurationProperties(FilePortalProperties.class)
public class FilePortalApplication implements CommandLineRunner {

	@Autowired
	ApplicationCacheService applicationCacheService;
	
	public static void main(String[] args) {
		TraceIdUtil.init("FILE_");
		SpringApplication.run(FilePortalApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		applicationCacheService.init();
	}

}
