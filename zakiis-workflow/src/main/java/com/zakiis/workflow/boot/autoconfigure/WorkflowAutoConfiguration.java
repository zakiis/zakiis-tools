package com.zakiis.workflow.boot.autoconfigure;

import org.activiti.spring.SpringProcessEngineConfiguration;
import org.activiti.spring.boot.ProcessEngineConfigurationConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WorkflowAutoConfiguration {

	
	@Bean
	public ProcessEngineConfigurationConfigurer processEngineConfigurationConfigurer() {
		return new ProcessEngineConfigurationConfigurer() {
			
			@Override
			public void configure(SpringProcessEngineConfiguration processEngineConfiguration) {
				processEngineConfiguration.setAsyncExecutorResetExpiredJobsPageSize(1000);
			}
		}; 
	}
}
