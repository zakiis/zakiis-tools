package com.zakiis.workflow.dto.form;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

public class DoubleFormType implements FormType {

	private static final long serialVersionUID = -2777224723570573905L;

	@Override
	public Object convertFormValueToModelValue(String propertyValue) {
		if (StringUtils.isEmpty(propertyValue)) {
			return null;
		}
		return Double.valueOf(propertyValue);
	}

	@Override
	public String convertModelValueToFormValue(Object modelValue) {
		return Optional.ofNullable(modelValue)
					.map(Object::toString)
					.orElse(null);
	}

	@Override
	public String getName() {
		return "double";
	}

	@Override
	public Object getMetaData() {
		return null;
	}

}
