package com.zakiis.keycloak.sdk;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import com.zakiis.keycloak.sdk.domain.dto.Credential;
import com.zakiis.keycloak.sdk.domain.dto.IDToken;
import com.zakiis.keycloak.sdk.domain.dto.Token;
import com.zakiis.keycloak.sdk.domain.dto.client.ClientAddBody;
import com.zakiis.keycloak.sdk.domain.dto.client.ClientInfo;
import com.zakiis.keycloak.sdk.domain.dto.realm.RealmAddReq;
import com.zakiis.keycloak.sdk.domain.dto.user.KeycloakRole;
import com.zakiis.keycloak.sdk.domain.dto.user.UserAddBody;
import com.zakiis.keycloak.sdk.domain.dto.user.UserClientRoleAddBody;
import com.zakiis.keycloak.sdk.util.JsonUtil;

public class KeycloakContextTest {
	
	static String AUTH_URL = "https://192.168.137.101";
	static String realm = "test-realm";
	static String adminClient = "admin-cli";
	
	@Test
	public void testManager() {
		// 1.创建realm
		String masterAdminSecret = "ZfYE23xswAcuPY23sd5HfYulqr1Xlfje";
		KeycloakManager manager = new KeycloakManager(AUTH_URL, KeycloakManager.MASTER_REALM, false, masterAdminSecret);
		RealmAddReq realmAddReq = new RealmAddReq()
			.setRealm(realm)
			.setDisplayName("测试域");
		manager.addRealm(realmAddReq);
		// 2.启用admin client auth
		ClientInfo clientInfo = manager.getClientInfo(realm, adminClient);
		Credential credential = manager.enableClientAuth(realm, clientInfo.getId());
		manager.addRealmAdminRoleForServiceAccount(realm, KeycloakManager.ADM_CLI);
		// for security reason, realm's secret would be used while it's created.
		System.out.println(String.format("The secret of admin-cli is %s, it has been granted the realm admin role."
				, credential.getValue()));
		manager = new KeycloakManager(AUTH_URL, realm, false, credential.getValue());
		// 3.创建用户
		UserAddBody userAddBody = new UserAddBody()
			.setUsername("liuzhenghua")
			.setEmail("liuzhenghua@xxx.com")
			.setFirstName("Liu")
			.setLastName("Zhenghua");
		String userId = manager.addUser(userAddBody, realm);
		manager.resetPassword(realm, userId, "123456");
		// 4.创建应用A
		String clientA = "app-a";
		ClientAddBody clientAddBody = new ClientAddBody()
			.setClientId(clientA)
			.setName("演示应用A");
		String appAClientId = manager.addClient(clientAddBody, realm);
		credential = manager.enableClientAuth(realm, appAClientId);
		System.out.println("The secret of " + clientA + " is " + credential.getValue());
		// 5.启用应用A权限控制，用户需要有该应用的client-grant角色才能访问该应用
		manager.configureWhitelistAuthorization(realm, appAClientId);
		// 6.给用户添加应用A角色
		String roleName = "client-grant";
		KeycloakRole role = manager.getClientRoleByName(realm, appAClientId, roleName);
		UserClientRoleAddBody userClientRoleAddBody = new UserClientRoleAddBody()
			.setId(role.getId())
			.setName(role.getName());
		manager.addClientRoleForUser(Arrays.asList(userClientRoleAddBody), realm, appAClientId, userId);
		// 7.创建应用B，但是不给用户授权(测试权限控制)
		String clientB = "app-b";
		clientAddBody = new ClientAddBody()
			.setClientId(clientB)
			.setName("演示应用B");
		String appBClientId = manager.addClient(clientAddBody, realm);
		manager.enableClientAuth(realm, appBClientId);
		manager.configureWhitelistAuthorization(realm, appBClientId);
	}
	
	@Test
	public void testLogin() throws InterruptedException {
		String clientASecret = "WbUmh1YIwolaPPgMPuWxtFOXSl3duttZ";
		String adminCliSercret = "4dtzZfWdqFllC8ouiWJ53c88twwXbNMy";
		String clientA = "app-a";
		String clientB = "app-b";
		// 登录
		KeycloakContext context = new KeycloakContext(AUTH_URL, realm, clientA, false, clientASecret);
		Token token = context.login("liuzhenghua", "123456");
		IDToken idToken = context.verify(token.getAccessToken());
		System.out.println(JsonUtil.toJson(idToken));
		// 给用户授予clientB的client-grant角色
		String roleName = "client-grant";
		KeycloakManager manager = new KeycloakManager(AUTH_URL, realm, false, adminCliSercret);
		ClientInfo clientInfo = manager.getClientInfo(realm, clientB);
		String userId = manager.getUserByUsername(realm, "liuzhenghua").getId();
		KeycloakRole role = manager.getClientRoleByName(realm, clientInfo.getId(), roleName);
		UserClientRoleAddBody userClientRoleAddBody = new UserClientRoleAddBody()
			.setId(role.getId())
			.setName(role.getName());
		manager.addClientRoleForUser(Arrays.asList(userClientRoleAddBody), realm, clientInfo.getId(), userId);
		// 登录后给用户加了一个client权限，需要用refreshToken去换新的accessToken
		Token token2 = context.refreshToken(token.getRefreshToken());
		IDToken idToken2 = context.verify(token2.getAccessToken());
		System.out.println(JsonUtil.toJson(idToken2));
		// 用新的accessToken去换取新应用的token
		Token token3 = context.umaLogin(token2.getAccessToken(), clientB);
		IDToken idToken3 = context.verify(token3.getAccessToken());
		System.out.println(JsonUtil.toJson(idToken3));
	}
	
	@Test
	public void testGenLoginURL() {
		String clientA = "app-a";
		String clientASecret = "WbUmh1YIwolaPPgMPuWxtFOXSl3duttZ";
		KeycloakContext context = new KeycloakContext(AUTH_URL, realm, clientA, false, clientASecret);
		String url = context.genLoginURL("http://rcms.cimbbank.com.ph:8080/health");
		System.out.println(url);
	}
}
