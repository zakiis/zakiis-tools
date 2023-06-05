package com.zakiis.keycloak.sdk;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.zakiis.keycloak.sdk.domain.dto.Credential;
import com.zakiis.keycloak.sdk.domain.dto.Token;
import com.zakiis.keycloak.sdk.domain.dto.client.ClientAddBody;
import com.zakiis.keycloak.sdk.domain.dto.client.ClientInfo;
import com.zakiis.keycloak.sdk.domain.dto.client.ClientPermissionAddBody;
import com.zakiis.keycloak.sdk.domain.dto.client.ClientPermissionAddResult;
import com.zakiis.keycloak.sdk.domain.dto.client.ClientPolicyRole;
import com.zakiis.keycloak.sdk.domain.dto.client.ClientRoleAddBody;
import com.zakiis.keycloak.sdk.domain.dto.client.ClientRolePolicyAddBody;
import com.zakiis.keycloak.sdk.domain.dto.client.ClientRolePolicyAddResult;
import com.zakiis.keycloak.sdk.domain.dto.client.ClientUpdateBody;
import com.zakiis.keycloak.sdk.domain.dto.realm.RealmAddReq;
import com.zakiis.keycloak.sdk.domain.dto.user.KeycloakRole;
import com.zakiis.keycloak.sdk.domain.dto.user.KeycloakUser;
import com.zakiis.keycloak.sdk.domain.dto.user.UserAddBody;
import com.zakiis.keycloak.sdk.domain.dto.user.UserClientRoleAddBody;
import com.zakiis.keycloak.sdk.domain.dto.user.UserUpdateBody;
import com.zakiis.keycloak.sdk.exception.KeyCloakSDKException;
import com.zakiis.keycloak.sdk.util.HttpUtil;
import com.zakiis.keycloak.sdk.util.JsonUtil;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class KeycloakManager {
	
	public static final String MASTER_REALM = "master";
	public static final String ADM_CLI = "admin-cli";
	private static final String REALM_MGT_CLI = "realm-management";
	private static final String REALM_ADMIN_ROLE = "realm-admin";
	
	private final String authUrl;
	private final String realm;
	private final boolean publicClient;
	private final String clientSecret;
	private KeycloakContext context = null;
	
	public synchronized void init() {
		if (context == null) {
			context = new KeycloakContext(authUrl, realm, ADM_CLI, publicClient, clientSecret);
		}
	}

	/**
	 * Add a new realm
	 * more field you can refer from keycloak admin page. for example, when you add realm in chrome ,you can press F12 to view the payload of the request.
	 * @param req
	 */
	public void addRealm(RealmAddReq req) {
		init();
		Token token = context.clientLogin();
		String url = authUrl + "/admin/realms";
		HttpUtil.post(url, JsonUtil.toJson(req), buildHeader(token.getAccessToken()));
	}
	
	public Credential enableClientAuth(String realm, String clientId) {
		init();
		Token token = context.clientLogin();
		String url = authUrl + String.format("/admin/realms/%s/clients/%s", realm, clientId);
		ClientUpdateBody updateBody = new ClientUpdateBody()
			.setPublicClient(false)
			.setAuthorizationServicesEnabled(true)
			.setServiceAccountsEnabled(true);
		HttpUtil.put(url, JsonUtil.toJson(updateBody), buildHeader(token.getAccessToken()));
		url = url + "/client-secret";
		String body = HttpUtil.post(url, null, buildHeader(token.getAccessToken()));
		return JsonUtil.toObject(body, Credential.class);
	}
	
	/**
	 * add client
	 * @param addBody
	 * @param realm
	 * @return client.id, note that client.id is different from client.clientId
	 */
	public String addClient(ClientAddBody addBody, String realm) {
		init();
		Token token = context.clientLogin();
		String url = authUrl + String.format("/admin/realms/%s/clients", realm);
		AtomicReference<String> clientIdRefer = new AtomicReference<String>(null);
		HttpUtil.post(url, JsonUtil.toJson(addBody), buildHeader(token.getAccessToken()), headers -> {
			String location = getFirst(headers, "Location");
			if (location != null) {
	            int index = location.lastIndexOf("/");
	            clientIdRefer.set(location.substring(index + 1));
	        }
		});
		return clientIdRefer.get();
	}
	
	/**
	 * 配置client权限
	 * @param realm
	 * @param clientId client.id
	 */
	public void configureWhitelistAuthorization(String realm, String clientId) {
		// 添加role, roleName为自己定义的一个值
		String roleName = "client-grant";
		ClientRoleAddBody roleAddBody = new ClientRoleAddBody()
			.setName(roleName);
		String roleId = addClientRole(roleAddBody, realm, clientId);
		// 添加role类型的policy
		ClientRolePolicyAddBody rolePolicyAddBody = new ClientRolePolicyAddBody()
			.setName("white-list-policy")
			.setRoles(Arrays.asList(new ClientPolicyRole().setId(roleId)));
		ClientRolePolicyAddResult rolePolicyAddResult = addClientRolePolicy(rolePolicyAddBody, realm, clientId);
		// 添加permission
		ClientPermissionAddBody permissionAddBody = new ClientPermissionAddBody()
			.setName("white-list-permission")
			.setPolicies(Arrays.asList(rolePolicyAddResult.getId()))
			.setResources(Arrays.asList("Default Resource"));
		addClientPermission(permissionAddBody, realm, clientId);
	}
	
	/**
	 * add client role
	 * @param addBody
	 * @param realm
	 * @param clientId client.id
	 * @return client role id
	 */
	public String addClientRole(ClientRoleAddBody addBody, String realm, String clientId) {
		init();
		Token token = context.clientLogin();
		String url = authUrl + String.format("/admin/realms/%s/clients/%s/roles", realm, clientId);
		HttpUtil.post(url, JsonUtil.toJson(addBody), buildHeader(token.getAccessToken()));
		KeycloakRole clientRole = getClientRoleByName(realm, clientId, addBody.getName());
		return clientRole.getId();
	}
	
	public KeycloakRole getClientRoleByName(String realm, String clientId, String roleName) {
		init();
		Token token = context.clientLogin();
		String url = authUrl + String.format("/admin/realms/%s/clients/%s/roles/%s", realm, clientId, roleName);
		String bodyStr = HttpUtil.get(url, buildHeader(token.getAccessToken()));
		return JsonUtil.toObject(bodyStr, KeycloakRole.class);
	}
	
	public ClientRolePolicyAddResult addClientRolePolicy(ClientRolePolicyAddBody addBody, String realm, String clientId) {
		init();
		Token token = context.clientLogin();
		String url = authUrl + String.format("/admin/realms/%s/clients/%s/authz/resource-server/policy/role", realm, clientId);
		String bodyStr = HttpUtil.post(url, JsonUtil.toJson(addBody), buildHeader(token.getAccessToken()));
		return JsonUtil.toObject(bodyStr, ClientRolePolicyAddResult.class);
	}
	
	public ClientPermissionAddResult addClientPermission(ClientPermissionAddBody addBody, String realm, String clientId) {
		init();
		Token token = context.clientLogin();
		String url = authUrl + String.format("/admin/realms/%s/clients/%s/authz/resource-server/permission/resource"
				, realm, clientId);
		String bodyStr = HttpUtil.post(url, JsonUtil.toJson(addBody), buildHeader(token.getAccessToken()));
		return JsonUtil.toObject(bodyStr, ClientPermissionAddResult.class);
		
	}
	
	public String addUser(UserAddBody userAddBody, String realm) {
		init();
		Token token = context.clientLogin();
		String url = authUrl + String.format("/admin/realms/%s/users", realm);
		AtomicReference<String> userIdRefer = new AtomicReference<String>(null);
		HttpUtil.post(url, JsonUtil.toJson(userAddBody), buildHeader(token.getAccessToken()), headers -> {
			String location = getFirst(headers, "Location");
			if (location != null) {
	            int index = location.lastIndexOf("/");
	            userIdRefer.set(location.substring(index + 1));
	        }
		});
		return userIdRefer.get();
	}
	
	public void updateUser(UserUpdateBody userUpdateBody, String realm) {
		init();
		Token token = context.clientLogin();
		String url = authUrl + String.format("/admin/realms/%s/users/%s", realm, userUpdateBody.getId());
		HttpUtil.put(url, JsonUtil.toJson(userUpdateBody), buildHeader(token.getAccessToken()));
	}
	
	public void deleteUser(String realm, String userId) {
		init();
		Token token = context.clientLogin();
		String url = authUrl + String.format("/admin/realms/%s/users/%s", realm, userId);
		HttpUtil.delete(url, buildHeader(token.getAccessToken()));
	}
	
	public void addClientRoleForUser(List<UserClientRoleAddBody> roleAddList, String realm, String clientId, String userId) {
		init();
		Token token = context.clientLogin();
		String url = authUrl + String.format("/admin/realms/%s/users/%s/role-mappings/clients/%s"
				, realm, userId, clientId);
		HttpUtil.post(url, JsonUtil.toJson(roleAddList), buildHeader(token.getAccessToken()));
	}
	
	/**
	 * add manage-realm role for service account of the client.
	 * @param realm
	 * @param client The client which the service account belongs to
	 */
	public void addRealmAdminRoleForServiceAccount(String realm, String client) {
		String username = "service-account-" + client;
		KeycloakUser user = getUserByUsername(realm, username);
		ClientInfo realmClient = getClientInfo(realm, REALM_MGT_CLI);
		KeycloakRole role = getClientRoleByName(realm, realmClient.getId(), REALM_ADMIN_ROLE);
		UserClientRoleAddBody userClientRoleAddBody = new UserClientRoleAddBody()
			.setId(role.getId())
			.setName(role.getName());
		addClientRoleForUser(Arrays.asList(userClientRoleAddBody), realm, realmClient.getId(), user.getId());
	}
	
	public void deleteClientRoleForUser(UserClientRoleAddBody addBody, String realm, String clientId, String userId) {
		init();
		Token token = context.clientLogin();
		String url = authUrl + String.format("/admin/realms/%s/users/%s/role-mappings/clients/%s"
				, realm, userId, clientId);
		HttpUtil.delete(url, JsonUtil.toJson(addBody), buildHeader(token.getAccessToken()));
	}
	
	public KeycloakUser getUserByUsername(String realm, String username) {
		init();
		Token token = context.clientLogin();
		String url = authUrl + String.format("/admin/realms/%s/users?username=%s&exact=true", realm, username);
		String bodyStr = HttpUtil.get(url, buildHeader(token.getAccessToken()));
		List<KeycloakUser> users = JsonUtil.toObject(bodyStr, new TypeReference<List<KeycloakUser>>() {});
		if (users != null && users.size() > 0) {
			return users.get(0);
		}
		return null;
	}
	
	public void resetPassword(String realm, String userId, String password) {
		init();
		Token token = context.clientLogin();
		String url = authUrl + String.format("/admin/realms/%s/users/%s/reset-password", realm, userId);
		Credential credential = new Credential().setTemporary(false).setType("password").setValue(password);
		HttpUtil.put(url, JsonUtil.toJson(credential), buildHeader(token.getAccessToken()));
	}
	
	public ClientInfo getClientInfo(String realm, String client) {
		init();
		Token token = context.clientLogin();
		String url = authUrl + String.format("/admin/realms/%s/clients?clientId=%s", realm, client);
		String body = HttpUtil.get(url, buildHeader(token.getAccessToken()));
		List<ClientInfo> clientList = JsonUtil.toObject(body, new TypeReference<List<ClientInfo>>() {});
		if (clientList == null || clientList.size() == 0) {
			throw new KeyCloakSDKException("client " + client + " not exists");
		}
		return clientList.get(0);
	}
	
	private Map<String, String> buildHeader(String accessToken) {
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Content-Type", "application/json");
		if (StringUtils.isNotBlank(accessToken)) {
			headers.put("Authorization", String.format("%s %s", "Bearer", accessToken));
		}
		return headers;
	}
	
	private String getFirst(Map<String,List<String>> headers, String header) {
		List<String> values = headers.get(header);
		if (values != null && values.size() > 0) {
			return values.get(0);
		}
		return null;
	}
}
