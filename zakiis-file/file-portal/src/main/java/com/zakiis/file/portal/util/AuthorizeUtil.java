package com.zakiis.file.portal.util;

import java.util.Optional;

import org.springframework.http.HttpCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;

import com.zakiis.file.domain.constants.CommonConstants;

public interface AuthorizeUtil {

	static String getAccessToken(ServerHttpRequest request) {
		return Optional.ofNullable(request.getHeaders().getFirst(CommonConstants.JWT_HEADER_NAME))
				.map(AuthorizeUtil::getJwtToken)
				.orElse(Optional.ofNullable(request.getCookies().getFirst(CommonConstants.JWT_HEADER_NAME))
						.map(HttpCookie::getValue)
						.map(AuthorizeUtil::getJwtToken)
						.orElse(null));
	}
	
	static String getAk(ServerHttpRequest request) {
		return Optional.ofNullable(request.getHeaders().getFirst(CommonConstants.X_ACCESS_KEY))
				.orElse(request.getQueryParams().getFirst(CommonConstants.AK));
	}
	
	static String getSign(ServerHttpRequest request) {
		return Optional.ofNullable(request.getHeaders().getFirst(CommonConstants.X_SIGN))
				.orElse(request.getQueryParams().getFirst(CommonConstants.SIGN));
	}
	
	static String getJwtToken(String authorization) {
		if (authorization != null && authorization.startsWith(CommonConstants.JWT_HEADER_VALUE_PREFIX)) {
			return authorization.substring(CommonConstants.JWT_HEADER_VALUE_PREFIX.length());
		}
		return null;
	}
}
