package com.zakiis.keycloak.sdk.domain.dto;

import java.util.List;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UMA2Configuration {

	String issuer;
	String authorizationEndpoint;
	String tokenEndpoint;
	String introspectionEndpoint;
	String endSessionEndpoint;
	String jwksUri;
	String registrationEndpoint;
	String resourceRegistrationEndpoint;
	String permissionEndpoint;
	String policyEndpoint;
	boolean frontchannelLogoutSessionSupported;
	boolean frontchannelLogoutSupported;
	List<String> grantTypesSupported;
	List<String> responseTypesSupported;
	List<String> responseModesSupported;
	List<String> tokenEndpointAuthMethodsSupported;
	List<String> tokenEndpointAuthSigningAlgValuesSupported;
	List<String> scopesSupported;
	
	public String getIssuer() {
		return issuer;
	}
	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}
	public String getAuthorizationEndpoint() {
		return authorizationEndpoint;
	}
	public void setAuthorizationEndpoint(String authorizationEndpoint) {
		this.authorizationEndpoint = authorizationEndpoint;
	}
	public String getTokenEndpoint() {
		return tokenEndpoint;
	}
	public void setTokenEndpoint(String tokenEndpoint) {
		this.tokenEndpoint = tokenEndpoint;
	}
	public String getIntrospectionEndpoint() {
		return introspectionEndpoint;
	}
	public void setIntrospectionEndpoint(String introspectionEndpoint) {
		this.introspectionEndpoint = introspectionEndpoint;
	}
	public String getEndSessionEndpoint() {
		return endSessionEndpoint;
	}
	public void setEndSessionEndpoint(String endSessionEndpoint) {
		this.endSessionEndpoint = endSessionEndpoint;
	}
	public String getJwksUri() {
		return jwksUri;
	}
	public void setJwksUri(String jwksUri) {
		this.jwksUri = jwksUri;
	}
	public String getRegistrationEndpoint() {
		return registrationEndpoint;
	}
	public void setRegistrationEndpoint(String registrationEndpoint) {
		this.registrationEndpoint = registrationEndpoint;
	}
	public String getResourceRegistrationEndpoint() {
		return resourceRegistrationEndpoint;
	}
	public void setResourceRegistrationEndpoint(String resourceRegistrationEndpoint) {
		this.resourceRegistrationEndpoint = resourceRegistrationEndpoint;
	}
	public String getPermissionEndpoint() {
		return permissionEndpoint;
	}
	public void setPermissionEndpoint(String permissionEndpoint) {
		this.permissionEndpoint = permissionEndpoint;
	}
	public String getPolicyEndpoint() {
		return policyEndpoint;
	}
	public void setPolicyEndpoint(String policyEndpoint) {
		this.policyEndpoint = policyEndpoint;
	}
	public boolean isFrontchannelLogoutSessionSupported() {
		return frontchannelLogoutSessionSupported;
	}
	public void setFrontchannelLogoutSessionSupported(boolean frontchannelLogoutSessionSupported) {
		this.frontchannelLogoutSessionSupported = frontchannelLogoutSessionSupported;
	}
	public boolean isFrontchannelLogoutSupported() {
		return frontchannelLogoutSupported;
	}
	public void setFrontchannelLogoutSupported(boolean frontchannelLogoutSupported) {
		this.frontchannelLogoutSupported = frontchannelLogoutSupported;
	}
	public List<String> getGrantTypesSupported() {
		return grantTypesSupported;
	}
	public void setGrantTypesSupported(List<String> grantTypesSupported) {
		this.grantTypesSupported = grantTypesSupported;
	}
	public List<String> getResponseTypesSupported() {
		return responseTypesSupported;
	}
	public void setResponseTypesSupported(List<String> responseTypesSupported) {
		this.responseTypesSupported = responseTypesSupported;
	}
	public List<String> getResponseModesSupported() {
		return responseModesSupported;
	}
	public void setResponseModesSupported(List<String> responseModesSupported) {
		this.responseModesSupported = responseModesSupported;
	}
	public List<String> getTokenEndpointAuthMethodsSupported() {
		return tokenEndpointAuthMethodsSupported;
	}
	public void setTokenEndpointAuthMethodsSupported(List<String> tokenEndpointAuthMethodsSupported) {
		this.tokenEndpointAuthMethodsSupported = tokenEndpointAuthMethodsSupported;
	}
	public List<String> getTokenEndpointAuthSigningAlgValuesSupported() {
		return tokenEndpointAuthSigningAlgValuesSupported;
	}
	public void setTokenEndpointAuthSigningAlgValuesSupported(List<String> tokenEndpointAuthSigningAlgValuesSupported) {
		this.tokenEndpointAuthSigningAlgValuesSupported = tokenEndpointAuthSigningAlgValuesSupported;
	}
	public List<String> getScopesSupported() {
		return scopesSupported;
	}
	public void setScopesSupported(List<String> scopesSupported) {
		this.scopesSupported = scopesSupported;
	}
}
