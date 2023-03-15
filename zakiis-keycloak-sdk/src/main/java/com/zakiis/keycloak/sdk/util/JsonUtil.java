package com.zakiis.keycloak.sdk.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class JsonUtil {
	
	static ObjectMapper mapper = new ObjectMapper();
	static Logger logger = LoggerFactory.getLogger(JsonUtil.class);
	
	static {
		mapper
			//.setVisibility(PropertyAccessor.ALL, Visibility.NONE)
			//.setVisibility(PropertyAccessor.FIELD, Visibility.ANY)
//			.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
			.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"))
			.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
			.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
			.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
	}
	
	public static String toJson(Object obj) {
		try {
			return mapper.writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			logger.error("to json error.", e);
		}
		return null;
	}
	
	public static <T> T toObject(String content, Class<T> valueType) {
		try {
			return mapper.readValue(content, valueType);
		} catch (JsonProcessingException e) {
			logger.error("to object error.", e);
		}
		return null;
	}
	
	public static <T> T toObject(String content, TypeReference<T> valueTypeRef) {
		try {
			return mapper.readValue(content, valueTypeRef);
		} catch (JsonProcessingException e) {
			logger.error("to object error.", e);
		}
		return null;
	}
	
	public static String getString(JsonNode node) {
        if (node == null || node.isNull()) {
            return null;
        }
        return node.asText(null);
    }
	
	public static Date getDate(JsonNode node) {
        if (node == null || node.isNull()) {
            return null;
        }
        String dateStr = node.asText();
        if (StringUtils.isEmpty(dateStr)) {
        	return null;
        }
        try {
			return mapper.getDateFormat().parse(dateStr);
		} catch (Exception e) {
			logger.error("to date error.", e);
		}
        return null;
    }
	
	public static List<String> getStringList(JsonNode node) {
        if (node == null || node.isNull() || !(node.isArray() || node.isTextual())) {
            return null;
        }
        if (node.isTextual() && !node.asText().isEmpty()) {
            return Collections.singletonList(node.asText());
        }
        List<String> list = new ArrayList<>(node.size());
        for (int i = 0; i < node.size(); i++) {
        	list.add(getString(node.get(i)));
        }
        return list;
    }

}
