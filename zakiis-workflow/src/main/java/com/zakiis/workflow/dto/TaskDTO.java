package com.zakiis.workflow.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

public class TaskDTO implements Serializable {

	private static final long serialVersionUID = -454972138032362742L;
	
	/** DB id of the task. */
	String id;
	/** Name or title of the task. */
	String name;
	/** description of the task */
	String description;
	/** indication of how important/urgent this task is */
	int priority;
	/** the person that is responsible for this task */
	String owner;
	/** the person to which this task is delegated */
	String assignee;
	/** Reference to the path of execution */
	String executionId;
	/** Reference to the process instance */
	String processInstanceId;
	/** Reference to the process definition */
	String processDefinitionId;
	/** The claim time of this task */
	Date claimTime;
	/** due date of the task */
	Date dueDate;
	/** form key of the task*/
	String formKey;
	/** Indicates whether this task is suspended or not*/
	boolean suspended;
	/** all variables on current scope */
	private Map<String, Object> variables;
	
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
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getPriority() {
		return priority;
	}
	public void setPriority(int priority) {
		this.priority = priority;
	}
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	public String getAssignee() {
		return assignee;
	}
	public void setAssignee(String assignee) {
		this.assignee = assignee;
	}
	public String getExecutionId() {
		return executionId;
	}
	public void setExecutionId(String executionId) {
		this.executionId = executionId;
	}
	public String getProcessInstanceId() {
		return processInstanceId;
	}
	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}
	public String getProcessDefinitionId() {
		return processDefinitionId;
	}
	public void setProcessDefinitionId(String processDefinitionId) {
		this.processDefinitionId = processDefinitionId;
	}
	public Date getClaimTime() {
		return claimTime;
	}
	public void setClaimTime(Date claimTime) {
		this.claimTime = claimTime;
	}
	public Date getDueDate() {
		return dueDate;
	}
	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}
	public String getFormKey() {
		return formKey;
	}
	public void setFormKey(String formKey) {
		this.formKey = formKey;
	}
	public boolean isSuspended() {
		return suspended;
	}
	public void setSuspended(boolean suspended) {
		this.suspended = suspended;
	}
	public Map<String, Object> getVariables() {
		return variables;
	}
	public void setVariables(Map<String, Object> variables) {
		this.variables = variables;
	}
	
}
