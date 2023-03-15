package com.zakiis.workflow.test.tool;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.zakiis.workflow.dto.FormPropertyDTO;
import com.zakiis.workflow.dto.form.FormType;

public class FormTestTool {

	public static Map<String, Object> inputParams(List<FormPropertyDTO> formProperties) {
		Map<String, Object> params = new HashMap<String, Object>();
		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(System.in);
		for (FormPropertyDTO formProperty : formProperties) {
			FormType type = formProperty.getType();
			if (type.getName().equals("date")) {
				System.out.print(String.format("please input %s(%s):", formProperty.getName(), type.getMetaData()));
			} else if (type.getName().equals("enum")) {
				System.out.print(String.format("please input %s(%s):", formProperty.getName(), type.getMetaData()));
			} else if (type.getName().equals("boolean")) {
				System.out.print(String.format("please input %s(true/false):", formProperty.getName()));
			} else {
				System.out.print(String.format("please input %s:", formProperty.getName()));
			}
			String formPropertyValue = scanner.next();
			Object modelValue = formProperty.getType().convertFormValueToModelValue(formPropertyValue);
			params.put(formProperty.getId(), modelValue);
		}
		//don't close scanner cause System.in will be closed too.
//		scanner.close();
		return params;
	}
}
