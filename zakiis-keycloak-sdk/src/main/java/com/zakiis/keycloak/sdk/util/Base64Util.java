package com.zakiis.keycloak.sdk.util;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;

public class Base64Util {
	
	/**
     * This array is a lookup table that translates 6-bit positive integer
     * index values into their "Base64 Alphabet" equivalents as specified
     * in "Table 1: The Base64 Alphabet" of RFC 2045 (and RFC 4648).
     */
	private static final char[] toBase64 = {
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'
        };
	
	/**
     * It's the lookup table for "URL and Filename safe Base64" as specified
     * in Table 2 of the RFC 4648, with the '+' and '/' changed to '-' and
     * '_'. This table is used when BASE64_URL is specified.
     */
    private static final char[] toBase64URL = {
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
        'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
        'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
        'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-', '_'
    };
    
    /**
     * Lookup table for decoding unicode characters drawn from the
     * "Base64 Alphabet" (as specified in Table 1 of RFC 2045) into
     * their 6-bit positive integer equivalents.  Characters that
     * are not in the Base64 alphabet but fall within the bounds of
     * the array are encoded to -1.
     *
     */
    private static final int[] fromBase64 = new int[256];
    static {
        Arrays.fill(fromBase64, -1);
        for (int i = 0; i < toBase64.length; i++)
            fromBase64[toBase64[i]] = i;
        fromBase64['='] = -2;
    }
    
    /**
     * Lookup table for decoding "URL and Filename safe Base64 Alphabet"
     * as specified in Table2 of the RFC 4648.
     */
    private static final int[] fromBase64URL = new int[256];

    static {
        Arrays.fill(fromBase64URL, -1);
        for (int i = 0; i < toBase64URL.length; i++)
            fromBase64URL[toBase64URL[i]] = i;
        fromBase64URL['='] = -2;
    }
    
    public static String encode(byte[] src) {
		return encode(src, toBase64);
	}
    
    public static String encodeToBase64URL(byte[] src) {
    	String base64 = encode(src, toBase64URL);
    	int i = base64.length();
    	while (base64.charAt(i - 1) == '=') {
    		i--;
    	}
    	return base64.substring(0, i);
	}
    
    private static String encode(byte[] src, char[] base64) {
    	int slen = src.length / 3 * 3;
		int len = encodeOutLength(src.length);
		char[] dst = new char[len];
		int sp = 0, dp = 0;
		for (; sp < slen;) {
			int bits = (src[sp++] & 0xff) << 16 |
					(src[sp++] & 0xff) << 8 |
					(src[sp++] & 0xff);
			dst[dp++] = base64[bits >>> 18 & 0x3f];
			dst[dp++] = base64[bits >>> 12 & 0x3f];
			dst[dp++] = base64[bits >>> 6 & 0x3f];
			dst[dp++] = base64[bits & 0x3f];
		}
		if (sp < src.length) {
			int b0 = src[sp++] & 0xff;
			dst[dp++] = base64[b0 >> 2];
			if (sp == src.length) {
				dst[dp++] = base64[(b0 << 4) & 0x3f];
				dst[dp++] = '=';
				dst[dp++] = '=';
			} else {
				int b1 = src[sp++] & 0xff;
				dst[dp++] = base64[(b0 << 4) & 0x3f | b1 >> 4];
				dst[dp++] = base64[(b1 << 2) & 0x3f];
				dst[dp++] = '=';
			}
		}
		return new String(dst);
    }
    
    public static byte[] decode(String src) {
		return decode(src.getBytes(), fromBase64);
	}
    
    public static byte[] decodeFromBase64URL(String src) {
    	int repeat = 4 - src.length() % 4;
    	if (repeat != 0) {
    		src += StringUtils.repeat('=', repeat);
    	}
		return decode(src.getBytes(), fromBase64URL);
	}
    
    private static byte[] decode(byte[] src, int[] base64) {
    	int outLength = decodeOutLength(src);
    	byte[] dest = new byte[outLength];
    	int sp = 0, dp = 0, shift = 18, bits = 0;
    	for (; sp < src.length;) {
    		int b = src[sp++] & 0xff;
    		if ((b = base64[b]) < 0) {
    			if (b != -2) {
    				continue;
    			}
    			break;
    		}
    		bits |= b << shift;
    		shift -= 6;
    		if (shift < 0) {
    			dest[dp++] = (byte)(bits >> 16);
    			dest[dp++] = (byte)(bits >> 8);
    			dest[dp++] = (byte)(bits);
    			shift = 18;
    			bits = 0;
    		}
    	}
    	if (shift == 0) {//最后是两个字符，所以有3个base64字符组成
    		dest[dp++] = (byte)(bits >> 16);
    		dest[dp++] = (byte)(bits >> 8);
    	} else if (shift == 6) {//最后是一个字符，由2个base64字符组成
    		dest[dp++] = (byte)(bits >> 16);
    	} else if (shift == 12) {
    		throw new IllegalArgumentException("Last unit doesn't have enough bits");
    	}
    	return dest;
    }
	
	private static int encodeOutLength(int len) {
		if (len % 3 > 0) {
			return (len / 3 + 1) * 4;
		} else {
			return len / 3 * 4;
		}
	}
	
	private static int decodeOutLength(byte[] src) {
		if (src.length % 4 != 0) {
			throw new IllegalArgumentException("The content are not well padding.");
		}
		int len = src.length / 4 * 3;
		if (src[src.length - 1] == '=') {
			len--;
		}
		if (src[src.length - 2] == '=') {
			len--;
		}
		return len;
	}

}