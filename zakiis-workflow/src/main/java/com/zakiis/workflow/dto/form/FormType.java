package com.zakiis.workflow.dto.form;

import java.io.Serializable;

public interface FormType extends Serializable {

	/**
	 * convert form value to java model value
	 * @param propertyValue form value always be String
	 * @return
	 */
	Object convertFormValueToModelValue(String propertyValue);
	
	/**
	 * convert model value to form value which type is string
	 * @param modelValue
	 * @return form value
	 */
	String convertModelValueToFormValue(Object modelValue);
	
	/**
	 * get form type name
	 * @return form type name
	 */
	String getName();
	
	/**
	 * meta data for this form type, for example date form type may hold a meta data: date pattern.
	 * @return meta data
	 */
	Object getMetaData();
	
}
