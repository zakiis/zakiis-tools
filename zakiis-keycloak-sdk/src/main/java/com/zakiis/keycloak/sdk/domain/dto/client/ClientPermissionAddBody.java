package com.zakiis.keycloak.sdk.domain.dto.client;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class ClientPermissionAddBody {

	private String name;
    private String description;
    private String decisionStrategy = "UNANIMOUS";
    private List<String> policies;
    private List<String> resources;
    
}
