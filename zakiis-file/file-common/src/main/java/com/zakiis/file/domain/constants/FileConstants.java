package com.zakiis.file.domain.constants;

public interface FileConstants {
	
	/** params: 1-bucket 2-file key*/
	String uploadFileURL = "/v1/file-core/file/upload/%s/%s";
	/** params: 1-bucket 2-file key*/
	String downloadFileURL = "/v1/file-core/file/download/%s/%s";
}
