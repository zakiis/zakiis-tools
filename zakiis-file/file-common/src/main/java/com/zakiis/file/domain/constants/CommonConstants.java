package com.zakiis.file.domain.constants;

public interface CommonConstants {

	String SUCCESS_CODE = "000000";
	String SUCCESS_MESSAGE = "success";
	
	String ERROR_CODE = "999999";
	String ERROR_MESSAGE = "System busy, please try it later";
	
	/** API called from web page should put this parameter into HTTP header in JWT format */
	String JWT_HEADER_NAME = "Authorization";
	String JWT_HEADER_VALUE_PREFIX = "Bearer ";
	/** JWT header in this format: Authorization: Bearer <token> */
	String JWT_HEADER_VALUE_FORMAT = "Bearer %s";
	/** external channel call API should put this parameter into HTTP header */
	String X_ACCESS_KEY = "X-ACCESS-KEY";
	/** GET方式调用接口，URL中用此参数*/
	String AK = "ak";
	/** header中用此参数*/
	String X_SIGN = "X-SIGN";
	/** GET方式调用接口，URL中用此参数*/
	String SIGN = "sign";
	
	Integer STATUS_INACTIVE = 0;
	Integer STATUS_ACTIVE = 1;
	
	Long DEFAULT_PAGE_INDEX = 1L;
	Integer DEFAULT_PAGE_SIZE = 20;
	
	String DEFAULT_USER = "SYSTEM";
}
