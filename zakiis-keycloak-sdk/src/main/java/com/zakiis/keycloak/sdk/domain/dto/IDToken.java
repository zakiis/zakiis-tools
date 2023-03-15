package com.zakiis.keycloak.sdk.domain.dto;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class IDToken {

	String preferredUsername;
	String givenName;
	String familyName;
	String email;
	String sid;
	String scope;
	boolean emailVerified;
	String sessionState;
	/** authorized party */
	String azp;
	/** type of token */
	String typ;
	/** issue at (seconds since Unix epoch)*/
	int iat;
	/** expire at (seconds since Unix epoch*/
	int exp;
	/** who or what the token is intended for*/
	String aud;
	/** subject - whom the token refers to */
	String sub;
	/** JWT id (unique identifier for this token) */
	String jti;
	/** 全局权限 */
	Access realmAccess;
	/** 用户在每个应用的权限*/
	Map<String, Access> resourceAccess;

	public static class Access {
		
		private List<String> roles;
		public List<String> getRoles() {
			return roles;
		}
		public void setRoles(List<String> roles) {
			this.roles = roles;
		}
	}
	

	public String getPreferredUsername() {
		return preferredUsername;
	}

	public void setPreferredUsername(String preferredUsername) {
		this.preferredUsername = preferredUsername;
	}

	public String getGivenName() {
		return givenName;
	}

	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}

	public String getFamilyName() {
		return familyName;
	}

	public void setFamilyName(String familyName) {
		this.familyName = familyName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public boolean isEmailVerified() {
		return emailVerified;
	}

	public void setEmailVerified(boolean emailVerified) {
		this.emailVerified = emailVerified;
	}

	public String getSessionState() {
		return sessionState;
	}

	public void setSessionState(String sessionState) {
		this.sessionState = sessionState;
	}

	public String getAzp() {
		return azp;
	}

	public void setAzp(String azp) {
		this.azp = azp;
	}

	public String getTyp() {
		return typ;
	}

	public void setTyp(String typ) {
		this.typ = typ;
	}

	public int getIat() {
		return iat;
	}

	public void setIat(int iat) {
		this.iat = iat;
	}

	public int getExp() {
		return exp;
	}

	public void setExp(int exp) {
		this.exp = exp;
	}

	public String getAud() {
		return aud;
	}

	public void setAud(String aud) {
		this.aud = aud;
	}

	public String getSub() {
		return sub;
	}

	public void setSub(String sub) {
		this.sub = sub;
	}

	public String getJti() {
		return jti;
	}

	public void setJti(String jti) {
		this.jti = jti;
	}

	public Access getRealmAccess() {
		return realmAccess;
	}

	public void setRealmAccess(Access realmAccess) {
		this.realmAccess = realmAccess;
	}

	public Map<String, Access> getResourceAccess() {
		return resourceAccess;
	}

	public void setResourceAccess(Map<String, Access> resourceAccess) {
		this.resourceAccess = resourceAccess;
	}
}
