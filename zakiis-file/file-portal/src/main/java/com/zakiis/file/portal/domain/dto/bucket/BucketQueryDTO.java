package com.zakiis.file.portal.domain.dto.bucket;

import com.zakiis.file.domain.constants.AccessMode;
import com.zakiis.file.domain.dto.PageRequest;

public class BucketQueryDTO implements PageRequest {

	private static final long serialVersionUID = 8093409815736290749L;

	String name;
	String description;
	AccessMode accessMode;
	Long pageIndex;
	Integer pageSize;
	
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
	public Long getPageIndex() {
		return pageIndex;
	}
	public void setPageIndex(Long pageIndex) {
		this.pageIndex = pageIndex;
	}
	public Integer getPageSize() {
		return pageSize;
	}
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}
	
}
