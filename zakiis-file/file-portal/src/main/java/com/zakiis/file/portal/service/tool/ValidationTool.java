package com.zakiis.file.portal.service.tool;

import org.apache.commons.lang3.StringUtils;

import com.zakiis.file.exception.ServiceException;
import com.zakiis.file.portal.exception.ErrorEnum;

public abstract class ValidationTool {

	public static void notEmpty(String str, String fieldName) {
		if (StringUtils.isEmpty(str)) {
			throw new ServiceException(ErrorEnum.FIELD_REQUIRE_NOT_EMPTY, new Object[] {fieldName});
		}
	}
	
	public static void notNull(Object obj, ErrorEnum error) {
		if (obj == null) {
			throw new ServiceException(error);
		}
	}
	
	public static void notNull(Object obj, String fieldName) {
		if (obj == null) {
			throw new ServiceException(ErrorEnum.FIELD_REQUIRE_NOT_NULL, new Object[] {fieldName});
		}
	}
}
