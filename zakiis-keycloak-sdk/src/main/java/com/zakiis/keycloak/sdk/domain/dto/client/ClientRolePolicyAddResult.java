package com.zakiis.keycloak.sdk.domain.dto.client;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientRolePolicyAddResult {

	private String id;
    private String name;
    private String description;
    private String type;
    private String logic = "POSITIVE";
    private String decisionStrategy;
    private List<ClientPolicyRole> roles;
    
    
}
