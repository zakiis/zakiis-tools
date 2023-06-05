package com.zakiis.keycloak.sdk.domain.dto.client;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class ClientRoleAddBody {
	
	private String name;
}
