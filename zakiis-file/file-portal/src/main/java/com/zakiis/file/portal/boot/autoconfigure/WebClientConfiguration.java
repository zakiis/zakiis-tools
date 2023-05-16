package com.zakiis.file.portal.boot.autoconfigure;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.reactive.DeferringLoadBalancerExchangeFilterFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import com.zakiis.common.constants.CommonConstants;
import com.zakiis.file.domain.dto.ResponseDTO;
import com.zakiis.file.exception.ServiceException;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;
import reactor.netty.resources.LoopResources;

@Configuration
public class WebClientConfiguration {
	
	Logger log = LoggerFactory.getLogger(WebClientConfiguration.class);
	
	/**
	 * WebClient can't change after build, You need add load balancer filter by yourself or inject WebClient.Builder Bean and add {@link LoadBalanced} annotation on the method.
	 * @see org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction
	 * @see org.springframework.cloud.loadbalancer.core.RandomLoadBalancer#choose(org.springframework.cloud.client.loadbalancer.Request)
	 * @see org.springframework.cloud.loadbalancer.core.CachingServiceInstanceListSupplier
	 * @see org.springframework.cloud.loadbalancer.core.DiscoveryClientServiceInstanceListSupplier
	 * @see org.springframework.cloud.loadbalancer.cache.DefaultLoadBalancerCache
	 * @see org.springframework.cloud.loadbalancer.cache.DefaultLoadBalancerCacheManager
	 * @param exchangeFilterFunction
	 * @return
	 */
	@Bean
	public WebClient webClient(DeferringLoadBalancerExchangeFilterFunction<? extends ExchangeFilterFunction> exchangeFilterFunction) {
		log.info("Customize web client feature enabled.");
		ConnectionProvider provider = ConnectionProvider.builder("http")
			.maxConnections(500)
			.pendingAcquireMaxCount(1000) /** Default to {@code 2 * max connections}.*/
			.maxIdleTime(Duration.ofSeconds(30L))
			.build();
		LoopResources loopResources = LoopResources.create("reactor-http", 1, LoopResources.DEFAULT_IO_WORKER_COUNT, true);
		HttpClient httpClient = HttpClient.create(provider)
				.doOnConnected(conn -> {
					conn.addHandlerLast(new ReadTimeoutHandler(10))
						.addHandlerLast(new WriteTimeoutHandler(10));
				})
				.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
				.option(ChannelOption.TCP_NODELAY, true)
				.runOn(loopResources);
		return WebClient.builder()
			.clientConnector(new ReactorClientHttpConnector(httpClient))
			.filter(exchangeFilterFunction)
			.filter(traceIdFilter())
			.filter(httpCodeFilter())
			.build();
	}
	
	private ExchangeFilterFunction traceIdFilter() {
		return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
			return Mono.deferContextual(ctx -> {
				String traceId = ctx.get(CommonConstants.TRACE_ID_PARAM_NAME);
				ClientRequest filteredRequest = ClientRequest.from(clientRequest)
					.header(CommonConstants.TRACE_ID_HEADER_NAME, traceId)
					.build();
				return Mono.just(filteredRequest);
			});
		});
	}
	
	private ExchangeFilterFunction httpCodeFilter() {
		return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
			if (clientResponse.statusCode().isError()) {
				return clientResponse.bodyToMono(new ParameterizedTypeReference<ResponseDTO<Object>>() {})
						.flatMap(dto -> Mono.error(new ServiceException(dto.getCode(), dto.getMessage())));
			} else {
				return Mono.just(clientResponse);
			}
		});
	}
}
