package com.zakiis.file.portal.service;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Service;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.zakiis.file.domain.constants.CommonConstants;
import com.zakiis.file.domain.constants.Message;
import com.zakiis.file.exception.ServiceException;
import com.zakiis.file.model.Channel;
import com.zakiis.file.portal.domain.constants.FunctionCode;
import com.zakiis.file.portal.domain.dto.channel.ChannelAddDTO;
import com.zakiis.file.portal.domain.dto.channel.ChannelQueryDTO;
import com.zakiis.file.portal.domain.dto.channel.ChannelQueryResultDTO;
import com.zakiis.file.portal.domain.dto.channel.ChannelUpdateDTO;
import com.zakiis.file.portal.exception.ErrorEnum;
import com.zakiis.security.HMACUtil;
import com.zakiis.security.HMACUtil.HMACType;
import com.zakiis.security.codec.HexUtil;

import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

@Service
public class ChannelService {

	@Autowired
	ReactiveMongoTemplate mongoTemplate;
	@Autowired
	ApplicationCacheService applicationCacheService;
	@Autowired
	ReactiveStringRedisTemplate stringRedisTemplate;
	
	public Mono<Channel> addChannel(ChannelAddDTO addDTO, String contextUsername) {
		Channel channel = new Channel();
		channel.setName(addDTO.getName());
		channel.setFunctions(Optional.ofNullable(addDTO.getFunctions())
				.map(functionEnumSet -> functionEnumSet.stream().map(FunctionCode::name).collect(Collectors.toSet()))
				.orElse(new HashSet<String>()));
		channel.setAlgorithm(HMACType.HMAC_SHA_256.getAlgorithm());
		channel.setAk(UUID.randomUUID().toString());
		channel.setSk(HexUtil.toHexString(HMACUtil.genSecretKey(HMACType.HMAC_SHA_256)));
		channel.setStatus(CommonConstants.STATUS_ACTIVE);
		channel.setCreatedBy(contextUsername);
		channel.setCreateTime(new Date());
		channel.setUpdatedBy(contextUsername);
		channel.setUpdateTime(new Date());
		Query query = Query.query(Criteria.where("name").is(channel.getName()));
		return mongoTemplate.findOne(query, Channel.class)
			.hasElement()
			.flatMap(exists -> {
				if (exists) {
					throw new ServiceException(ErrorEnum.CHANNEL_REPEATED);
				}
				return mongoTemplate.insert(channel)
						.zipWhen(obj -> stringRedisTemplate.convertAndSend(Message.APPLICATION_CACHE_REFRESH, ApplicationCacheService.CACHE_KEY_CHANNEL))
						.map(Tuple2::getT1);
						
			});
	}
	
	public Mono<Long> updateChannel(ChannelUpdateDTO updateDTO, String contextUsername) {
		Query query = Query.query(Criteria.where("ak").is(updateDTO.getAk()));
		return mongoTemplate.findOne(query, Channel.class)
				.switchIfEmpty(Mono.error(new ServiceException(ErrorEnum.CHANNEL_NOT_EXISTS)))
				.flatMap(dbChannel -> {
					Update update = new Update();
					if (updateDTO.getStatus() != null) {
						update.set("status", updateDTO.getStatus());
					}
					if (updateDTO.getFunctions() != null) {
						update.set("functions", updateDTO.getFunctions());
					}
					update.set("updatedBy", contextUsername);
					update.set("updateTime", new Date());
					return mongoTemplate.updateFirst(query, update, Channel.class)
						.map(UpdateResult::getModifiedCount)
						.zipWhen(obj -> stringRedisTemplate.convertAndSend(Message.APPLICATION_CACHE_REFRESH, ApplicationCacheService.CACHE_KEY_CHANNEL))
						.map(Tuple2::getT1);
				});
	}
	
	public Mono<Long> deleteChannel(String ak) {
		Query query = Query.query(Criteria.where("ak").is(ak));
		return mongoTemplate.findOne(query, Channel.class)
				.switchIfEmpty(Mono.error(new ServiceException(ErrorEnum.CHANNEL_NOT_EXISTS)))
				.flatMap(dbChannel -> {
					return mongoTemplate.remove(dbChannel)
							.map(DeleteResult::getDeletedCount)
							.zipWhen(obj -> stringRedisTemplate.convertAndSend(Message.APPLICATION_CACHE_REFRESH, ApplicationCacheService.CACHE_KEY_CHANNEL))
							.map(Tuple2::getT1);
				});
	}
	
	public Mono<ChannelQueryResultDTO> queryChannel(ChannelQueryDTO queryDTO) {
		Criteria criteria = new Criteria();
		if (StringUtils.isNotBlank(queryDTO.getAk())) {
			criteria.and("ak").is(queryDTO.getAk());
		}
		if (StringUtils.isNotBlank(queryDTO.getName())) {
			criteria.and("name").is(queryDTO.getName());
		}
		if (queryDTO.getStatus() != null) {
			criteria.and("status").is(queryDTO.getStatus());
		}
		if (queryDTO.getPageIndex() == null || queryDTO.getPageIndex() < 1) {
			queryDTO.setPageIndex(CommonConstants.DEFAULT_PAGE_INDEX);
		}
		if (queryDTO.getPageSize() == null) {
			queryDTO.setPageSize(CommonConstants.DEFAULT_PAGE_SIZE);
		}
		long skip = (queryDTO.getPageIndex() - 1) * queryDTO.getPageSize();
		Mono<List<Channel>> dataListMono = mongoTemplate.find(Query.query(criteria).skip(skip).limit(queryDTO.getPageSize()), Channel.class)
			.collectList();
		Mono<Long> dataCountMono = mongoTemplate.count(Query.query(criteria), Channel.class);
		return dataListMono.zipWith(dataCountMono)
			.map(tuple -> {
				List<Channel> dataList = tuple.getT1();
				Long count = tuple.getT2();
				ChannelQueryResultDTO result = new ChannelQueryResultDTO();
				result.setPageIndex(queryDTO.getPageIndex());
				result.setPageSize(queryDTO.getPageSize());
				result.setCount(count);
				result.setChannels(dataList);
				return result;
			});
	}
}
