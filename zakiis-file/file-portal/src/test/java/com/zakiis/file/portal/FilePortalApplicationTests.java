package com.zakiis.file.portal;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;

import com.zakiis.file.domain.constants.AccessMode;
import com.zakiis.file.domain.constants.CommonConstants;
import com.zakiis.file.model.Bucket;
import com.zakiis.file.model.Channel;
import com.zakiis.file.model.inner.Access;
import com.zakiis.file.portal.domain.constants.FunctionCode;
import com.zakiis.security.HMACUtil;
import com.zakiis.security.HMACUtil.HMACType;
import com.zakiis.security.codec.HexUtil;

@SpringBootTest
class FilePortalApplicationTests {
	
	@Autowired
	ReactiveMongoTemplate mongoTemplate;

	@Test
	void contextLoads() throws InterruptedException {
		createChannel();
		createBucket();
	}
	
	private void createBucket() {
		Map<String, String> channelMap = mongoTemplate.query(Channel.class)
				.all()
				.collectMap(Channel::getName, Channel::getAk)
				.block();
		Bucket bucket = new Bucket();
		bucket.setName("cimb-jumio");
		bucket.setDescription("jumio files of cimb project");
		bucket.setAccessMode(AccessMode.PUBLIC);
		bucket.setCreateTime(new Date());
		Map<String, Access> accessMap = new HashMap<String, Access>();
		accessMap.put(channelMap.get("MOBILE"), Access.READ_WRITE);
		accessMap.put(channelMap.get("RCMS"), Access.READ_ONLY);
		bucket.setAccess(accessMap);
		mongoTemplate.save(bucket).block();
		
		bucket = new Bucket();
		bucket.setName("cimb-zoloz");
		bucket.setDescription("zoloz files of cimb project");
		bucket.setAccessMode(AccessMode.PRIVATE);
		bucket.setCreateTime(new Date());
		accessMap = new HashMap<String, Access>();
		accessMap.put(channelMap.get("MOBILE"), Access.READ_ONLY);
		accessMap.put(channelMap.get("RCMS"), Access.READ_WRITE);
		bucket.setAccess(accessMap);
		mongoTemplate.save(bucket).block();
	}

	void createChannel() {
//		mongoTemplate.dropCollection(Channel.class).block(); //也许是bug，删除再save会导致创建索引失败
		mongoTemplate.save(buildAdminChannel())
			.block();
		mongoTemplate.save(buildChannel(UUID.randomUUID().toString(), "HMAC-SHA256", "MOBILE"))
			.block();
		mongoTemplate.save(buildChannel(UUID.randomUUID().toString(), "HMAC-SHA256" , "SLP"))
			.block();
		mongoTemplate.save(buildChannel(UUID.randomUUID().toString(), "HMAC-SHA256", "RCMS"))
			.block();
	}
	
	Channel buildChannel(String ak, String algorithm, String name) {
		Channel channel = new Channel();
		channel.setAk(ak);
		channel.setSk(HexUtil.toHexString(HMACUtil.genSecretKey(HMACType.getByAlogorithm(algorithm))));
		channel.setAlgorithm(algorithm);
		channel.setName(name);
		channel.setStatus(CommonConstants.STATUS_ACTIVE);
		channel.setFunctions(new HashSet<String>(Arrays.asList(FunctionCode.FILE_READ_VALUE, FunctionCode.FILE_WRITE_VALUE)));
		return channel;
	}
	
	Channel buildAdminChannel() {
		Channel channel = new Channel();
		channel.setAk("00000000-0000-0000-0000-000000000000");
		channel.setSk(HexUtil.toHexString(HMACUtil.genSecretKey(HMACType.HMAC_SHA_256)));
		channel.setAlgorithm(HMACType.HMAC_SHA_256.getAlgorithm());
		channel.setName("ADMIN");
		channel.setStatus(CommonConstants.STATUS_ACTIVE);
		channel.setFunctions(new HashSet<String>(Arrays.asList(FunctionCode.ADMIN_VALUE)));
		return channel;
	}

}
