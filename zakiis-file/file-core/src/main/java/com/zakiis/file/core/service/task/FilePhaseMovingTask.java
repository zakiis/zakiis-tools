package com.zakiis.file.core.service.task;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.zakiis.file.core.boot.autoconfigure.properties.FileCoreProperties;
import com.zakiis.file.core.service.tool.FileTool;
import com.zakiis.file.domain.constants.FileAction;
import com.zakiis.file.domain.constants.FilePhase;
import com.zakiis.file.model.Bucket;
import com.zakiis.file.model.FileEntity;

import reactor.core.publisher.Mono;

/**
 * Moving file to the desired phase. This method should be scheduled regularly
 * Moving flow: init -> new file copied -> file entity path changed -> old file deleted -> complete. {@link FileAction}
 * File moving task will redo from last success flow node if interrupted.
 * This task process the file entity which file action field value is @ {@link FileAction#PHASE_MOVING_INIT} that changed by {@link FilePhaseTransformTask#transformPhase()}
 * @author 10901
 */
@Service
public class FilePhaseMovingTask {

	Logger log = LoggerFactory.getLogger(FilePhaseTransformTask.class);
	
	@Autowired
	FileCoreProperties fileCoreProperties;
	@Autowired
	ReactiveMongoTemplate mongoTemplate;
	int batchSize = 100;
	
	/**
	 * Entrance of file phase moving task.
	 * @return
	 */
	public Mono<Long> movingPhase() {
		log.info("File phase moving task start");
		return mongoTemplate.findAll(Bucket.class)
				.flatMap(bucket -> movingPhase(bucket.getName()))
				.collect(Collectors.summingLong(t -> t))
				.doOnSuccess(movingCount -> log.info("File phase moving task done, moving count: {}", movingCount));
	}
	
	/**
	 * File phase moving task by bucket name.
	 * @param bucketName
	 * @return
	 */
	private Mono<Long> movingPhase(String bucketName) {
		Query query = Query.query(Criteria.where("currentAction").in(FileAction.phaseMovingAction()));
		return mongoTemplate.count(query, FileEntity.class, bucketName)
				.flatMap(count -> {
					Mono<Long> mono = Mono.just(0L);
					for (long skip = 0; skip < count; skip += batchSize) {
						Mono<Long> processedCount = mongoTemplate.find(query.limit(batchSize).skip(skip), FileEntity.class, bucketName)
								.flatMap(fileEntity -> movingPhase(fileEntity, bucketName))
								.count();
						mono = mono.zipWith(processedCount).map(tuple -> tuple.getT1() + tuple.getT2());
					}
					return mono;
				});
		
	}
	
	/**
	 * file phase moving task by file entity
	 * @param fileEntity
	 * @param bucketName
	 * @return
	 */
	private Mono<Object> movingPhase(FileEntity fileEntity, String bucketName) {
		try {
			if (FileAction.PHASE_MOVING_INIT.equals(fileEntity.getCurrentAction())) {
				return copyToNewFile(fileEntity, bucketName)
						.then(changeFilePath(fileEntity, bucketName))
						.then(deleteOldFile(fileEntity, bucketName))
						.then(completeMoving(fileEntity, bucketName));
			} else if (FileAction.PHASE_MOVING_NEW_FILE_COPIED.equals(fileEntity.getCurrentAction())) {
				return changeFilePath(fileEntity, bucketName)
						.then(deleteOldFile(fileEntity, bucketName))
						.then(completeMoving(fileEntity, bucketName));
			} else if (FileAction.PHASE_MOVING_PATH_CHANGED.equals(fileEntity.getCurrentAction())) {
				return deleteOldFile(fileEntity, bucketName)
						.then(completeMoving(fileEntity, bucketName));
			} else if (FileAction.PHASE_MOVING_OLD_FILE_DELETED.equals(fileEntity.getCurrentAction())) {
				return completeMoving(fileEntity, bucketName);
			}
		} catch (Throwable e) {
			log.error("moving file phase task got an exception, bucket: {}, file key: {}", bucketName, fileEntity.getFileKey());
		}
		return Mono.empty();
	}
	
