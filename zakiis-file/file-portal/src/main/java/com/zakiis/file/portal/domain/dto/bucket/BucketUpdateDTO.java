package com.zakiis.file.portal.domain.dto.bucket;

import java.io.Serializable;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zakiis.file.domain.constants.AccessMode;
import com.zakiis.file.model.inner.Access;

public class BucketUpdateDTO implements Serializable {

	private static final long serialVersionUID = -7983019822417780120L;

	@JsonIgnore
	String name;
	String description;
	AccessMode accessMode;
	/** key is channel AK */
	Map<String, Access> access;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public AccessMode getAccessMode() {
		return accessMode;
	}
	public void setAccessMode(AccessMode accessMode) {
		this.accessMode = accessMode;
	}
	public Map<String, Access> getAccess() {
		return access;
	}
	public void setAccess(Map<String, Access> access) {
		this.access = access;
	}
	
}
