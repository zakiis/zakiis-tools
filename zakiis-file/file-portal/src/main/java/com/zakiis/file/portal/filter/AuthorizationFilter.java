package com.zakiis.file.portal.filter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import com.zakiis.file.model.Channel;
import com.zakiis.file.portal.boot.autoconfigure.properties.FilePortalProperties;
import com.zakiis.file.portal.domain.constants.FilePortalConstants;
import com.zakiis.file.portal.domain.constants.FunctionCode;
import com.zakiis.file.portal.exception.ErrorEnum;
import com.zakiis.file.portal.service.ApplicationCacheService;
import com.zakiis.file.portal.service.tool.ValidationTool;
import com.zakiis.file.portal.util.AuthorizeUtil;
import com.zakiis.file.portal.util.SignUtil;
import com.zakiis.security.PermissionUtil;
import com.zakiis.security.annotation.Permission;
import com.zakiis.security.exception.NoPermissionException;
import com.zakiis.security.jwt.JWTUtil;
import com.zakiis.security.jwt.algorithm.Algorithm;
import com.zakiis.security.jwt.exception.JWTVerificationException;
import com.zakiis.security.jwt.interfaces.DecodedJwt;

import reactor.core.publisher.Mono;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 2)
public class AuthorizationFilter implements WebFilter {
	
	@Autowired
	RequestMappingHandlerMapping handlerMapping;
	@Autowired
	ApplicationCacheService cacheService;
	@Autowired
	FilePortalProperties filePortalProperties;

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
		if (filePortalProperties.isSkipAuth()) {
			return chain.filter(exchange);
		}
		ServerHttpRequest request = exchange.getRequest();
		Mono<HandlerMethod> handlerMethodMono = handlerMapping.getHandler(exchange).cast(HandlerMethod.class);
		return handlerMethodMono.zipWhen(handlerMethod -> {
			if (handlerMethod.hasMethodAnnotation(Permission.class)) {
				Permission permission = handlerMethod.getMethodAnnotation(Permission.class);
				String token = AuthorizeUtil.getAccessToken(request);
				if (StringUtils.isNotBlank(token)) { //web back-end user request
					DecodedJwt decodedJwt = JWTUtil.decode(token);
					try {
						JWTUtil.require(Algorithm.HMAC256(filePortalProperties.getJwtSecret()))
							.verify(decodedJwt);
					} catch (JWTVerificationException e) {
						throw new NoPermissionException("JWT token error");
					}
					Set<String> functions = Optional.ofNullable(decodedJwt.getClaim(FilePortalConstants.FUNCTIONS_CLAIM))
						.map(str -> new HashSet<String>(Arrays.asList(str.split(","))))
						.orElse(null);
					exchange.getAttributes().put(FilePortalConstants.CONTEXT_USERNAME, decodedJwt.getSubject());
					if (functions != null && functions.contains(FunctionCode.ADMIN_VALUE)) {
						return chain.filter(exchange);
					}
					PermissionUtil.checkFunctionAccess(functions, permission);
				} else {	//channel request
					String ak = AuthorizeUtil.getAk(request);
					String sign = AuthorizeUtil.getSign(request);
					ValidationTool.notEmpty(sign, "Request header X-SIGN or query parameter sign");
					ValidationTool.notEmpty(ak, "Request header X-ACCESS-KEY or query parameter ak");
					Channel channel = Optional.ofNullable(cacheService.getChannelMap())
							.map(m -> m.get(ak))
							.orElse(null);
					ValidationTool.notNull(channel, ErrorEnum.CHANNEL_NOT_EXISTS);
					SignUtil.validateSign(ak, channel.getSk(), request.getMethodValue(), request.getURI().getPath(), sign);
					exchange.getAttributes().put(FilePortalConstants.CONTEXT_USERNAME, channel.getName());
					if (channel.getFunctions() != null && channel.getFunctions().contains(FunctionCode.ADMIN_VALUE)) {
						return chain.filter(exchange);
					}
					PermissionUtil.checkFunctionAccess(channel.getFunctions(), permission);
				}
			}
			return chain.filter(exchange);
		}).map(tuple -> tuple.getT2());
	}
}
