package com.zakiis.file.boot.autoconfigure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.zakiis.file.filter.ExceptionFilter;
import com.zakiis.file.filter.LogFilter;
import com.zakiis.file.listener.ContextClosedListener;
import com.zakiis.file.util.RequestUtil;

@Configuration
public class FileCommonAutoConfiguration {

	Logger log = LoggerFactory.getLogger(FileCommonAutoConfiguration.class);
	
//	@Bean
//	@ConditionalOnMissingBean
//	public GlobalExceptionHandler globalExceptionHandler() {
//		log.info("Global excpetion handler feature enabled.");
//		return new GlobalExceptionHandler();
//	}
	
	@Bean
	@ConditionalOnMissingBean
	public LogFilter logFilter() {
		log.info("Log filter feature enabled.");
		return new LogFilter();
	}
	
	@Bean
	@ConditionalOnMissingBean
	public ExceptionFilter exceptionFilter() {
		log.info("Exception filter feature enabled.");
		return new ExceptionFilter();
	}
	
	@Bean
	@ConditionalOnMissingBean
	public ContextClosedListener contextClosedListener() {
		log.info("Context close listener enabled.");
		return new ContextClosedListener();
	}
	
	@Bean
	@ConditionalOnMissingBean
	RequestUtil requestUtil() {
		return new RequestUtil();
	}
}
