package com.zakiis.workflow.service.task;

import org.activiti.engine.delegate.BpmnError;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

import com.zakiis.workflow.holder.ApplicationContextHolder;
import com.zakiis.workflow.service.WorkflowRuntimeService;

public class CallSystem1Task implements JavaDelegate {
	
	@Override
	public void execute(DelegateExecution execution) {
		System.out.println("call system 1 task start");
		String businessKey = execution.getProcessInstanceBusinessKey();
		System.out.println("search standard request data from db using business key:" + businessKey);
		System.out.println("call system 1 task done");
		
		WorkflowRuntimeService workflowRuntimeService = ApplicationContextHolder.get().getBean(WorkflowRuntimeService.class);
		workflowRuntimeService.setVariable(execution.getId(), "reason", "call system1 got xxx exception");
		throw new BpmnError("10001", "call system1 got xxx excpetion");
	}

}
