package com.zakiis.keycloak.sdk.domain.dto.user;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@ToString
@Accessors(chain = true)
public class UserClientRoleAddBody {

	/** role id */
	private String id;
	/** role name */
	private String name;
}
