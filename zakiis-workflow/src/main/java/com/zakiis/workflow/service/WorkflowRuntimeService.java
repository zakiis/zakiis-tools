package com.zakiis.workflow.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowNode;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.bpmn.model.StartEvent;
import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.form.FormProperty;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.image.ProcessDiagramGenerator;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zakiis.workflow.dto.FlowElementDTO;
import com.zakiis.workflow.dto.FormPropertyDTO;
import com.zakiis.workflow.exception.WorkflowRuntimeException;
import com.zakiis.workflow.holder.ApplicationContextHolder;
import com.zakiis.workflow.service.tool.FormTool;
import com.zakiis.workflow.util.IOUtil;


@Service
public class WorkflowRuntimeService {

	Logger log = LoggerFactory.getLogger(WorkflowRuntimeService.class);
	
	@Autowired
	private RuntimeService runtimeService;
	@Autowired
	private HistoryService historyService;
	@Autowired
	private IdentityService identityService;
	@Autowired
	RepositoryService repositoryService;
	@Autowired
	FormService formService;
	@Autowired
	private ProcessEngine processEngine;
	
	/**
	 * start a new process instance
	 * @param processDefinitionKey id attribute of process node in BPMN XML file 
	 * @param businessKey business key
	 * @param initiator who should be assigned to this process instance, null if no one.
	 * @param params parameters that start a new process instance need.
	 * @return process instance id
	 */
	public String startProcess(String processDefinitionKey, String businessKey, String initiator, Map<String, Object> params) {
		if (StringUtils.isNotEmpty(initiator)) {
			identityService.setAuthenticatedUserId(initiator);
		}
		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDefinitionKey, businessKey, params);
		log.info("start process success, process definition key:{}, instance id:{}, business key:{}", processDefinitionKey, processInstance.getId(), businessKey);
		return processInstance.getId();
	}
	
	public List<FormPropertyDTO> queryStartFromData(String processDefinitionKey) {
		WorkflowRepositoryService workflowRepositoryService = ApplicationContextHolder.get().getBean(WorkflowRepositoryService.class);
		String processDefinitionId = workflowRepositoryService.getProcessDefinitionByKey(processDefinitionKey).getId();
		List<FormProperty> formProperties = formService.getStartFormData(processDefinitionId).getFormProperties();
		return formProperties.stream()
				.map(FormTool::convertFromActivitiFormProperty)
				.collect(Collectors.toList());
	}
	
	public Map<String, Object> getVariables(String executionId) {
		return runtimeService.getVariables(executionId);
	}
	
	public FlowElementDTO getStartElement(String processDefinitionKey) {
		FlowElementDTO flowElement = new FlowElementDTO();
		WorkflowRepositoryService workflowRepositoryService = ApplicationContextHolder.get().getBean(WorkflowRepositoryService.class);
		String processDefinitionId = workflowRepositoryService.getProcessDefinitionByKey(processDefinitionKey).getId();
		Process process = repositoryService.getBpmnModel(processDefinitionId).getProcesses().get(0);
		StartEvent startEvent = process.findFlowElementsOfType(StartEvent.class).get(0);
		BeanUtils.copyProperties(startEvent, flowElement);
		return flowElement;
	}
	
	public void deleteProcessInstance(String businessKey, String deleteReason) {
		ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
				.processInstanceBusinessKey(businessKey)
				.singleResult();
		runtimeService.deleteProcessInstance(processInstance.getId(), deleteReason);
		log.info("process instance deleted, instance id:{}, business key:{}, delete reason:{}", processInstance.getId(), businessKey, deleteReason);
	}

	/**
	 * generate the diagram of current process instance
	 * @param businessKey
	 * @param output
	 */
	public void getProcessInstanceDiagram(String businessKey, OutputStream output) {
		HistoricProcessInstance processInstance = historyService.createHistoricProcessInstanceQuery()
				.processInstanceBusinessKey(businessKey)
				.singleResult();
		BpmnModel bpmnModel = repositoryService.getBpmnModel(processInstance.getProcessDefinitionId());
		
		List<HistoricActivityInstance> historicActivityInstances = historyService.createHistoricActivityInstanceQuery()
				.processInstanceId(processInstance.getId())
				.list();
		List<String> highlightedActivities = historicActivityInstances.stream()
			.map(HistoricActivityInstance::getActivityId)
			.collect(Collectors.toList());
		List<String> highlightedFlow = getHighlightFlow(bpmnModel, historicActivityInstances);
		ProcessDiagramGenerator diagramGenerator = processEngine.getProcessEngineConfiguration().getProcessDiagramGenerator();
		try (InputStream inputStream = diagramGenerator.generateDiagram(bpmnModel, "png", highlightedActivities, highlightedFlow)) {
			IOUtil.copy(inputStream, output);
		} catch (IOException e) {
			throw new WorkflowRuntimeException("get process instance diagram error", e);
		}
	}

	private List<String> getHighlightFlow(BpmnModel bpmnModel, List<HistoricActivityInstance> highlightedActivities) {
		List<String> highlightedFlows = new ArrayList<String>();
		Set<String> highlightedActivitySet = highlightedActivities.stream()
				.map(HistoricActivityInstance::getActivityId)
				.collect(Collectors.toSet());
		for (int i = 0; i < highlightedActivities.size(); i++) {
			if (highlightedActivities.get(i).getEndTime() == null) {
				continue;
			}
			FlowNode flowNode = (FlowNode)bpmnModel.getFlowElement(highlightedActivities.get(i).getActivityId());
			List<SequenceFlow> outgoingFlows = flowNode.getOutgoingFlows();
			for (SequenceFlow sequenceFlow : outgoingFlows) {
				if (highlightedActivitySet.contains(sequenceFlow.getTargetFlowElement().getId())) {
					highlightedFlows.add(sequenceFlow.getId());
				}
			}
		}
		return highlightedFlows;
	}

	public void setVariable(String executionId, String key, Object value) {
		runtimeService.setVariable(executionId, key, value);
	}
	
}
