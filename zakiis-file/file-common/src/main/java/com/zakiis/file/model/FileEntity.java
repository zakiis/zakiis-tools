package com.zakiis.file.model;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.zakiis.file.domain.constants.FileAction;
import com.zakiis.file.domain.constants.FilePhase;
import com.zakiis.file.domain.constants.FileStatus;

/**
 * collection name different with model name can't create index automatically,
 * you should create index and collection of that fileEntity in create bucket method manually
 * The annotation of index would not work.
 * @author 10901
 */
@Document
@CompoundIndexes(
	@CompoundIndex(def = "{'currentPhase': 1, 'updateTime': 1}")
)
public class FileEntity {

	@Id
	private String fileKey;
	private String filePath;
	private Date createTime;
	private Date updateTime;
	/** file size of bytes */
	private long fileSize;
	private FileStatus status;
	private FilePhase currentPhase;
	private FilePhase desiredPhase;
	@Indexed(name = "idx_current_action")
	private FileAction currentAction;
	
	public String getFileKey() {
		return fileKey;
	}
	public void setFileKey(String fileKey) {
		this.fileKey = fileKey;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
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
	public long getFileSize() {
		return fileSize;
	}
	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}
	public FileStatus getStatus() {
		return status;
	}
	public void setStatus(FileStatus status) {
		this.status = status;
	}
	public FilePhase getCurrentPhase() {
		return currentPhase;
	}
	public void setCurrentPhase(FilePhase currentPhase) {
		this.currentPhase = currentPhase;
	}
	public FilePhase getDesiredPhase() {
		return desiredPhase;
	}
	public void setDesiredPhase(FilePhase desiredPhase) {
		this.desiredPhase = desiredPhase;
	}
	public FileAction getCurrentAction() {
		return currentAction;
	}
	public void setCurrentAction(FileAction currentAction) {
		this.currentAction = currentAction;
	}
	
}
