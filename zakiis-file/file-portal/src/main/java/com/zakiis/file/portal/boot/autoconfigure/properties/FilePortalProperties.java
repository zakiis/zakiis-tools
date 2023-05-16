package com.zakiis.file.portal.boot.autoconfigure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "file.portal")
public class FilePortalProperties {

	/** file core url */
	String fileCoreUrl;
	/** AES256 secret key for password encrypt */
	private String aesSecret;
	/** HMAC-SHA256 secret key in HEX string for JWT token generate*/
	String jwtSecret;
	/** session timeout in milliseconds */
	Long sessionTimeoutMills;
	/** for debug purpose need skip authentication */
	private boolean skipAuth = false;

	public String getFileCoreUrl() {
		return fileCoreUrl;
	}

	public void setFileCoreUrl(String fileCoreUrl) {
		this.fileCoreUrl = fileCoreUrl;
	}

	public Long getSessionTimeoutMills() {
		return sessionTimeoutMills;
	}

	public void setSessionTimeoutMills(Long sessionTimeoutMills) {
		this.sessionTimeoutMills = sessionTimeoutMills;
	}

	public String getJwtSecret() {
		return jwtSecret;
	}

	public void setJwtSecret(String jwtSecret) {
		this.jwtSecret = jwtSecret;
	}

	public String getAesSecret() {
		return aesSecret;
	}

	public void setAesSecret(String aesSecret) {
		this.aesSecret = aesSecret;
	}

	public boolean isSkipAuth() {
		return skipAuth;
	}

	public void setSkipAuth(boolean skipAuth) {
		this.skipAuth = skipAuth;
	}

	
}
