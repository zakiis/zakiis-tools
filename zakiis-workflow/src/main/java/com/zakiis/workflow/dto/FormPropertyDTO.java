package com.zakiis.workflow.dto;

import java.io.Serializable;

import com.zakiis.workflow.dto.form.FormType;

public class FormPropertyDTO implements Serializable {

	private static final long serialVersionUID = 2995074379895831017L;

	String id;
	String name;
	boolean isRequired;
	boolean isReadable;
	boolean isWriteable;
	String value;
	FormType type;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isRequired() {
		return isRequired;
	}
	public void setRequired(boolean isRequired) {
		this.isRequired = isRequired;
	}
	public boolean isReadable() {
		return isReadable;
	}
	public void setReadable(boolean isReadable) {
		this.isReadable = isReadable;
	}
	public boolean isWriteable() {
		return isWriteable;
	}
	public void setWriteable(boolean isWriteable) {
		this.isWriteable = isWriteable;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public FormType getType() {
		return type;
	}
	public void setType(FormType type) {
		this.type = type;
	}
	
}
