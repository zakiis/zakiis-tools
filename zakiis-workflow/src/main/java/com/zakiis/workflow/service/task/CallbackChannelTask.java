package com.zakiis.workflow.service.task;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

public class CallbackChannelTask implements JavaDelegate {

	@Override
	public void execute(DelegateExecution execution) {
		String businessKey = execution.getProcessInstanceBusinessKey();
		System.out.println("build channel callback data from db using business key:" + businessKey);
		System.out.println("callback channel done");
	}

}
