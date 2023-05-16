package com.zakiis.file.portal.exception;

import com.zakiis.file.exception.IError;

public enum ErrorEnum implements IError {
	
	FIELD_REQUIRE_NOT_EMPTY("000001", "%s can't be empty"),
	FIELD_REQUIRE_NOT_NULL("000002", "%s can't be null"),
	
	FILE_NOT_EXISTS("100002", "file not exists"),
	WRITE_FILE_FAILED("100003", "write file failed"),
	READ_FILE_FAILED("100004", "read file failed"),
	ARCHIVE_FILE_CANT_CHANGED("100005", "archive file can't be changed"),
	FILE_EMPTY("100006", "File can't be empty"),
	
	CHANNEL_NOT_EXISTS("200001", "channel not exists"),
	CHANNEL_REPEATED("200002", "channel repeated"),

	BUCKET_NOT_EXISTS("300001", "bucket not exists"),
	BUCKET_REPEATED("300002", "bucket repeated"),
	BUCKET_NAME_INVALID("300003", "Bucket name invalid, can consists of number, alphabet and _ only and can't start with number"),
	
	USER_EXISTS("400001", "User aleardy exists"),
	USER_NOT_EXISTS("400002", "User not exists"),
	INVALID_USERNAME_OR_PASSWORD("400003", "Invalid username or password"),
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
