package com.zakiis.file.core.exception;

import com.zakiis.file.exception.IError;

public enum ErrorEnum implements IError {
	
	FIELD_REQUIRE_NOT_EMPTY("100001", "%s can't be empty"),
	FILE_NOT_EXISTS("100002", "File not exists"),
	WRITE_FILE_FAILED("100003", "Write file failed"),
	READ_FILE_FAILED("100004", "Read file failed"),
	FILE_CANT_CHANGED("100005", "File can't be changed"),
	FILE_EMPTY("100006", "File can't be empty"),
	FILE_NOT_READY("100007", "File not ready"),
	;
	String code;
	String message;
	
	private ErrorEnum(String code, String message) {
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
