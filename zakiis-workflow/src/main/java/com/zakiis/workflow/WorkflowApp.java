package com.zakiis.workflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = {org.activiti.spring.boot.SecurityAutoConfiguration.class})
public class WorkflowApp {
	
	public static void main(String[] args) {
		SpringApplication.run(WorkflowApp.class, args);
	}
}
