package com.zakiis.keycloak.sdk.util;

import java.security.InvalidParameterException;

import org.apache.commons.lang3.StringUtils;

public abstract class ValidationUtil {
	
	public static void notBlank(String obj, String fieldName) {
		if (StringUtils.isBlank(obj)) {
			throw new InvalidParameterException(String.format("%s can't be empty", fieldName));
		}
	}
}
