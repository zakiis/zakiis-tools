package com.zakiis.file.util;

import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Responsible for program shutdown gracefully
 * @author 10901
 */
public class RequestUtil {

	Logger log = LoggerFactory.getLogger(RequestUtil.class);
	final AtomicLong requestCount = new AtomicLong(0);
	
	public long incre() {
		return requestCount.incrementAndGet();
	}
	
	public long decre() {
		return requestCount.decrementAndGet();
	}
	
	public void shutdownGracefully() {
		long processingRequest;
		while ((processingRequest = requestCount.get()) > 0) {
			try {
				log.info("There are {} requests in processing", processingRequest);
				Thread.sleep(1000L);
			} catch (InterruptedException e) {}
		}
		log.info("All requests processed done, program will exit in 3 seconds.", processingRequest);
		//wait some time for writing response to client.
		try {
			Thread.sleep(3000L);
		} catch (InterruptedException e) {}
	}
}
