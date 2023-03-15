package com.zakiis.workflow.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.activiti.engine.FormService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.form.FormProperty;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zakiis.workflow.dto.FormPropertyDTO;
import com.zakiis.workflow.dto.TaskDTO;
import com.zakiis.workflow.service.tool.FormTool;

@Service
public class WorkflowTaskService {

	@Autowired
	TaskService taskService;
	@Autowired
	FormService formService;
	@Autowired
	RuntimeService runtimeService;
	
	public TaskDTO getTask(String businessKey) {
		Task task = taskService.createTaskQuery()
					.processInstanceBusinessKeyLikeIgnoreCase(businessKey)
					.singleResult();
		TaskDTO taskDTO = new TaskDTO();
		BeanUtils.copyProperties(task, taskDTO, "variables");
		Map<String, Object> variables = runtimeService.getVariables(task.getExecutionId());
		taskDTO.setVariables(variables);
		return taskDTO;
	}
	
	public List<TaskDTO> queryTask(String candidateGroup, String assignee) {
		TaskQuery taskQuery = taskService.createTaskQuery();
		if (StringUtils.isNotBlank(candidateGroup)) {
			taskQuery.taskCandidateGroup(candidateGroup);
		}
		if (StringUtils.isNotBlank(assignee)) {
			taskQuery.taskAssignee(assignee);
		}
		List<Task> taskList = taskQuery.list();
		return taskList.stream()
				.map(task -> {
					TaskDTO taskDTO = new TaskDTO();
					BeanUtils.copyProperties(task, taskDTO, "variables");
					return taskDTO;
				})
				.collect(Collectors.toList());
	}
	
	public void claimTask(String taskId, String userId) {
		taskService.claim(taskId, userId);
	}
	
	/**
	 * complete task
	 * @param taskid current task id
	 * @param params parameters that task need
	 */
	public void completeTask(String taskid, Map<String, Object> params) {
		taskService.complete(taskid, params);
	}
	
	public List<FormPropertyDTO> queryTaskFormProperty(String taskId) {
		List<FormProperty> formProperties = formService.getTaskFormData(taskId).getFormProperties();
		List<FormPropertyDTO> formPropertyDTOs = formProperties.stream()
				.map(FormTool::convertFromActivitiFormProperty)
				.collect(Collectors.toList());
		return formPropertyDTOs;
	}
}