	/**
	 * Copy files from old path to new path, {@link FileAction#PHASE_MOVING_NEW_FILE_COPIED}
	 * @return
	 * @throws IOException 
	 */
	private Mono<Object> copyToNewFile(FileEntity fileEntity, String bucketName) throws IOException {
		final String fileNewPathStr = getFileNewPath(fileEntity.getFilePath(), fileEntity.getCurrentPhase(), fileEntity.getDesiredPhase());
		Path FileNewPath = FileTool.createFile(fileNewPathStr);
		Files.copy(Paths.get(fileEntity.getFilePath()), FileNewPath, StandardCopyOption.REPLACE_EXISTING);
		Query query = Query.query(Criteria.where("fileKey").is(fileEntity.getFileKey()));
		Update update = Update.update("currentAction", FileAction.PHASE_MOVING_NEW_FILE_COPIED)
				.set("updateTime", new Date());
		return mongoTemplate.updateFirst(query, update, FileEntity.class, bucketName)
				.map(r -> {
					log.info("copy file from {} to {} done, bucket: {}, file key: {}", fileEntity.getFilePath()
							, fileNewPathStr, bucketName, fileEntity.getFileKey());
					return r.getModifiedCount();
				});
	}
	
	/**
	 * Update file path field from old path to new path, also see {@link FileAction#PHASE_MOVING_PATH_CHANGED}
	 * @param fileEntity
	 * @param bucketName
	 * @return
	 */
	private Mono<Object> changeFilePath(FileEntity fileEntity, String bucketName) {
		final String fileNewPathStr = getFileNewPath(fileEntity.getFilePath(), fileEntity.getCurrentPhase(), fileEntity.getDesiredPhase());
		Query query = Query.query(Criteria.where("fileKey").is(fileEntity.getFileKey()));
		Update update = Update.update("currentAction", FileAction.PHASE_MOVING_PATH_CHANGED)
				.set("filePath", fileNewPathStr)
				.set("updateTime", new Date());
		return mongoTemplate.updateFirst(query, update, FileEntity.class, bucketName)
				.map(r -> {
					log.info("update file entity path field from {} to {} done, bucket:{}, file key: {}", fileEntity.getFilePath()
							, bucketName, fileNewPathStr, fileEntity.getFileKey());
					return r.getModifiedCount();
				});
	}
	
	/**
	 * Delete old file path, also see {@link FileAction#PHASE_MOVING_OLD_FILE_DELETED}
	 * @param fileEntity
	 * @param bucketName
	 * @return
	 * @throws IOException
	 */
	private Mono<Object> deleteOldFile(FileEntity fileEntity, String bucketName) throws IOException {
		final String fileOldPathStr = getFileOldPath(fileEntity.getFilePath(), fileEntity.getCurrentPhase(), fileEntity.getDesiredPhase());
		Files.delete(Paths.get(fileOldPathStr));
		Query query = Query.query(Criteria.where("fileKey").is(fileEntity.getFileKey()));
		Update update = Update.update("currentAction", FileAction.PHASE_MOVING_OLD_FILE_DELETED)
				.set("updateTime", new Date());
		return mongoTemplate.updateFirst(query, update, FileEntity.class, bucketName)
				.map(r -> {
					log.info("delete old file done, bucket:{}, file key: {}, old file path: {}", bucketName
							, fileEntity.getFileKey(), fileOldPathStr);
					return r.getModifiedCount();
				});
	}
	
	/**
	 * Complete File Phase Moving task, see also {@link FileAction#COMPLETE}
	 * @param fileEntity
	 * @param bucketName
	 * @return
	 */
	private Mono<Object> completeMoving(FileEntity fileEntity, String bucketName) {
		Query query = Query.query(Criteria.where("fileKey").is(fileEntity.getFileKey()));
		Update update = Update.update("currentAction", FileAction.COMPLETE)
				.set("currentPhase", fileEntity.getDesiredPhase())
				.set("updateTime", new Date());
		return mongoTemplate.updateFirst(query, update, FileEntity.class, bucketName)
				.map(r -> {
					log.info("file phase moving complete, bucket:{}, file key: {}", bucketName, fileEntity.getFileKey());
					return r.getModifiedCount();
				});
	}
	
	private String getFileNewPath(String oldPath, FilePhase currentPhase, FilePhase desiredPhase) {
		return oldPath.replaceFirst(getPhaseBasePath(currentPhase), getPhaseBasePath(desiredPhase));
	}
	
	private String getFileOldPath(String newPath, FilePhase currentPhase, FilePhase desiredPhase) {
		return newPath.replaceFirst(getPhaseBasePath(desiredPhase), getPhaseBasePath(currentPhase));
	}
	
	private String getPhaseBasePath(FilePhase filePhase) {
		if (FilePhase.HOT.equals(filePhase)) {
			return fileCoreProperties.getHotPath();
		} else if (FilePhase.WARM.equals(filePhase)) {
			return fileCoreProperties.getWarmPath();
		} else if (FilePhase.COLD.equals(filePhase)) {
			return fileCoreProperties.getColdPath();
		}
		return null;
	}
}
