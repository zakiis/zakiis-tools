package com.zakiis.keycloak.sdk.util;

import java.io.ByteArrayInputStream;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

public class RSAKeyUtil {

	static final String X509 = "X.509";
	
	
	public static PublicKey extractPubKeyFromPEMEncodedCert(byte[] certPemBytes) {
		try {
			CertificateFactory factory = CertificateFactory.getInstance(X509);
			X509Certificate cert = (X509Certificate)factory.generateCertificate(new ByteArrayInputStream(certPemBytes));
			PublicKey publicKey = cert.getPublicKey();
			return publicKey;
		} catch(CertificateException e) {
			throw new IllegalArgumentException(e.getMessage(), e);
		}
	}
	
}
