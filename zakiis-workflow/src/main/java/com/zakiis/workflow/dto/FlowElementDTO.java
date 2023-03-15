package com.zakiis.workflow.dto;

import java.io.Serializable;
import java.util.List;

public class FlowElementDTO implements Serializable {

	private static final long serialVersionUID = 270784097816892783L;

	String id;
	String name;
	String documentation;
	List<String> candidateStarterUsers;
	List<String> candidateStarterGroups;
	
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
	public String getDocumentation() {
		return documentation;
	}
	public void setDocumentation(String documentation) {
		this.documentation = documentation;
	}
	public List<String> getCandidateStarterUsers() {
		return candidateStarterUsers;
	}
	public void setCandidateStarterUsers(List<String> candidateStarterUsers) {
		this.candidateStarterUsers = candidateStarterUsers;
	}
	public List<String> getCandidateStarterGroups() {
		return candidateStarterGroups;
	}
	public void setCandidateStarterGroups(List<String> candidateStarterGroups) {
		this.candidateStarterGroups = candidateStarterGroups;
	}
	
}
