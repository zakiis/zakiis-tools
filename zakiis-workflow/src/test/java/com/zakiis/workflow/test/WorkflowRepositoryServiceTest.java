package com.zakiis.workflow.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.zakiis.workflow.dto.ProcessDefinitionDTO;
import com.zakiis.workflow.service.WorkflowRepositoryService;
import com.zakiis.workflow.util.JsonUtil;

@SpringBootTest
public class WorkflowRepositoryServiceTest {
	
	@Autowired
	WorkflowRepositoryService workflowRepositoryService;
	
	@Test
	public void testDeploy() throws IOException {
		byte[] definitionBytes = Files.readAllBytes(Paths.get("src/main/resources/bpmn/leave.bpmn20.xml"));
		workflowRepositoryService.deployment(new String(definitionBytes));
	}
	
	@Test
	public void testLatestVersionProcessDefinitions() {
		List<ProcessDefinitionDTO> processDefinitions = workflowRepositoryService.latestVersionProcessDefinitions();
		System.out.println(JsonUtil.toJson(processDefinitions));
	}
	
	@Test
	public void testDeleteDeploy() {
		List<ProcessDefinitionDTO> processDefinitions = workflowRepositoryService.allProcessDefinitions();
		processDefinitions.forEach(pd -> workflowRepositoryService.deleteDeployment(pd.getDeploymentId(), true));
	}
	
	@Test
	public void testGetProcessDefinitionXML() {
		String processDefinitionXML = workflowRepositoryService.getProcessDefinitionXML("cimb-leave");
		System.out.println(processDefinitionXML);
	}
	
	@Test
	public void testGetProcessDefinitionDiagram() throws IOException {
		String processDefinitionKey = "cimb-leave";
		FileOutputStream fos = new FileOutputStream(new File("target/" + processDefinitionKey + ".jpg"));
		workflowRepositoryService.getProcessDefinitionDiagram(processDefinitionKey, fos);
		fos.close();
	}

}
