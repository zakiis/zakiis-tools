package com.zakiis.file.exception;

public class ServiceException extends RuntimeException {

	private static final long serialVersionUID = -8870095301337490197L;
	
	private String code;

	public ServiceException() {
		super();
	}

	public ServiceException(IError error) {
		super(error.getMessage());
		this.code = error.getCode();
	}
	
	public ServiceException(IError error, Object[] params) {
		super(String.format(error.getMessage(), params));
		this.code = error.getCode();
	}
	
	public ServiceException(IError error, Throwable cause) {
		super(error.getMessage(), cause);
		this.code = error.getCode();
	}
	
	public ServiceException(String code, String message) {
		super(message);
		this.code = code;
	}
	
	public String getCode() {
		return code;
	}

	
}
