package com.zakiis.file.portal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import com.zakiis.file.domain.dto.ResponseDTO;
import com.zakiis.file.portal.boot.autoconfigure.properties.FilePortalProperties;
import com.zakiis.file.portal.domain.constants.FilePortalConstants;
import com.zakiis.file.portal.domain.constants.FunctionCode;
import com.zakiis.file.portal.domain.dto.user.UserLoginDTO;
import com.zakiis.file.portal.domain.dto.user.UserRegisterDTO;
import com.zakiis.file.portal.domain.dto.user.UserUpdateDTO;
import com.zakiis.file.portal.service.UserService;
import com.zakiis.file.portal.service.tool.ValidationTool;
import com.zakiis.security.annotation.Permission;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1/file-portal/user")
public class UserController {

	@Autowired
	FilePortalProperties filePortalProperties;
	@Autowired
	UserService userService;
	
	@PostMapping("/register/{username}")
	public Mono<ResponseDTO<Object>> register(@PathVariable String username, @RequestBody UserRegisterDTO registerDTO) {
		registerDTO.setUsername(username);
		ValidationTool.notEmpty(registerDTO.getPassword(), "Password");
		return userService.registerUser(registerDTO)
				.then(Mono.just(ResponseDTO.ok()));
	}
	
	@PostMapping("/login/{username}")
	public Mono<ResponseDTO<Object>> login(@PathVariable String username, @RequestBody UserLoginDTO loginDTO, ServerHttpResponse response) {
		loginDTO.setUsername(username);
		ValidationTool.notEmpty(loginDTO.getPassword(), "Password");
		return userService.loginUser(loginDTO)
				.map(jwtToken -> {
//					response.addCookie(ResponseCookie.from(CommonConstants.JWT_HEADER_NAME, String.format(CommonConstants.JWT_HEADER_VALUE_FORMAT, jwtToken))
//							.path("/")
//							.maxAge(Duration.ofMillis(filePortalProperties.getSessionTimeoutMills()))
//							.build());
					return ResponseDTO.ok(jwtToken);
				});
	}
	
	@PostMapping("/update/{username}")
	@Permission(functions = {FunctionCode.UPDATE_USER_VALUE})
	public Mono<ResponseDTO<Long>> updateUser(@PathVariable String username, @RequestBody UserUpdateDTO updateDTO, ServerWebExchange exchange) {
		updateDTO.setUsername(username);
		return userService.updateUser(updateDTO, exchange.getAttribute(FilePortalConstants.CONTEXT_USERNAME))
				.map(ResponseDTO::ok);
	}
}
