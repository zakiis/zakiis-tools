package com.zakiis.keycloak.sdk.domain.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@ToString
public class Credential {

	private String type;
    private String value;
    private boolean temporary;
}
