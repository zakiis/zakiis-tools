package com.zakiis.file.exception.handler;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.zakiis.file.domain.dto.ResponseDTO;
import com.zakiis.file.exception.CommonError;
import com.zakiis.file.exception.ServiceError;
import com.zakiis.file.exception.ServiceException;
import com.zakiis.file.filter.ExceptionFilter;
import com.zakiis.file.util.ReactiveLogger;
import com.zakiis.security.exception.NoPermissionException;

/**
 * Exception handler for reactive service, {@link ExceptionFilter}
 * @author 10901
 */
public class ReactiveExceptionHandler {
	
	ReactiveLogger log = new ReactiveLogger(LoggerFactory.getLogger(ReactiveExceptionHandler.class));

	public ResponseEntity<ResponseDTO<Object>> handleError(String traceId, Throwable e) {
		if (e instanceof ServiceException) {
			return serviceException(traceId, (ServiceException)e);
		} else if (e instanceof ServiceError) {
			return serviceError(traceId, (ServiceError)e);
		}  else if (e instanceof NoPermissionException) {
			return noPermissionException(traceId, (NoPermissionException)e);
		} else {
			return throwable(traceId, (Throwable)e);
		}
	}
	
	private ResponseEntity<ResponseDTO<Object>> serviceException(String traceId, ServiceException e) {
		log.warn(traceId, "{}: code:{}, message:{}\n\tat {}", e.getClass().getSimpleName(), e.getCode()
				, e.getMessage(), getStackTrace(e, 3));
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.contentType(MediaType.APPLICATION_JSON)
					.body(ResponseDTO.buildErrorResponse(e));
	}
	
	private ResponseEntity<ResponseDTO<Object>> serviceError(String traceId, ServiceError e) {
		log.error(traceId, "got an service error", e);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.contentType(MediaType.APPLICATION_JSON)
					.body(ResponseDTO.buildErrorResponse(e));
	}
	
	private ResponseEntity<ResponseDTO<Object>> noPermissionException(String traceId, NoPermissionException e) {
		log.warn(traceId, "Unauthorized request, {}", e.getMessage());
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.contentType(MediaType.APPLICATION_JSON)
					.body(ResponseDTO.buildErrorResponse(CommonError.UNAUTHORIZED));
	}
	
	private ResponseEntity<ResponseDTO<Object>> throwable(String traceId, Throwable e) {
		log.error(traceId, "got an uncaught exception", e);
		ResponseDTO<Object> dto = ResponseDTO.buildErrorResponse(CommonError.UNKNOWN_ERROR);
		if (StringUtils.isNotEmpty(e.getMessage())) {
			dto.setMessage(e.getMessage());
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.contentType(MediaType.APPLICATION_JSON)
					.body(dto);
	}

	private Object getStackTrace(Throwable e, int lineCount) {
		if (e != null && e.getStackTrace() != null && e.getStackTrace().length > 0) {
			List<String> stackTraces = new ArrayList<String>();
			for (int i = 0; i < e.getStackTrace().length && i < lineCount; i++) {
				stackTraces.add(e.getStackTrace()[i].toString());
			}
			return StringUtils.join(stackTraces, "\n\t");
		}
		return null;
	}
}
