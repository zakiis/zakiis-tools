package com.zakiis.file.portal.boot.autoconfigure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.ReactiveRedisMessageListenerContainer;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.RedisSerializer;

import com.zakiis.file.domain.constants.Message;
import com.zakiis.file.portal.service.listener.ApplicationCacheRefreshListener;

@Configuration
public class RedisConfiguration {
	
	Logger log = LoggerFactory.getLogger(RedisConfiguration.class);

//	@Bean
	public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory redisConnectionFactory
			, ApplicationCacheRefreshListener cacheRefreshListener) {
		RedisMessageListenerContainer redisMessageListenerContainer = new RedisMessageListenerContainer();
		redisMessageListenerContainer.setConnectionFactory(redisConnectionFactory);
		MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter(cacheRefreshListener);
		redisMessageListenerContainer.addMessageListener(messageListenerAdapter, new PatternTopic("REFRESH_CACHE"));
        redisMessageListenerContainer.setTopicSerializer(RedisSerializer.string());
		return redisMessageListenerContainer;
	}
	
	@Bean
	public ReactiveRedisMessageListenerContainer reactiveRedisMessageListenerContainer(ReactiveRedisConnectionFactory factory
			, ApplicationCacheRefreshListener cacheRefreshListener) {
		log.info("add application cache refresh message listener");
		ReactiveRedisMessageListenerContainer msgListenerContainer = new ReactiveRedisMessageListenerContainer(factory);
		msgListenerContainer.receive(ChannelTopic.of(Message.APPLICATION_CACHE_REFRESH))
			.doOnNext(msg -> {
				cacheRefreshListener.handleMessage(msg.getChannel(), msg.getMessage());
			}).subscribe();
		return msgListenerContainer;
	}
}
