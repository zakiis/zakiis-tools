package com.zakiis.file.portal.domain.dto.channel;

import java.io.Serializable;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zakiis.file.portal.domain.constants.FunctionCode;

public class ChannelUpdateDTO implements Serializable {

	private static final long serialVersionUID = -3496787002149659244L;
	
	@JsonIgnore
	private String ak;
	/** 0:inactive, 1:active */
	private Integer status;
	private Set<FunctionCode> functions;
	public String getAk() {
		return ak;
	}
	public void setAk(String ak) {
		this.ak = ak;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public Set<FunctionCode> getFunctions() {
		return functions;
	}
	public void setFunctions(Set<FunctionCode> functions) {
		this.functions = functions;
	}
}
