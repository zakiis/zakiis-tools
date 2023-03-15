package com.zakiis.keycloak.sdk.exception;

public class HttpException extends KeyCloakSDKException {
	
	private static final long serialVersionUID = -6422753751862206430L;
	int code;
	
	public HttpException() {
		super();
	}
	public HttpException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
	public HttpException(String message, Throwable cause) {
		super(message, cause);
	}
	public HttpException(String message) {
		super(message);
	}
	public HttpException(int code, String message) {
		super(message);
		this.code = code;
	}
	public HttpException(Throwable cause) {
		super(cause);
	}
	
}
