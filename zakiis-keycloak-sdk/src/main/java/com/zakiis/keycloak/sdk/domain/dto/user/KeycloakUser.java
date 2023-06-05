package com.zakiis.keycloak.sdk.domain.dto.user;

import java.util.List;

import com.zakiis.keycloak.sdk.domain.dto.Credential;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class KeycloakUser {
	
	private String id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private Boolean emailVerified;
    private Boolean enabled;
    private List<Credential> credentials;
    
}
