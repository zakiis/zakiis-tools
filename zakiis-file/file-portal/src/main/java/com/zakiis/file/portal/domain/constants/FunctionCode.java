package com.zakiis.file.portal.domain.constants;

public enum FunctionCode {

	/** Who has ADMIN function privileges means he can access any function */
	ADMIN,
	FILE_WRITE,
	FILE_DELETE,
	FILE_READ,
	BUCKET_QUERY,
	BUCKET_MANAGE,
	CHANNEL_QUERY,
	CHANNEL_MANAGE,
	UPDATE_USER,
	;
	public final static String ADMIN_VALUE = "ADMIN";
	public final static String FILE_WRITE_VALUE = "FILE_WRITE";
	public final static String FILE_DELETE_VALUE = "FILE_DELETE";
	public final static String FILE_READ_VALUE = "FILE_READ";

	public final static String BUCKET_QUERY_VALUE = "BUCKET_QUERY";
	public final static String BUCKET_MANAGE_VALUE = "BUCKET_MANAGE";

	public final static String CHANNEL_QUERY_VALUE = "CHANNEL_QUERY";
	public final static String CHANNEL_MANAGE_VALUE = "CHANNEL_MANAGE";
	
	public final static String UPDATE_USER_VALUE = "UPDATE_USER";
	
}
