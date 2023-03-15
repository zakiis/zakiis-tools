package com.zakiis.keycloak.sdk;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.PublicKey;
import java.security.interfaces.RSAKey;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.zakiis.keycloak.sdk.domain.constants.KeyCloackEndPoints;
import com.zakiis.keycloak.sdk.domain.dto.IDToken;
import com.zakiis.keycloak.sdk.domain.dto.Token;
import com.zakiis.keycloak.sdk.domain.dto.UMA2Configuration;
import com.zakiis.keycloak.sdk.exception.InvalidTokenException;
import com.zakiis.keycloak.sdk.exception.KeyCloakSDKException;
import com.zakiis.keycloak.sdk.util.Base64Util;
import com.zakiis.keycloak.sdk.util.HttpUtil;
import com.zakiis.keycloak.sdk.util.JsonUtil;
import com.zakiis.keycloak.sdk.util.RSAKeyUtil;
import com.zakiis.keycloak.sdk.util.ValidationUtil;

public class KeycloakContext {
	
	Logger log = LoggerFactory.getLogger(KeycloakContext.class);
	
	String authURL;
	String realm;
	String clientId;
	boolean publicClient;
	String clientSecret;
	UMA2Configuration umaConfiguration;
	final Map<String, Key> keyMap = new HashMap<String, Key>();

	/**
	 * Constructor
	 * @param authURL get end point of keycloak
	 * @param realm
	 * @param clientId
	 * @param publicClient if false, need provider secret
	 * @param clientSecret
	 */
	public KeycloakContext(String authURL, String realm, String clientId, boolean publicClient, String clientSecret) {
		if (!publicClient) {
			ValidationUtil.notBlank(clientSecret, "Client secret");
		}
		this.authURL = authURL;
		this.realm = realm;
		this.clientId = clientId;
		this.clientSecret = clientSecret;
		this.publicClient = publicClient;
		init();
	}
	
	public Token login(String username, String password) {
		String url = umaConfiguration.getTokenEndpoint();
		String data = null;
		if (publicClient) {
			data = String.format("grant_type=password&client_id=%s&username=%s&password=%s", clientId, username, password);
		} else {
			data = String.format("grant_type=password&client_id=%s&client_secret=%s&username=%s&password=%s", clientId
					, clientSecret, username, password);	
		}
		String body = HttpUtil.post(url, data);
		Token token = JsonUtil.toObject(body, Token.class);
		return token;
	}
	
	public void logout(String refreshToken) {
		String url = umaConfiguration.getEndSessionEndpoint();
		String params = null;
		if (publicClient) {
			params = String.format("client_id=%s&refresh_token=%s", clientId, refreshToken);
		} else {
			params = String.format("client_id=%s&client_secret=%s&refresh_token=%s", clientId, clientSecret, refreshToken);
		}
		String data = HttpUtil.post(url, params);
		System.out.println("logout, data:" + data);
	}
	
	/**
	 * generate web login url
	 * @param redirectURL after login done in Keycloak, the browser will redirect to this URL.
	 * @return
	 */
	public String genLoginURL(String redirectURL) {
		String url = umaConfiguration.getAuthorizationEndpoint();
		String params = null;
		if (publicClient) {
			params = String.format("response_type=code&client_id=%s&redirect_uri=%s&state=%s&login=true&scope=openid"
					, clientId, redirectURL, UUID.randomUUID().toString());
		} else {
			params = String.format("response_type=code&client_id=%s&client_secret=%s&redirect_uri=%s&state=%s&login=true&scope=openid"
					, clientId, clientSecret, redirectURL, UUID.randomUUID().toString());
		}
		return String.format("%s?%s", url, params);
	}
	
	public Token refreshToken(String refreshToken) {
		String url = umaConfiguration.getTokenEndpoint();
		String data = null;
		if (publicClient) {
			data = String.format("grant_type=refresh_token&client_id=%s&refresh_token=%s", clientId, refreshToken);
		} else {
			data = String.format("grant_type=refresh_token&client_id=%s&client_secret=%s&refresh_token=%s", clientId
					, clientSecret, refreshToken);	
		}
		String body = HttpUtil.post(url, data);
		Token token = JsonUtil.toObject(body, Token.class);
		return token;
	}
	
	public IDToken verify(String accessToken) {
		ValidationUtil.notBlank(accessToken, "Access token");
		DecodedJWT decodedJWT = JWT.decode(accessToken);
		if ("RS256".equals(decodedJWT.getAlgorithm())) {
			String keyId = decodedJWT.getKeyId();
			Key key = keyMap.get(keyId);
			JWTVerifier jwtVerifier = JWT.require(Algorithm.RSA256((RSAKey)key))
					.acceptLeeway(60)
					.build();
			try {
				jwtVerifier.verify(decodedJWT);
			} catch (JWTVerificationException e) {
				throw new InvalidTokenException(e.getMessage(), e);
			}
			IDToken idToken = JsonUtil.toObject(new String(Base64Util.decodeFromBase64URL(decodedJWT.getPayload()), StandardCharsets.UTF_8), IDToken.class);
			return idToken;
		} else {
			throw new KeyCloakSDKException("暂不支持其它签名方式，需要加代码");
		}
	}
	
	@SuppressWarnings("unchecked")
	private void init() {
		// init configuration URL
		String url = authURL + String.format(KeyCloackEndPoints.UMA2_CONFIGURATION, realm);
		String body = HttpUtil.get(url);
		umaConfiguration = JsonUtil.toObject(body, UMA2Configuration.class);
		// init key
		url = umaConfiguration.getJwksUri();
		body = HttpUtil.get(url);
		List<Map<String, Object>> keyList = ((Map<String, List<Map<String, Object>>>)JsonUtil.toObject(body, Map.class)).get("keys");
		for (Map<String, Object> keyStrMap : keyList) {
			if (!"RSA".equals(keyStrMap.get("kty"))) {
				continue;
			}
			String x5c = ((List<String>)keyStrMap.get("x5c")).get(0);
			PublicKey publicKey = RSAKeyUtil.extractPubKeyFromPEMEncodedCert(Base64Util.decode(x5c));
			keyMap.put((String)keyStrMap.get("kid"), publicKey);
		}
		
	}
}
