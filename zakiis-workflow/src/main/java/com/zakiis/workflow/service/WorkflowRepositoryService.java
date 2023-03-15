package com.zakiis.workflow.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.List;
import java.util.stream.Collectors;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zakiis.workflow.dto.ProcessDefinitionDTO;
import com.zakiis.workflow.exception.WorkflowRuntimeException;
import com.zakiis.workflow.service.tool.WorkflowRepositoryTool;
import com.zakiis.workflow.util.IOUtil;

@Service
public class WorkflowRepositoryService {

	Logger log = LoggerFactory.getLogger(WorkflowRepositoryService.class);
	
	@Autowired
	RepositoryService repositoryService;

	/**
	 * deploy BPMN File to Activiti
	 * @param processDefinitionXML BPMN file content
	 * @return deployment id
	 */
	public String deployment(String processDefinitionXML) {
		String processDefinitionId = WorkflowRepositoryTool.getProcessDefinitionName(processDefinitionXML);
		Deployment deployment = repositoryService.createDeployment()
				.name(processDefinitionId)
				.addString(processDefinitionId + ".bpmn20.xml", processDefinitionXML)
				.deploy();
		log.info("create deployment success, deploymentId: {}", deployment.getId());
		return deployment.getId();
	}
	
	/**
	 * Retrieve process definition by key 
	 * @param processDefinitionKey id attribute of process node in BPMN XML file 
	 * @return Process definition
	 */
	public ProcessDefinitionDTO getProcessDefinitionByKey(String processDefinitionKey) {
		ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
				.processDefinitionKey(processDefinitionKey)
				.latestVersion()
				.singleResult();
		ProcessDefinitionDTO processDefinitionDTO = new ProcessDefinitionDTO();
		BeanUtils.copyProperties(processDefinition, processDefinitionDTO);
		return processDefinitionDTO;
	}
	
	public List<ProcessDefinitionDTO> latestVersionProcessDefinitions() {
		List<ProcessDefinition> processDefinitionList = repositoryService.createProcessDefinitionQuery()
				.latestVersion()
				.list();
		return processDefinitionList.stream()
				.map(pd -> {
					ProcessDefinitionDTO pdDTO = new ProcessDefinitionDTO();
					BeanUtils.copyProperties(pd, pdDTO);
					return pdDTO;
				})
				.collect(Collectors.toList());
	}
	
	public List<ProcessDefinitionDTO> allProcessDefinitions() {
		List<ProcessDefinition> processDefinitionList = repositoryService.createProcessDefinitionQuery()
				.list();
		return processDefinitionList.stream()
				.map(pd -> {
					ProcessDefinitionDTO pdDTO = new ProcessDefinitionDTO();
					BeanUtils.copyProperties(pd, pdDTO);
					return pdDTO;
				})
				.collect(Collectors.toList());
	}
	
	/**
	 * Get BPMN XML content, It's useful if you have an web edit page for workflow editing.
	 * @param processDefinitionKey id attribute of process node in BPMN XML file
	 * @return BPMN XML content
	 */
	public String getProcessDefinitionXML(String processDefinitionKey) {
		ProcessDefinitionDTO processDefinition = getProcessDefinitionByKey(processDefinitionKey);
		try (InputStream inputStream = repositoryService.getResourceAsStream(processDefinition.getDeploymentId(), processDefinition.getResourceName());
				InputStreamReader reader = new InputStreamReader(inputStream)) {
			return IOUtil.readAll(reader);
		} catch (IOException e) {
			throw new WorkflowRuntimeException("get process definition XML error", e);
		}
	}
	
	/**
	 * generate the diagram of this flow
	 * @param processDefinitionKey id attribute of process node in BPMN XML file 
	 * @param os output stream
	 */
	public void getProcessDefinitionDiagram(String processDefinitionKey, OutputStream os) {
		ProcessDefinitionDTO processDefinition = getProcessDefinitionByKey(processDefinitionKey);
		try (InputStream inputStream = repositoryService.getResourceAsStream(processDefinition.getDeploymentId(), processDefinition.getDiagramResourceName());
				InputStreamReader reader = new InputStreamReader(inputStream)) {
			IOUtil.copy(inputStream, os);
		} catch (IOException e) {
			throw new WorkflowRuntimeException("get process definition diagram error", e);
		}
	}
	
	public void deleteDeployment(String deploymentId, boolean cascade) {
		repositoryService.deleteDeployment(deploymentId, cascade);
		log.info("delete deployment {} success", deploymentId);
	}
	
}
