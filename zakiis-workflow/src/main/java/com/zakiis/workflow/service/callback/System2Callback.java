package com.zakiis.workflow.service.callback;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zakiis.workflow.dto.TaskDTO;
import com.zakiis.workflow.service.WorkflowTaskService;

@Service
public class System2Callback {

	@Autowired
	WorkflowTaskService workflowTaskService;
	
	public void system2Callback(String businessKey, String responseData) {
		System.out.println("system2 callback, businessKey:" + businessKey + ", response data:" + responseData);
		TaskDTO task = workflowTaskService.getTask(businessKey);
		workflowTaskService.completeTask(task.getId(), null);
	}
}
