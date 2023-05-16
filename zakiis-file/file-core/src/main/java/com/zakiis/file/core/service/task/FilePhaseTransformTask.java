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
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.mongodb.client.result.UpdateResult;
import com.zakiis.file.core.boot.autoconfigure.properties.FileCoreProperties;
import com.zakiis.file.domain.constants.FileAction;
import com.zakiis.file.domain.constants.FilePhase;
import com.zakiis.file.model.Bucket;
import com.zakiis.file.model.FileEntity;

import reactor.core.publisher.Mono;

/**
 * update file desired phase after configured days, for example: HOT -> WARM, WARM -> COLD
 * @author 10901
 */
@Service
public class FilePhaseTransformTask {

	Logger log = LoggerFactory.getLogger(FilePhaseTransformTask.class);
	@Autowired
	FileCoreProperties fileCoreProperties;
	@Autowired
	ReactiveMongoTemplate mongoTemplate;
	
	public Mono<Long> transformPhase() {
		log.info("file phase transform task start");
		return transferHotToWarmPhase()
				.zipWith(transferWarmToColdPhase())
				.map(tuple -> tuple.getT1() + tuple.getT2())
				.doOnSuccess(updatedCount -> log.info("file phase transform task done, updated count: {}", updatedCount));
	}
	
	private Mono<Long> transferHotToWarmPhase() {
		if (fileCoreProperties.getMoveIntoWarmPhaseAfterDay() > 0) {
			Date gotoWarmPhaseDate = DateUtils.addDays(new Date(), fileCoreProperties.getMoveIntoWarmPhaseAfterDay() * -1);
			Query query = Query.query(Criteria.where("currentPhase").is(FilePhase.HOT)
					.and("updateTime").lte(gotoWarmPhaseDate)
					.and("currentAction").is(FileAction.COMPLETE));
			return mongoTemplate.findAll(Bucket.class)
					.flatMap(bucket -> {
						Update update = Update.update("desiredPhase", FilePhase.WARM)
								.set("currentAction", FileAction.PHASE_MOVING_INIT)
								.set("updateTime", new Date());
						return mongoTemplate.updateMulti(query, update, FileEntity.class, bucket.getName())
								.map(UpdateResult::getModifiedCount);
					})
					.collect(Collectors.summingLong(t -> t))
					.doOnSuccess(updatedCount -> log.info("File transform task from HOT phase to WARM phase done, matched count: {}", updatedCount));
		} else {
			return Mono.just(0L);
		}
	}
	
	private Mono<Long> transferWarmToColdPhase() {
		if (fileCoreProperties.getMoveIntoColdPhaseAfterDay() > 0) {
			Date gotoColdPhaseDate = DateUtils.addDays(new Date(), fileCoreProperties.getMoveIntoColdPhaseAfterDay() * -1);
			Query query = Query.query(Criteria.where("currentPhase").is(FilePhase.WARM)
					.and("updateTime").lte(gotoColdPhaseDate)
					.and("currentAction").is(FileAction.COMPLETE));
			return mongoTemplate.findAll(Bucket.class)
					.flatMap(bucket -> {
						Update update = Update.update("desiredPhase", FilePhase.COLD)
								.set("currentAction", FileAction.PHASE_MOVING_INIT)
								.set("updateTime", new Date());
						return mongoTemplate.updateMulti(query, update, FileEntity.class, bucket.getName())
								.map(UpdateResult::getModifiedCount);
					})
					.collect(Collectors.summingLong(t -> t))
					.doOnSuccess(updatedCount -> log.info("File transform task from WARM phase to COLD phase done, matched count: {}", updatedCount));
		} else {
			return Mono.just(0L);
		}
	}
}
