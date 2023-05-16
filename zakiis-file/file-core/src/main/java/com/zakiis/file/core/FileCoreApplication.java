package com.zakiis.file.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import com.zakiis.common.TraceIdUtil;
import com.zakiis.file.core.boot.autoconfigure.properties.FileCoreProperties;

@SpringBootApplication
@EnableDiscoveryClient
@EnableConfigurationProperties(FileCoreProperties.class)
public class FileCoreApplication {

	public static void main(String[] args) {
		TraceIdUtil.init("FILE_");
		SpringApplication.run(FileCoreApplication.class, args);
	}

}
