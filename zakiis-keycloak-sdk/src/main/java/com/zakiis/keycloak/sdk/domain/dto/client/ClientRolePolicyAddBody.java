package com.zakiis.keycloak.sdk.domain.dto.client;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class ClientRolePolicyAddBody {

    private String name;
    private String description;
    private String logic = "POSITIVE";
    private List<ClientPolicyRole> roles;
    
    
}
