package com.zakiis.workflow.dto.form;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

public class BooleanFormType implements FormType {

	private static final long serialVersionUID = -2777224723570573904L;

	@Override
	public Object convertFormValueToModelValue(String propertyValue) {
		if (StringUtils.isEmpty(propertyValue)) {
			return null;
		}
		return Boolean.valueOf(propertyValue);
	}

	@Override
	public String convertModelValueToFormValue(Object modelValue) {
		return Optional.ofNullable(modelValue)
					.map(Object::toString)
					.orElse(null);
	}

	@Override
	public String getName() {
		return "boolean";
	}

	@Override
	public Object getMetaData() {
		return null;
	}

}
