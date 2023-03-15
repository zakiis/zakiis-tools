package com.zakiis.workflow.service.tool;


import java.util.Map;

import org.activiti.engine.form.FormProperty;
import org.springframework.beans.BeanUtils;

import com.zakiis.workflow.dto.FormPropertyDTO;
import com.zakiis.workflow.dto.form.BooleanFormType;
import com.zakiis.workflow.dto.form.DateFormType;
import com.zakiis.workflow.dto.form.DoubleFormType;
import com.zakiis.workflow.dto.form.EnumFormType;
import com.zakiis.workflow.dto.form.FormType;
import com.zakiis.workflow.dto.form.LongFormType;
import com.zakiis.workflow.dto.form.StringFormType;

public class FormTool {
	
	public static FormPropertyDTO convertFromActivitiFormProperty(FormProperty formProperty) {
		FormPropertyDTO formPropertyDTO = new FormPropertyDTO();
		BeanUtils.copyProperties(formProperty, formPropertyDTO, "type");
		if (formProperty.getType() != null) {
			FormType formType = convertFromActivitiFormType(formProperty.getType());
			formPropertyDTO.setType(formType);
		}
		return formPropertyDTO;
	}

	
	@SuppressWarnings("unchecked")
	private static FormType convertFromActivitiFormType(org.activiti.engine.form.FormType formType) {
		FormType convertedFormType = null;
		switch (formType.getName()) {
		case "string":
			convertedFormType = new StringFormType();
			break;
		case "long":
			convertedFormType = new LongFormType();
			break;
		case "double":
			convertedFormType = new DoubleFormType();
			break;
		case "boolean":
			convertedFormType = new BooleanFormType();
			break;
		case "date":
			String datePattern = (String)((org.activiti.engine.impl.form.DateFormType)formType).getInformation("datePattern");
			convertedFormType = new DateFormType(datePattern);
			break;
		case "enum":
			Map<String, String> values = (Map<String, String>)((org.activiti.engine.impl.form.EnumFormType)formType).getInformation("values");
			convertedFormType = new EnumFormType(values);
			break;
		}
		return convertedFormType;
	}
}
