package com.zakiis.workflow.dto.form;

public class StringFormType implements FormType {

	private static final long serialVersionUID = -2777224723570573907L;

	@Override
	public Object convertFormValueToModelValue(String propertyValue) {
		return propertyValue;
	}

	@Override
	public String convertModelValueToFormValue(Object modelValue) {
		return (String)modelValue;
	}

	@Override
	public String getName() {
		return "string";
	}

	@Override
	public Object getMetaData() {
		return null;
	}

}
