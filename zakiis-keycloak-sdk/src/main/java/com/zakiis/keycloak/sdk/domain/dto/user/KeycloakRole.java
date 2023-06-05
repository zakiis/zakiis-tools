package com.zakiis.keycloak.sdk.domain.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class KeycloakRole {
    private String id;

    private String name;

    private String description;

    private boolean clientRole = false;
}
