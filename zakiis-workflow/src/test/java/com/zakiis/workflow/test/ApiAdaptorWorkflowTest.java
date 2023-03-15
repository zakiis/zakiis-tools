package com.zakiis.workflow.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.zakiis.workflow.dto.FormPropertyDTO;
import com.zakiis.workflow.service.WorkflowRepositoryService;
import com.zakiis.workflow.service.WorkflowRuntimeService;
import com.zakiis.workflow.service.callback.System2Callback;
import com.zakiis.workflow.test.tool.FormTestTool;

@SpringBootTest
public class ApiAdaptorWorkflowTest {
	
	@Autowired
	WorkflowRepositoryService workflowRepositoryService;
	@Autowired
	WorkflowRuntimeService workflowRuntimeService;
	@Autowired
	System2Callback system2Callback;

	@Test
	public void test() throws IOException, InterruptedException {
		//deploy workflow definition
		byte[] definitionBytes = Files.readAllBytes(Paths.get("src/main/resources/bpmn/api-adaptor.bpmn20.xml"));
		workflowRepositoryService.deployment(new String(definitionBytes));
		//start workflow
		String processDefinitionKey = "cimb-api-adaptor";
		List<FormPropertyDTO> startFromData = workflowRuntimeService.queryStartFromData(processDefinitionKey);
		Map<String, Object> params = FormTestTool.inputParams(startFromData);
		String businessKey = UUID.randomUUID().toString();
		workflowRuntimeService.startProcess(processDefinitionKey, businessKey, null, params);
		//system2 callback
		Scanner scanner = new Scanner(System.in);
		Thread.sleep(2000L);
		System.out.print("please input system2 reponse data:");
		String system2Response = scanner.nextLine();
		system2Callback.system2Callback(businessKey, system2Response);
		scanner.close();
		//export workflow diagram
		FileOutputStream fos = new FileOutputStream(new File("target/" + businessKey + ".png"));
		workflowRuntimeService.getProcessInstanceDiagram(businessKey, fos);
		fos.close();
		
	}
	
	
}
