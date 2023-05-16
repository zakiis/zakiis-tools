package com.zakiis.file.portal.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Service;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.zakiis.file.domain.constants.AccessMode;
import com.zakiis.file.domain.constants.CommonConstants;
import com.zakiis.file.domain.constants.Message;
import com.zakiis.file.exception.ServiceException;
import com.zakiis.file.model.Bucket;
import com.zakiis.file.model.inner.Access;
import com.zakiis.file.portal.domain.dto.bucket.BucketAddDTO;
import com.zakiis.file.portal.domain.dto.bucket.BucketQueryDTO;
import com.zakiis.file.portal.domain.dto.bucket.BucketQueryResultDTO;
import com.zakiis.file.portal.domain.dto.bucket.BucketUpdateDTO;
import com.zakiis.file.portal.exception.ErrorEnum;
import com.zakiis.file.portal.service.tool.ValidationTool;
import com.zakiis.security.exception.NoPermissionException;

import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

@Service
public class BucketService {

	@Autowired
	ApplicationCacheService cacheService;
	@Autowired
	ReactiveMongoTemplate mongoTemplate;
	@Autowired
	ReactiveStringRedisTemplate stringRedisTemplate;

	public void checkChannelCanRead(String ak, String bucketName) {
		Bucket bucket = cacheService.getBucketMap().get(bucketName);
		ValidationTool.notNull(bucket, ErrorEnum.BUCKET_NOT_EXISTS);
		if (AccessMode.PUBLIC.equals(bucket.getAccessMode())) {
			return;
		}
		Access access = Optional.ofNullable(bucket.getAccess())
			.map(map -> map.get(ak))
			.orElse(null);
		if (access == null || !access.isCanRead()) {
			throw new NoPermissionException(String.format("%s has no read privilege on %s", ak, bucketName));
		}
	}
	
	public void checkChannelCanWrite(String ak, String bucketName) {
		Bucket bucket = cacheService.getBucketMap().get(bucketName);
		ValidationTool.notNull(bucket, ErrorEnum.BUCKET_NOT_EXISTS);
		Access access = Optional.ofNullable(bucket.getAccess())
			.map(map -> map.get(ak))
			.orElse(null);
		if (access == null || !access.isCanWrite()) {
			throw new NoPermissionException(String.format("%s has no write privilege on %s", ak, bucketName));
		}
	}

	public Mono<Bucket> addBucket(BucketAddDTO addDTO, String contextUsername) {
		Bucket bucket = new Bucket();
		bucket.setName(addDTO.getName());
		bucket.setDescription(addDTO.getDescription());
		bucket.setAccessMode(addDTO.getAccessMode());
		bucket.setAccess(addDTO.getAccess());
		bucket.setCreatedBy(contextUsername);
		bucket.setCreateTime(new Date());
		bucket.setUpdatedBy(contextUsername);
		bucket.setUpdateTime(new Date());
		return mongoTemplate.findById(bucket.getName(), Bucket.class)
				.hasElement()
				.flatMap(exists -> {
					if (exists) {
						throw new ServiceException(ErrorEnum.BUCKET_REPEATED);
					}
					return mongoTemplate.insert(bucket)
							.doOnSuccess(obj -> cacheService.refreshBucketMap());
				}).zipWhen(dbBucket -> {
					return mongoTemplate.createCollection(dbBucket.getName())
							.zipWhen(c -> {
								Mono<String> idx1Mono = mongoTemplate.indexOps(dbBucket.getName())
									.ensureIndex(new Index().on("currentPhase", Direction.ASC)
											.on("updateTime", Direction.ASC)
											.named("idx_current_phase_update_time"));
								Mono<String> idx2Mono = mongoTemplate.indexOps(dbBucket.getName())
										.ensureIndex(new Index().on("currentAction", Direction.ASC).named("idx_current_action"));
								return idx1Mono.zipWith(idx2Mono);
							}).then(stringRedisTemplate.convertAndSend(Message.APPLICATION_CACHE_REFRESH, ApplicationCacheService.CACHE_KEY_BUCKET));
				}).map(Tuple2::getT1);
	}

	public Mono<Long> updateBucket(BucketUpdateDTO updateDTO, String contextUsername) {
		return mongoTemplate.findById(updateDTO.getName(), Bucket.class)
				.switchIfEmpty(Mono.error(new ServiceException(ErrorEnum.BUCKET_NOT_EXISTS)))
				.flatMap(dbChannel -> {
					Update update = new Update();
					if (StringUtils.isNotEmpty(updateDTO.getDescription())) {
						update.set("description", updateDTO.getDescription());
					}
					if (updateDTO.getAccessMode() != null) {
						update.set("accessMode", updateDTO.getAccessMode());
					}
					if (updateDTO.getAccess() != null) {
						update.set("access", updateDTO.getAccess());
					}
					update.set("updatedBy", contextUsername);
					update.set("updateTime", new Date());
					return mongoTemplate.updateFirst(Query.query(Criteria.where("name").is(updateDTO.getName())), update, Bucket.class)
						.map(UpdateResult::getModifiedCount)
						.zipWhen(obj -> stringRedisTemplate.convertAndSend(Message.APPLICATION_CACHE_REFRESH, ApplicationCacheService.CACHE_KEY_BUCKET))
						.map(Tuple2::getT1);
				});
	}

	public Mono<Long> deleteBucket(String name) {
		return mongoTemplate.findById(name, Bucket.class)
				.switchIfEmpty(Mono.error(new ServiceException(ErrorEnum.BUCKET_NOT_EXISTS)))
				.flatMap(dbBucket -> {
					return mongoTemplate.remove(dbBucket)
							.map(DeleteResult::getDeletedCount)
							.zipWhen(obj -> stringRedisTemplate.convertAndSend(Message.APPLICATION_CACHE_REFRESH, ApplicationCacheService.CACHE_KEY_BUCKET))
							.map(Tuple2::getT1);
				});
	}

	public Mono<BucketQueryResultDTO> queryBucket(BucketQueryDTO queryDTO) {
		Criteria criteria = new Criteria();
		if (StringUtils.isNotBlank(queryDTO.getName())) {
			criteria.and("name").is(queryDTO.getName());
		}
		if (StringUtils.isNotBlank(queryDTO.getDescription())) {
			criteria.and("description").regex(queryDTO.getDescription());
		}
		if (queryDTO.getAccessMode() != null) {
			criteria.and("accessMode").is(queryDTO.getAccessMode());
		}
		if (queryDTO.getPageIndex() == null || queryDTO.getPageIndex() < 1) {
			queryDTO.setPageIndex(CommonConstants.DEFAULT_PAGE_INDEX);
		}
		if (queryDTO.getPageSize() == null) {
			queryDTO.setPageSize(CommonConstants.DEFAULT_PAGE_SIZE);
		}
		long skip = (queryDTO.getPageIndex() - 1) * queryDTO.getPageSize();
		Mono<List<Bucket>> dataListMono = mongoTemplate.find(Query.query(criteria).skip(skip).limit(queryDTO.getPageSize()), Bucket.class)
			.collectList();
		Mono<Long> dataCountMono = mongoTemplate.count(Query.query(criteria), Bucket.class);
		return dataListMono.zipWith(dataCountMono)
			.map(tuple -> {
				List<Bucket> dataList = tuple.getT1();
				Long count = tuple.getT2();
				BucketQueryResultDTO result = new BucketQueryResultDTO();
				result.setPageIndex(queryDTO.getPageIndex());
				result.setPageSize(queryDTO.getPageSize());
				result.setCount(count);
				result.setBuckets(dataList);
				return result;
			});
	}
}
