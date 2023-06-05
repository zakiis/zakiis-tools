package com.zakiis.keycloak.sdk.domain.dto.client;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientInfo {

	private String id;
    private String clientId;
    private String name;
    private String description;
    private boolean publicClient;
    private boolean serviceAccountsEnabled;
    private boolean directAccessGrantsEnabled;
    private boolean authorizationServicesEnabled;
}
