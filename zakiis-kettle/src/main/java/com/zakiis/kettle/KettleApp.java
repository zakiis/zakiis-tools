package com.zakiis.kettle;

import org.apache.commons.lang3.RandomUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.zakiis.common.SnowFlakeUtil;

@SpringBootApplication
public class KettleApp {
	
	public static void main(String[] args) {
		int dataCenterId = RandomUtils.nextInt(0, 0x1f);
		int workerId = RandomUtils.nextInt(0, 0x1f);
		// prd env need use other mechanism liking redis incre to make sure it would not be repeat on different process.
		SnowFlakeUtil.init(dataCenterId, workerId);
		SpringApplication.run(KettleApp.class, args);
	}

}
