package com.zakiis.file.filter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import com.zakiis.common.TraceIdUtil;
import com.zakiis.common.constants.CommonConstants;
import com.zakiis.file.util.ReactiveLogger;
import com.zakiis.file.util.RequestUtil;

import reactor.core.publisher.Mono;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class LogFilter implements WebFilter {

	ReactiveLogger log = new ReactiveLogger(LoggerFactory.getLogger(LogFilter.class));
	Set<String> excludePath = new HashSet<String>(Arrays.asList("/health"));
	@Autowired
	RequestUtil requestUtil;
	
	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
		HttpHeaders headers = exchange.getRequest().getHeaders();
		String traceId = headers.getFirst(CommonConstants.TRACE_ID_HEADER_NAME);
		if (StringUtils.isBlank(traceId)) {
			traceId = TraceIdUtil.generateTraceId();
		}
		AtomicReference<String> traceIdRefer = new AtomicReference<String>(traceId);
		requestUtil.incre();
		String path = exchange.getRequest().getPath().value();
		final boolean logEnabled;
		if (excludePath.contains(path)) {
			logEnabled = false;
		} else {
			logEnabled = true;
		}
		if (logEnabled) {
			log.info(traceId, "{} {} start", exchange.getRequest().getMethodValue(), path);
		}
		long start = System.currentTimeMillis();
		return chain.filter(exchange)
				.contextWrite(context -> context.put(CommonConstants.TRACE_ID_PARAM_NAME, traceIdRefer.get()))
				.doOnTerminate(new Runnable() {
					@Override
					public void run() {
						long end = System.currentTimeMillis();
						requestUtil.decre();
						if (logEnabled) {
							log.info(traceIdRefer.get(), "{} {} end, time elapse {} ms", exchange.getRequest().getMethodValue(), path, end - start);
						}
					}
				}).then();
				
	}

}
