package com.zakiis.workflow.service.task;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

import com.zakiis.workflow.holder.ApplicationContextHolder;
import com.zakiis.workflow.service.WorkflowRuntimeService;

public class CancelProcessTask implements JavaDelegate {

	@Override
	public void execute(DelegateExecution execution) {
		System.out.println("Cancel process task start");
		String businessKey = execution.getProcessInstanceBusinessKey();
		WorkflowRuntimeService workflowRuntimeService = ApplicationContextHolder.get().getBean(WorkflowRuntimeService.class);
		String reason = (String)workflowRuntimeService.getVariables(execution.getId()).get("reason");
		workflowRuntimeService.deleteProcessInstance(businessKey, reason);
		System.out.println("Cancel process task end, business key:" + businessKey);
	}

}
