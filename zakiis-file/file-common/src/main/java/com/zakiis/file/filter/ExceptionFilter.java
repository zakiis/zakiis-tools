package com.zakiis.file.filter;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import com.zakiis.common.JsonUtil;
import com.zakiis.common.constants.CommonConstants;
import com.zakiis.file.domain.dto.ResponseDTO;
import com.zakiis.file.exception.handler.ReactiveExceptionHandler;

import reactor.core.publisher.Mono;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class ExceptionFilter implements WebFilter {
	
	ReactiveExceptionHandler exceptionHandler = new ReactiveExceptionHandler();

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
		AtomicReference<String> traceIdRefer = new AtomicReference<String>();
		return Mono.deferContextual(ctx -> {
					traceIdRefer.set(ctx.get(CommonConstants.TRACE_ID_PARAM_NAME));
					return Mono.just(traceIdRefer);
				})
				.then(chain.filter(exchange))
				.onErrorResume(t -> {
					String traceId = traceIdRefer.get();
					ResponseEntity<ResponseDTO<Object>> responseEntity = exceptionHandler.handleError(traceId, t);
					ServerHttpResponse response = exchange.getResponse();
					response.setStatusCode(responseEntity.getStatusCode());
					response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
					return response.writeAndFlushWith(Mono.just(Mono.just(response.bufferFactory().wrap(getWrapperData(responseEntity.getBody())))));
				});
	}

	private byte[] getWrapperData(Object body) {
		return JsonUtil.toJson(body).getBytes(StandardCharsets.UTF_8);
	}
}
