package com.zakiis.workflow.service.task;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

public class CallSystem2Task implements JavaDelegate {

	@Override
	public void execute(DelegateExecution execution) {
		System.out.println("call system 2 task start");
		String businessKey = execution.getProcessInstanceBusinessKey();
		System.out.println("search standard request data from db using business key:" + businessKey);
		System.out.println("call system 2 task success, waitting callback......");
	}

}
