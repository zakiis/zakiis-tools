package com.zakiis.file.core.service.task;

import java.util.Date;
import java.util.stream.Collectors;

import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.mongodb.client.result.DeleteResult;
import com.zakiis.file.core.boot.autoconfigure.properties.FileCoreProperties;
import com.zakiis.file.domain.constants.FileAction;
import com.zakiis.file.model.Bucket;

import reactor.core.publisher.Mono;

/**
 * clean the file entity which in uploading status for a long time
 * @author 10901
 */
@Service
public class CleanFileEntityService {

	@Autowired
	FileCoreProperties properties;
	@Autowired
	ReactiveMongoTemplate mongoTemplate;
	
	Logger log = LoggerFactory.getLogger(CleanFileEntityService.class);
	
	public Mono<Long> cleanInvalidUploadingFileEntity() {
		return mongoTemplate.findAll(Bucket.class)
			.flatMap(bucket -> cleanInvalidUploadingFileEntity(bucket))
			.collect(Collectors.summingLong(t -> t))
			.doOnSuccess(deletedCount -> log.info("clean invalid uploding file entity done, deleted count: {}", deletedCount));
	}
	
	private Mono<Long> cleanInvalidUploadingFileEntity(Bucket bucket) {
		Date expireDate = DateUtils.addSeconds(new Date(), properties.getCleanUploadingFileEntityAfterSeconds() * -1);
		Query query = Query.query(Criteria.where("currentAction").is(FileAction.UPLOADING)
				.and("updateTime").lte(expireDate));
		return mongoTemplate.remove(query, bucket.getName())
			 .map(DeleteResult::getDeletedCount);
	}
}
