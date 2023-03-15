package com.zakiis.keycloak.sdk.exception;

public class InvalidTokenException extends KeyCloakSDKException {

	private static final long serialVersionUID = -1119419942096429361L;

	public InvalidTokenException() {
		super();
	}

	public InvalidTokenException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public InvalidTokenException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidTokenException(String message) {
		super(message);
	}

	public InvalidTokenException(Throwable cause) {
		super(cause);
	}
	
}
