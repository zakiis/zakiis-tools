package com.zakiis.file.core.boot.autoconfigure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "file.core")
public class FileCoreProperties {

	/** hot path of file saved */
	private String hotPath;
	/** warm path of file saved */
	private String warmPath;
	/** cold path of file saved */
	private String coldPath;
	/** file entity in uploading status for a long time may failed in process, should clean these entities in mongodb */
	private Integer cleanUploadingFileEntityAfterSeconds;
	/** how long should file move into warm phase, calculate from create date. a nonpositive value means disable this phase */
	private Integer moveIntoWarmPhaseAfterDay = 0;
	/** how long should file move into cold phase, calculate from create date. a nonpositive value means disable this phase */
	private Integer moveIntoColdPhaseAfterDay = 0;
	
	public String getHotPath() {
		return hotPath;
	}
	public void setHotPath(String hotPath) {
		this.hotPath = hotPath;
	}
	public String getWarmPath() {
		return warmPath;
	}
	public void setWarmPath(String warmPath) {
		this.warmPath = warmPath;
	}
	public String getColdPath() {
		return coldPath;
	}
	public void setColdPath(String coldPath) {
		this.coldPath = coldPath;
	}
	public Integer getCleanUploadingFileEntityAfterSeconds() {
		return cleanUploadingFileEntityAfterSeconds;
	}
	public void setCleanUploadingFileEntityAfterSeconds(Integer cleanUploadingFileEntityAfterSeconds) {
		this.cleanUploadingFileEntityAfterSeconds = cleanUploadingFileEntityAfterSeconds;
	}
	public Integer getMoveIntoWarmPhaseAfterDay() {
		return moveIntoWarmPhaseAfterDay;
	}
	public void setMoveIntoWarmPhaseAfterDay(Integer moveIntoWarmPhaseAfterDay) {
		this.moveIntoWarmPhaseAfterDay = moveIntoWarmPhaseAfterDay;
	}
	public Integer getMoveIntoColdPhaseAfterDay() {
		return moveIntoColdPhaseAfterDay;
	}
	public void setMoveIntoColdPhaseAfterDay(Integer moveIntoColdPhaseAfterDay) {
		this.moveIntoColdPhaseAfterDay = moveIntoColdPhaseAfterDay;
	}

}
