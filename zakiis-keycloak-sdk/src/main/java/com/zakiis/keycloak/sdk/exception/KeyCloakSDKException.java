package com.zakiis.keycloak.sdk.exception;

public class KeyCloakSDKException extends RuntimeException {

	private static final long serialVersionUID = 459234911416265874L;

	public KeyCloakSDKException() {
		super();
	}

	public KeyCloakSDKException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public KeyCloakSDKException(String message, Throwable cause) {
		super(message, cause);
	}

	public KeyCloakSDKException(String message) {
		super(message);
	}

	public KeyCloakSDKException(Throwable cause) {
		super(cause);
	}

	
}
