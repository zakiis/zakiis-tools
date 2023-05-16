package com.zakiis.file.portal.service;

import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.mongodb.client.result.UpdateResult;
import com.zakiis.file.domain.constants.CommonConstants;
import com.zakiis.file.exception.ServiceException;
import com.zakiis.file.model.User;
import com.zakiis.file.portal.boot.autoconfigure.properties.FilePortalProperties;
import com.zakiis.file.portal.domain.constants.FilePortalConstants;
import com.zakiis.file.portal.domain.constants.FunctionCode;
import com.zakiis.file.portal.domain.dto.user.UserLoginDTO;
import com.zakiis.file.portal.domain.dto.user.UserRegisterDTO;
import com.zakiis.file.portal.domain.dto.user.UserUpdateDTO;
import com.zakiis.file.portal.exception.ErrorEnum;
import com.zakiis.file.portal.util.PasswordUtil;
import com.zakiis.security.AESUtil;
import com.zakiis.security.codec.HexUtil;
import com.zakiis.security.jwt.JWTUtil;
import com.zakiis.security.jwt.algorithm.Algorithm;

import reactor.core.publisher.Mono;

@Service
public class UserService {
	
	@Autowired
	FilePortalProperties filePortalProperties;
	@Autowired
	ReactiveMongoTemplate mongoTemplate;

	public Mono<Void> registerUser(UserRegisterDTO registerDTO) {
		User user = new User();
		user.setUsername(registerDTO.getUsername());
		user.setIv(HexUtil.toHexString(AESUtil.genIV()));
		user.setPassword(PasswordUtil.genEncryptedPassword(registerDTO.getPassword(), filePortalProperties.getAesSecret(), user.getIv()));
		user.setCreatedBy(CommonConstants.DEFAULT_USER);
		user.setCreateTime(new Date());
		user.setUpdatedBy(CommonConstants.DEFAULT_USER);
		user.setUpdateTime(new Date());
		return mongoTemplate.findOne(Query.query(Criteria.where("username").is(user.getUsername())), User.class)
				.hasElement()
				.flatMap(exists -> {
					if (exists) {
						throw new ServiceException(ErrorEnum.USER_EXISTS);
					}
					return mongoTemplate.insert(user)
						.then();
				});
	}

	/**
	 * login
	 * @param loginDTO
	 * @return JWT token
	 */
	public Mono<String> loginUser(UserLoginDTO loginDTO) {
		return mongoTemplate.findOne(Query.query(Criteria.where("username").is(loginDTO.getUsername())), User.class)
				.switchIfEmpty(Mono.error(new ServiceException(ErrorEnum.USER_NOT_EXISTS)))
				.map(user -> {
					String loginPassword = PasswordUtil.genEncryptedPassword(loginDTO.getPassword(), filePortalProperties.getAesSecret(), user.getIv());
					if (!user.getPassword().equals(loginPassword)) {
						throw new ServiceException(ErrorEnum.INVALID_USERNAME_OR_PASSWORD);
					}
					String token = JWTUtil.create()
							.withIssuer(FilePortalConstants.APPLICATION_NAME)
							.withIssuedAt(new Date())
							.withSubject(user.getUsername())
							.withExpiresAt(DateUtils.addMilliseconds(new Date(), (int)(long)filePortalProperties.getSessionTimeoutMills()))
							.withClaim(FilePortalConstants.FUNCTIONS_CLAIM, StringUtils.join(user.getFunctions(), ","))
							.sign(Algorithm.HMAC256(filePortalProperties.getJwtSecret()));
					return token;
				});
	}

	public Mono<Long> updateUser(UserUpdateDTO updateDTO, String contextUsername) {
		Query query = Query.query(Criteria.where("username").is(updateDTO.getUsername()));
		return mongoTemplate.findOne(query, User.class)
				.switchIfEmpty(Mono.error(new ServiceException(ErrorEnum.USER_NOT_EXISTS)))
				.flatMap(user -> {
					Update update = new Update();
					if (StringUtils.isNotEmpty(updateDTO.getPassword())) {
						String newPassword = PasswordUtil.genEncryptedPassword(updateDTO.getPassword(), filePortalProperties.getAesSecret(), user.getIv());;
						update.set("password", newPassword);
					}
					if (updateDTO.getFunctions() != null) {
						update.set("functions", Optional.ofNullable(updateDTO.getFunctions())
								.map(functionEnumSet -> functionEnumSet.stream().map(FunctionCode::name).collect(Collectors.toSet()))
								.orElse(new HashSet<String>()));
					}
					update.set("updatedBy", contextUsername);
					update.set("updateTime", new Date());
					return mongoTemplate.updateFirst(query, update, User.class)
							.map(UpdateResult::getModifiedCount);
				});
	}

}
