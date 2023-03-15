package com.zakiis.workflow.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.zakiis.workflow.dto.FormPropertyDTO;
import com.zakiis.workflow.dto.TaskDTO;
import com.zakiis.workflow.service.WorkflowRuntimeService;
import com.zakiis.workflow.service.WorkflowTaskService;
import com.zakiis.workflow.test.tool.FormTestTool;
import com.zakiis.workflow.util.JsonUtil;

@SpringBootTest
public class WorkflowTaskServiceTest {

	@Autowired
	WorkflowTaskService workflowTaskService;
	@Autowired
	WorkflowRuntimeService workflowProcessService;
	
	@Test
	public void testCompleteTask() throws IOException {
		String businessKey = "676fb688-2fee-439b-b0ea-bf1088a3c650";
		TaskDTO task = workflowTaskService.getTask(businessKey);
		String description = fillParams(task.getDescription(), task.getVariables());
		System.out.println(description);
		
		List<FormPropertyDTO> formProperties = workflowTaskService.queryTaskFormProperty(task.getId());
		Map<String, Object> params = FormTestTool.inputParams(formProperties);
		workflowTaskService.completeTask(task.getId(), params);
		
		FileOutputStream fos = new FileOutputStream(new File("target/" + businessKey + ".png"));
		workflowProcessService.getProcessInstanceDiagram(businessKey, fos);
		fos.close();
	}
	
	@Test
	public void testClaimTask() {
		String user = "zhangsan";
		String group = "manager";
		List<TaskDTO> userTasks = workflowTaskService.queryTask(null, user);
		System.out.println("=========" + user + " task pool=========");
		System.out.println(JsonUtil.toJson(userTasks));
		List<TaskDTO> managerTasks = workflowTaskService.queryTask("manager", null);
		System.out.println("=========group " + group + " task pool=========");
		System.out.println(JsonUtil.toJson(managerTasks));
		System.out.print("input task id that you want to assign to " + user + ":");
		Scanner scanner = new Scanner(System.in);
		String taskId = scanner.next();
		workflowTaskService.claimTask(taskId, user);
		System.out.println("=========" + user + " task pool=========");
		userTasks = workflowTaskService.queryTask(null, user);
		System.out.println(JsonUtil.toJson(userTasks));
		scanner.close();
	}

	private String fillParams(String description, Map<String, Object> variables) {
		Pattern pattern = Pattern.compile("\\$\\{([^}]+)\\}");
		Matcher matcher = pattern.matcher(description);
		int index = 0;
		StringBuilder builder = new StringBuilder();
		while (matcher.find()) {
			builder.append(description.substring(index, matcher.start()));
			builder.append(variables.get(matcher.group(1)));
			index = matcher.end();
		}
		if (index < description.length() - 1) {
			builder.append(description.substring(index));
		}
		return builder.toString();
	}
}
