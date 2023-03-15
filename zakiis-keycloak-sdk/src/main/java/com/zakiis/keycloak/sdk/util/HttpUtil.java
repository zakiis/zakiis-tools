package com.zakiis.keycloak.sdk.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zakiis.keycloak.sdk.exception.HttpException;
import com.zakiis.keycloak.sdk.exception.KeyCloakSDKException;

public class HttpUtil {

	final static int READ_TIMEOUT = 10 * 1000;
	final static int CONNECT_TIMEOUT = 10 * 1000;
	
	final static Logger log = LoggerFactory.getLogger(HttpUtil.class);
	
	public static String post(String url, String data) {
		try {
			HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
			conn.setReadTimeout(READ_TIMEOUT);
			conn.setConnectTimeout(CONNECT_TIMEOUT);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
			conn.setUseCaches(false);
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.connect();
			conn.getOutputStream().write(data.getBytes(StandardCharsets.UTF_8));
			int responseCode = conn.getResponseCode();
			InputStream is = null;
			if (responseCode < 400) {
				is = conn.getInputStream();
			} else {
				is = conn.getErrorStream();
			}
			ByteArrayOutputStream bos = new ByteArrayOutputStream(1024);
			byte[] buf = new byte[256];
			int len = 0;
			while ((len = is.read(buf)) > 0) {
				bos.write(buf, 0, len);
			}
			String body = new String(bos.toByteArray(), StandardCharsets.UTF_8);
			if (responseCode < 400) {
				return body;
			} else {
				log.warn("post {}, request data:{}, response code:{}, msg:{}", url, data, responseCode, body);
				throw new HttpException(responseCode, body);
			}
		} catch (IOException e) {
			throw new KeyCloakSDKException(String.format("call %s got an exception", url), e);
		}
	}
	
	public static String get(String url) {
		try {
			HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
			conn.setReadTimeout(READ_TIMEOUT);
			conn.setConnectTimeout(CONNECT_TIMEOUT);
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
			conn.setUseCaches(false);
			conn.setDoInput(true);
			conn.connect();
			int responseCode = conn.getResponseCode();
			InputStream is = null;
			if (responseCode < 400) {
				is = conn.getInputStream();
			} else {
				is = conn.getErrorStream();
			}
			ByteArrayOutputStream bos = new ByteArrayOutputStream(1024);
			byte[] buf = new byte[256];
			int len = 0;
			while ((len = is.read(buf)) > 0) {
				bos.write(buf, 0, len);
			}
			String body = new String(bos.toByteArray(), StandardCharsets.UTF_8);
			if (responseCode < 400) {
				return body;
			} else {
				log.warn("get {}, response code:{}, msg:{}", url, responseCode, body);
				throw new HttpException(responseCode, body);
			}
		} catch (IOException e) {
			throw new KeyCloakSDKException("call keycloak API got an exception", e);
		}
	}
}
