package com.zakiis.workflow.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.zakiis.workflow.dto.FormPropertyDTO;
import com.zakiis.workflow.dto.FlowElementDTO;
import com.zakiis.workflow.service.WorkflowRuntimeService;
import com.zakiis.workflow.test.tool.FormTestTool;

@SpringBootTest
public class WorkflowRuntimeServiceTest {
	
	@Autowired
	WorkflowRuntimeService workflowRuntimeService;

	@Test
	public void testStartProcess() {
		String processDefinitionKey = "cimb-leave";
		FlowElementDTO startElement = workflowRuntimeService.getStartElement(processDefinitionKey);
		System.out.println(startElement.getDocumentation());
		List<FormPropertyDTO> startFromData = workflowRuntimeService.queryStartFromData(processDefinitionKey);
		Map<String, Object> params = FormTestTool.inputParams(startFromData);
		
		String businessKey = UUID.randomUUID().toString();
		workflowRuntimeService.startProcess("cimb-leave", businessKey, null, params);
	}
	
	@Test
	public void testGetProcessInstanceDiagram() throws IOException {
		String businessKey = "9bafc35a-3ec4-4553-b9ad-870ad534f982";
		FileOutputStream fos = new FileOutputStream(new File("target/" + businessKey + ".png"));
		workflowRuntimeService.getProcessInstanceDiagram(businessKey, fos);
		fos.close();
	}
	
	@Test
	public void testDeleteProcessInstance() {
		String businessKey = "7c466074-f06f-40b2-afc9-7131129d66a0";
		String deleteReason = "cancel apply";
		workflowRuntimeService.deleteProcessInstance(businessKey, deleteReason);
	}
	
}
