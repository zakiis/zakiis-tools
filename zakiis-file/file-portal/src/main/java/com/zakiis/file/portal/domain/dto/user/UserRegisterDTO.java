package com.zakiis.file.portal.domain.dto.user;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class UserRegisterDTO implements Serializable {

	private static final long serialVersionUID = -2851617309389790256L;
	
	@JsonIgnore
	String username;
	String password;
	
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
