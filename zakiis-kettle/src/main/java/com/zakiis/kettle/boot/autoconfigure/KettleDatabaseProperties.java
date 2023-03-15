package com.zakiis.kettle.boot.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "kettle.database")
public class KettleDatabaseProperties {

	private String name;
	/** mysql */
	private String type;
	/** jdbc */
	private String accessProtocol;
	/** hostname or ip address */
	private String host;
	private String port;
	private String username;
	private String password;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getAccessProtocol() {
		return accessProtocol;
	}
	public void setAccessProtocol(String accessProtocol) {
		this.accessProtocol = accessProtocol;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
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
