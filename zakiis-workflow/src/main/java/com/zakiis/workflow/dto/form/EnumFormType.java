package com.zakiis.workflow.dto.form;

import java.util.Map;

import com.zakiis.workflow.exception.WorkflowRuntimeException;

public class EnumFormType implements FormType {

	private static final long serialVersionUID = 84286521178181540L;
	Map<String, String> values;
	
	public EnumFormType(Map<String, String> values) {
		this.values = values;
	}

	@Override
	public Object convertFormValueToModelValue(String propertyValue) {
		validateValue(propertyValue);
		return propertyValue;
	}

	@Override
	public String convertModelValueToFormValue(Object modelValue) {
		if (modelValue != null) {
			if (!(modelValue instanceof String)) {
				throw new WorkflowRuntimeException("Enum model value should be a String");
			}
			validateValue((String)modelValue);
		}
		return (String)modelValue;
	}

	@Override
	public String getName() {
		return "enum";
	}

	@Override
	public Object getMetaData() {
		return values;
	}
	
	protected void validateValue(String value) {
		if (value != null) {
			if (values != null && !values.containsKey(value)) {
				throw new WorkflowRuntimeException("Invalid value for enum property: " + value);
			}
		}
	}

}
