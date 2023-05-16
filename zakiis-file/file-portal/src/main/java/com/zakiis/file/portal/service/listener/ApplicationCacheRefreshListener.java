package com.zakiis.file.portal.service.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zakiis.file.portal.service.ApplicationCacheService;

@Service
public class ApplicationCacheRefreshListener {

	Logger log = LoggerFactory.getLogger(ApplicationCacheRefreshListener.class);
	
	@Autowired
	ApplicationCacheService cacheService;

	public void handleMessage(String topic, String msg) {
		log.info("Recevied msg, topic:{}, msg:{}", topic, msg);
		cacheService.refreshCache(msg);
	}

}
