package com.zakiis.file.core.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zakiis.file.core.service.task.CleanFileEntityService;
import com.zakiis.file.core.service.task.FilePhaseMovingTask;
import com.zakiis.file.core.service.task.FilePhaseTransformTask;
import com.zakiis.file.domain.dto.ResponseDTO;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1/file-core/task")
public class TaskController {

	@Autowired
	CleanFileEntityService cleanFileEntityService;
	@Autowired
	FilePhaseTransformTask filePhaseTransformTask;
	@Autowired
	FilePhaseMovingTask filePhaseMovingTask;
	
	/**
	 * for performance purpose there is no transaction guarantee on upload file method, It may exists a situation that a record 
	 * in Mongodb with uploading status but no file in the disk, we should remove this record to keep data consistency.
	 * @return deleted count
	 */
	@PostMapping("cleanInvalidFileEntity")
	public Mono<ResponseDTO<Long>> cleanInvalidFileEntity() {
		return cleanFileEntityService.cleanInvalidUploadingFileEntity()
				.map(ResponseDTO::ok);
	}
	
	@PostMapping("transformFilePhase")
	public Mono<ResponseDTO<Long>> transformFilePhase() {
		return filePhaseTransformTask.transformPhase()
				.map(ResponseDTO::ok);
	}
	
	@PostMapping("movingFilePhase")
	public Mono<ResponseDTO<Long>> movingFilePhase() {
		return filePhaseMovingTask.movingPhase()
				.map(ResponseDTO::ok);
	}
}
