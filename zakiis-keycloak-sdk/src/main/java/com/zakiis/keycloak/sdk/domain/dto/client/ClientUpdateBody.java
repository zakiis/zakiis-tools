package com.zakiis.keycloak.sdk.domain.dto.client;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ClientUpdateBody {

	private String clientId;
    private String name;
    private String description;
    private boolean publicClient = true;
    private boolean serviceAccountsEnabled = false;
    private boolean directAccessGrantsEnabled = true;
    private boolean authorizationServicesEnabled = false;
    
}
