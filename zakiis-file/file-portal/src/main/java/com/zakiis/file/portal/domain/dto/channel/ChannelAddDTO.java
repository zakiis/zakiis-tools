package com.zakiis.file.portal.domain.dto.channel;

import java.io.Serializable;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zakiis.file.portal.domain.constants.FunctionCode;

public class ChannelAddDTO implements Serializable {

	private static final long serialVersionUID = -2730128427834273401L;

	@JsonIgnore
	private String name;
	private Set<FunctionCode> functions;

	public Set<FunctionCode> getFunctions() {
		return functions;
	}
	public void setFunctions(Set<FunctionCode> functions) {
		this.functions = functions;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
