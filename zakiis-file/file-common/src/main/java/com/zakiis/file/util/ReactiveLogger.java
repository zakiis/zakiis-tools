package com.zakiis.file.util;

import org.slf4j.Logger;
import org.slf4j.MDC;

import com.zakiis.common.constants.CommonConstants;

/**
 * Reactive线程复用，一个方法可能会被多个线程交叉执行，依赖于ThreadLocal技术的MDC会出问题，traceId应当保存在业务方法中，每次打印日志时传递过来。
 * @author 10901
 */
public class ReactiveLogger {
	
	private Logger log;
	
	public ReactiveLogger(Logger log) {
		this.log = log;
	}

	public void debug(String traceId, String format, Object... arguments) {
		MDC.put(CommonConstants.TRACE_ID_PARAM_NAME, traceId);
		log.debug(format, arguments);
		MDC.clear();
	}
	
	public void info(String traceId, String format, Object... arguments) {
		MDC.put(CommonConstants.TRACE_ID_PARAM_NAME, traceId);
		log.info(format, arguments);
		MDC.clear();
	}
	
	public void warn(String traceId, String format, Object... arguments) {
		MDC.put(CommonConstants.TRACE_ID_PARAM_NAME, traceId);
		log.warn(format, arguments);
		MDC.clear();
	}
	
	public void error(String traceId, String format, Object... arguments) {
		MDC.put(CommonConstants.TRACE_ID_PARAM_NAME, traceId);
		log.error(format, arguments);
		MDC.clear();
	}
}
