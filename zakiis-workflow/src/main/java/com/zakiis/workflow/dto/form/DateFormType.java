package com.zakiis.workflow.dto.form;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.zakiis.workflow.exception.WorkflowRuntimeException;

public class DateFormType implements FormType {

	private static final long serialVersionUID = 6901188997165538449L;
	String datePattern;
	Format dateFormat;
	
	public DateFormType(String datePattern) {
		this.datePattern = datePattern;
		this.dateFormat = new SimpleDateFormat(datePattern);
	}

	@Override
	public Object convertFormValueToModelValue(String propertyValue) {
		if (StringUtils.isBlank(propertyValue)) {
			return null;
		}
		try {
			return dateFormat.parseObject(propertyValue);
		} catch (Exception e) {
			throw new WorkflowRuntimeException("Invalid date value:" + propertyValue);
		}
	}

	@Override
	public String convertModelValueToFormValue(Object modelValue) {
		return Optional.ofNullable(modelValue)
					.map(d -> dateFormat.format(d))
					.orElse(null);
	}

	@Override
	public String getName() {
		return "date";
	}

	@Override
	public Object getMetaData() {
		return datePattern;
	}

}
