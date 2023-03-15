package com.zakiis.workflow.util;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;

import com.zakiis.workflow.exception.WorkflowRuntimeException;

public class IOUtil {
	
	private static final int EOF = -1;
	private static final int DEFAULT_BUFFER_SIZE = 4096;

	public static String readAll(Reader reader) {
		StringBuilder builder = new StringBuilder();
		try {
			int n = 0;
			char[] buffer = new char[DEFAULT_BUFFER_SIZE];
			while (EOF != (n = reader.read(buffer))) {
				builder.append(buffer, 0, n);
			}
			return builder.toString();
		} catch (Exception e) {
			throw new WorkflowRuntimeException("read string from reader error", e);
		}
	}
	
	public static void copy(InputStream input, OutputStream output) {
		int n = 0;
		byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
		try {
			while (EOF != (n = input.read(buffer))) {
				output.write(buffer, 0, n);
			}
		} catch (Exception e) {
			throw new WorkflowRuntimeException("copy stream error", e);
		}
	}
}
