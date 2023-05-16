package com.zakiis.file.portal.domain.dto.user;

import java.io.Serializable;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zakiis.file.portal.domain.constants.FunctionCode;

public class UserUpdateDTO implements Serializable {

	private static final long serialVersionUID = -6370527472759985620L;

	@JsonIgnore
	String username;
	String password;
	/** system function code */
	Set<FunctionCode> functions;
	
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Set<FunctionCode> getFunctions() {
		return functions;
	}
	public void setFunctions(Set<FunctionCode> functions) {
		this.functions = functions;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
}
