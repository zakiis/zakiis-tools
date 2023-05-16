package com.zakiis.file.exception;

public class ServiceError extends RuntimeException {
	
	private static final long serialVersionUID = 3631615000859900998L;

	private String code;

	public ServiceError() {
		super();
	}

	public ServiceError(IError error) {
		super(error.getMessage());
		this.code = error.getCode();
	}
	
	public ServiceError(IError error, Object[] params) {
		super(String.format(error.getMessage(), params));
		this.code = error.getCode();
	}
	
	public ServiceError(IError error, Throwable cause) {
		super(error.getMessage(), cause);
		this.code = error.getCode();
	}
	
	public String getCode() {
		return code;
	}
}
