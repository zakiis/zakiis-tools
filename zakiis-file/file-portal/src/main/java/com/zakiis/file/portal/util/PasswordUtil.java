package com.zakiis.file.portal.util;

import java.nio.charset.StandardCharsets;

import com.zakiis.security.AESUtil;
import com.zakiis.security.codec.HexUtil;

public class PasswordUtil {

	/**
	 * generate encrypted password
	 * @param password password in plain text
	 * @param secretKey AES256 secret key
	 * @param iv AES256 initial vector
	 * @return password in AES/CBC/PKCS7Padding encrypted format
	 */
	public static String genEncryptedPassword(String password, String secretKey, String iv) {
		return HexUtil.toHexString(AESUtil.encrypt(password.getBytes(StandardCharsets.UTF_8),
				HexUtil.toByteArray(secretKey), HexUtil.toByteArray(iv)));
	}
}
