package com.zakiis.file.portal.util;

import java.nio.charset.StandardCharsets;

import com.zakiis.security.HMACUtil;
import com.zakiis.security.HMACUtil.HMACType;
import com.zakiis.security.codec.HexUtil;
import com.zakiis.security.exception.NoPermissionException;

public abstract class SignUtil {

	public static String generateSign(String ak, String sk, String method, String uri) {
		String strToSign = String.format("%s.%s.%s", ak, method, uri);
		String sign = HMACUtil.digestAsHex(strToSign.getBytes(StandardCharsets.UTF_8), HexUtil.toByteArray(sk), HMACType.HMAC_SHA_256);
		return sign;
	}
	
	public static void validateSign(String ak, String sk, String method, String uri, String sign) {
		String calcSign = generateSign(ak, sk, method, uri);
		if (!calcSign.equals(sign)) {
			throw new NoPermissionException(String.format("ak:%s, method:%s, uri:%s, passed sign:%s, calc sign:%s"
					, ak, method, uri, sign, calcSign));
		}
	}
}
