package com.zakiis.keycloak.sdk.domain.dto.realm;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@ToString
public class RealmAddReq {

	private String realm;
    private String displayName;
    private boolean loginWithEmailAllowed = true;
    private boolean enabled = true;
    private String sslRequired = "none";
    /** access token有效期 */
    private Integer accessTokenLifespan = 10 * 60;
    /** refresh token有效期 */
    private Integer ssoSessionIdleTimeout = 60 * 60;
}
