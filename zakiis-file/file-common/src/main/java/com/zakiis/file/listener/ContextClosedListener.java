package com.zakiis.file.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;

import com.netflix.discovery.EurekaClient;
import com.zakiis.file.util.RequestUtil;

public class ContextClosedListener implements ApplicationListener<ContextClosedEvent> {
	
	Logger log = LoggerFactory.getLogger(ContextClosedListener.class);
	@Autowired
	EurekaClient eurekaClient;
	@Autowired
	RequestUtil requestUtil;
	/**
	 * minWait = 2 * registerFetchInterval + loadBalancerCacheTTL in the case of responseCacheUpdateInterval < registerFetchInterval
	 * In production environment, we recommend that registerFetchInterval = 5s, responseCacheUpdateInterval = 3s, loadBalancerCacheTTL = 2s
	 */
	Long shutdownWaitMillis = 12000L; 
	
	@Override
	public void onApplicationEvent(ContextClosedEvent event) {
		log.info("Shutdown eureka server");
		eurekaClient.shutdown();
		try {
			Thread.sleep(shutdownWaitMillis);
		} catch (InterruptedException e) {
			log.warn("Eureka shutdown and wait for {} milliseconds to make client refresh cache got an error", shutdownWaitMillis, e);
		}
		requestUtil.shutdownGracefully();
	}

}
