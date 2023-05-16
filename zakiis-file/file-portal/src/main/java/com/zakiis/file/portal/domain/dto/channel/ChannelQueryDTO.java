package com.zakiis.file.portal.domain.dto.channel;

import com.zakiis.file.domain.dto.PageRequest;

public class ChannelQueryDTO implements PageRequest {

	private static final long serialVersionUID = 3342207944254084683L;
	
	private String ak;
	private String name;
	private Integer status;
	private Long pageIndex;
	private Integer pageSize;
	
	public String getAk() {
		return ak;
	}
	public void setAk(String ak) {
		this.ak = ak;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
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
