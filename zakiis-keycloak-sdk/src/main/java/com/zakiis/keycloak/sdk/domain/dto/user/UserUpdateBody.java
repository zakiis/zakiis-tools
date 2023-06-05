package com.zakiis.keycloak.sdk.domain.dto.user;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@ToString
@Accessors(chain = true)
public class UserUpdateBody {

	private String id;
	private String username;
	private String firstName;
	private String lastName;
	private String email;
	private Boolean emailVerified;
	private Boolean enabled;
	
}
