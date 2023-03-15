package com.zakiis.workflow.exception;

public class WorkflowRuntimeException extends RuntimeException {

	private static final long serialVersionUID = 6847233990347553274L;

	public WorkflowRuntimeException() {
		super();
	}

	public WorkflowRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

	public WorkflowRuntimeException(String message) {
		super(message);
	}

	public WorkflowRuntimeException(Throwable cause) {
		super(cause);
	}

}
