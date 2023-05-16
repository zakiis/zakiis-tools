package com.zakiis.file.exception;


public enum CommonError implements IError {
	UNAUTHORIZED("401", "UNAUTHORIZED"),
	UNKNOWN_ERROR("999999", "System busy, please try it later"),
	;
	String code;
	String message;
	
	private CommonError(String code, String message) {
		this.code = code;
		this.message = message;
	}
	
	@Override
	public String getCode() {
		return code;
	}

	@Override
	public String getMessage() {
		return message;
	}
}