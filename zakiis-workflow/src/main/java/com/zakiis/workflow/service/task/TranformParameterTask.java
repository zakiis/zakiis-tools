package com.zakiis.workflow.service.task;

import java.util.Map;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

import com.zakiis.workflow.holder.ApplicationContextHolder;
import com.zakiis.workflow.service.WorkflowRuntimeService;

public class TranformParameterTask implements JavaDelegate {

	@Override
	public void execute(DelegateExecution execution) {
		System.out.println("transform parameter task start");
		WorkflowRuntimeService workflowRuntimeService = ApplicationContextHolder.get().getBean(WorkflowRuntimeService.class);
		String executionId = execution.getId();
		Map<String, Object> params = workflowRuntimeService.getVariables(executionId);
		System.out.println("transform parameter task done, request body: "  + params.get("requestBody"));
	}

}
