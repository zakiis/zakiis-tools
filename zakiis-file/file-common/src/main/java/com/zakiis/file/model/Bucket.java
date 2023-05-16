package com.zakiis.file.model;

import java.util.Date;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.zakiis.file.domain.constants.AccessMode;
import com.zakiis.file.model.inner.Access;

@Document
public class Bucket {

	@Id
	String name;
	@Indexed(name = "idx_description")
	String description;
	@Indexed(name = "idx_access_mode")
	AccessMode accessMode;
	String createdBy;
	Date createTime;
	String updatedBy;
	Date updateTime;
	/** key is ak */
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
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	public Map<String, Access> getAccess() {
		return access;
	}
	public void setAccess(Map<String, Access> access) {
		this.access = access;
	}
	public AccessMode getAccessMode() {
		return accessMode;
	}
	public void setAccessMode(AccessMode accessMode) {
		this.accessMode = accessMode;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public String getUpdatedBy() {
		return updatedBy;
	}
	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}
	
}
