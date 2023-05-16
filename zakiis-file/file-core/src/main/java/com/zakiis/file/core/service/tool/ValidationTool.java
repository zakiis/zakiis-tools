package com.zakiis.file.core.service.tool;

import org.apache.commons.lang3.StringUtils;

import com.zakiis.file.core.exception.ErrorEnum;
import com.zakiis.file.exception.ServiceException;

public abstract class ValidationTool {

	public static void notEmpty(String str, String fieldName) {
		if (StringUtils.isEmpty(str)) {
			throw new ServiceException(ErrorEnum.FIELD_REQUIRE_NOT_EMPTY, new Object[] {fieldName});
		}
	}
}
