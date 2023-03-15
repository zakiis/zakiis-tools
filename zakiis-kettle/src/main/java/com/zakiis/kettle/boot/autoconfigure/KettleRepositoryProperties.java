package com.zakiis.kettle.boot.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "kettle.repository")
public class KettleRepositoryProperties {

	/** repository's user name, initial value is admin */
	private String username;
	/** repository's password, initial value is admin */
	private String password;
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	
}
