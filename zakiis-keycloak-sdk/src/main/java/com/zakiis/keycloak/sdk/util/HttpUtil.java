package com.zakiis.keycloak.sdk.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zakiis.keycloak.sdk.exception.HttpException;
import com.zakiis.keycloak.sdk.exception.KeyCloakSDKException;

public class HttpUtil {

	final static int READ_TIMEOUT = 10 * 1000;
	final static int CONNECT_TIMEOUT = 10 * 1000;
	
	final static Logger log = LoggerFactory.getLogger(HttpUtil.class);
	
	public static String get(String url, Map<String, String> headers) {
		return request(url, "GET", null, headers, null);
	}
	
	public static String delete(String url, Map<String, String> headers) {
		return request(url, "DELETE", null, headers, null);
	}
	
	public static String delete(String url, String data, Map<String, String> headers) {
		return request(url, "DELETE", data, headers, null);
	}
	
	public static String post(String url, String data, Map<String, String> headers) {
		return request(url, "POST", data, headers, null);
	}
	
	public static String post(String url, String data, Map<String, String> headers
			, Consumer<Map<String,List<String>>> headerConsumer) {
		return request(url, "POST", data, headers, headerConsumer);
	}
	
	public static String put(String url, String data, Map<String, String> headers) {
		return request(url, "PUT", data, headers, null);
	}
	
	public static String request(String url, String method, String data, Map<String, String> headers
			, Consumer<Map<String,List<String>>> headerConsumer) {
		try {
			HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
			ignoreSSL(conn);
			conn.setReadTimeout(READ_TIMEOUT);
			conn.setConnectTimeout(CONNECT_TIMEOUT);
			conn.setRequestMethod(method);
			conn.setUseCaches(false);
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
			if (headers != null) {
				for (Map.Entry<String, String> entry : headers.entrySet()) {
					conn.setRequestProperty(entry.getKey(), entry.getValue());
				}	
			}
			conn.connect();
			if (data != null) {
				conn.getOutputStream().write(data.getBytes(StandardCharsets.UTF_8));
			}
			int responseCode = conn.getResponseCode();
			if (headerConsumer != null) {
				headerConsumer.accept(conn.getHeaderFields());
			}
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
				log.debug("{} {}, request data:{}, response code:{}, msg:{}", method, url, data, responseCode, body);
				return body;
			} else {
				log.warn("{} {}, request data:{}, response code:{}, msg:{}", method, url, data, responseCode, body);
				throw new HttpException(responseCode, body);
			}
		} catch (IOException e) {
			throw new KeyCloakSDKException(String.format("call %s got an exception", url), e);
		}
	}
	
//	public static String get(String url, Map<String, String> headers) {
//		try {
//			HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
//			ignoreSSL(conn);
//			conn.setReadTimeout(READ_TIMEOUT);
//			conn.setConnectTimeout(CONNECT_TIMEOUT);
//			conn.setRequestMethod("GET");
//			conn.setUseCaches(false);
//			conn.setDoInput(true);
//			conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
//			if (headers != null) {
//				for (Map.Entry<String, String> entry : headers.entrySet()) {
//					conn.setRequestProperty(entry.getKey(), entry.getValue());
//				}	
//			}
//			conn.connect();
//			int responseCode = conn.getResponseCode();
//			InputStream is = null;
//			if (responseCode < 400) {
//				is = conn.getInputStream();
//			} else {
//				is = conn.getErrorStream();
//			}
//			ByteArrayOutputStream bos = new ByteArrayOutputStream(1024);
//			byte[] buf = new byte[256];
//			int len = 0;
//			while ((len = is.read(buf)) > 0) {
//				bos.write(buf, 0, len);
//			}
//			String body = new String(bos.toByteArray(), StandardCharsets.UTF_8);
//			if (responseCode < 400) {
//				return body;
//			} else {
//				log.warn("get {}, response code:{}, msg:{}", url, responseCode, body);
//				throw new HttpException(responseCode, body);
//			}
//		} catch (IOException e) {
//			throw new KeyCloakSDKException("call keycloak API got an exception", e);
//		}
//	}

	private static void ignoreSSL(HttpURLConnection conn) {
		if (conn instanceof HttpsURLConnection) {
			HttpsURLConnection httpsConn = (HttpsURLConnection)conn;
			try {
				SSLContext context = SSLContext.getInstance("SSL");
				context.init(null, new TrustManager[] {
					new X509TrustManager() {
						
						@Override
						public X509Certificate[] getAcceptedIssuers() {
							return new X509Certificate[0];
						}
						
						@Override
						public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
						}
						
						@Override
						public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
						}
					}
				}, new SecureRandom());
				httpsConn.setSSLSocketFactory(context.getSocketFactory());
				httpsConn.setHostnameVerifier(new HostnameVerifier() {
					@Override
					public boolean verify(String hostname, SSLSession session) {
						return true;
					}
				});
			} catch (Exception e) {
				log.warn("忽略证书校验发生异常", e);
			}
		}
	}
}
