package com.zakiis.file.core.service.tool;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Date;
import java.util.Set;

import com.zakiis.file.core.exception.ErrorEnum;
import com.zakiis.file.domain.constants.FileAction;
import com.zakiis.file.domain.constants.FilePhase;
import com.zakiis.file.domain.constants.FileStatus;
import com.zakiis.file.exception.ServiceException;
import com.zakiis.file.model.FileEntity;

public abstract class FileTool {
	
	private final static FileAttribute<Set<PosixFilePermission>> fileAttributes;
	private static final boolean isPosix =
		    FileSystems.getDefault().supportedFileAttributeViews().contains("posix");
	
	static {
		Set<PosixFilePermission> filePermissions = PosixFilePermissions.fromString("rwxr-----");
		fileAttributes = PosixFilePermissions.asFileAttribute(filePermissions);
	}
	
	public static Path createFile(String fileFullName) {
		int index = fileFullName.lastIndexOf("/");
		String directory = fileFullName.substring(0, index);
		String filename = fileFullName.substring(index);
		return createFile(directory, filename);
	}

	public static Path createFile(String directory, String fileName) {
		Path filePath = null;
		try {
			Path directoryPath = Paths.get(directory);
			if (!Files.exists(directoryPath)) {
				//TODO add global lock
				if (isPosix) {
					Files.createDirectories(directoryPath, fileAttributes);
				} else {
					Files.createDirectories(directoryPath);
				}
			}
			filePath = Paths.get(directory, fileName);
			if (!Files.exists(filePath)) {
				if (isPosix) {
					Files.createFile(filePath, fileAttributes);
				} else {
					Files.createFile(filePath);
				}
			}
		} catch (IOException e) {
			throw new ServiceException(ErrorEnum.WRITE_FILE_FAILED, e);
		}
		return filePath;
	}
	
	public static FileEntity buildFileEntity(String fileKey, String filePath) {
		FileEntity fileEntity = new FileEntity();
		fileEntity.setFileKey(fileKey);
		fileEntity.setFilePath(filePath.toString());
		fileEntity.setStatus(FileStatus.INACTIVE);
		fileEntity.setCurrentPhase(FilePhase.HOT);
		fileEntity.setDesiredPhase(FilePhase.HOT);
		fileEntity.setCurrentAction(FileAction.UPLOADING);
		fileEntity.setCreateTime(new Date());
		fileEntity.setUpdateTime(new Date());
		return fileEntity;
		
	}
	
}
