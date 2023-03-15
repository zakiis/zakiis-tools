package com.zakiis.keycloak.sdk;

import org.junit.jupiter.api.Test;

import com.zakiis.keycloak.sdk.domain.dto.IDToken;
import com.zakiis.keycloak.sdk.domain.dto.Token;
import com.zakiis.keycloak.sdk.util.JsonUtil;

public class KeycloakContextTest {

	@Test
	public void testLogin() throws InterruptedException {
		KeycloakContext context = new KeycloakContext("https://keycloak.cimbbank.com.ph", "cimb", "rcms", false, "uiPRlBB7SgHCTvava4uba3fS5kEWCuC9");
		Token token = context.login("liuzhenghua312", "123456");
		IDToken idToken = context.verify(token.getAccessToken());
		System.out.println(JsonUtil.toJson(idToken));
		Thread.sleep(2000L);
		Token token2 = context.refreshToken(token.getRefreshToken());
		IDToken idToken2 = context.verify(token2.getAccessToken());
		System.out.println(JsonUtil.toJson(idToken2));
	}
	
	@Test
	public void testGenLoginURL() {
		KeycloakContext context = new KeycloakContext("https://keycloak.cimbbank.com.ph", "cimb", "rcms", false, "uiPRlBB7SgHCTvava4uba3fS5kEWCuC9");
		String url = context.genLoginURL("http://rcms.cimbbank.com.ph:8080/health");
		System.out.println(url);
	}
}
