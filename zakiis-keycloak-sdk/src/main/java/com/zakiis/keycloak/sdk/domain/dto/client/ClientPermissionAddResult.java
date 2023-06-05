package com.zakiis.keycloak.sdk.domain.dto.client;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class ClientPermissionAddResult {

	private String id;
	private String name;
    private String description;
    private String decisionStrategy;
    private String logic;
    private String type;
    private List<String> policies;
    private List<String> resources;
    
}
