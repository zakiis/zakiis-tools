package com.zakiis.kettle.test;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.pentaho.di.core.exception.KettleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.zakiis.common.SnowFlakeUtil;
import com.zakiis.kettle.constants.KettleConstants;
import com.zakiis.kettle.service.KettleService;

@SpringBootTest
public class KettleServiceTest {

	@Autowired
	private KettleService kettleService;
	Logger log = LoggerFactory.getLogger(KettleServiceTest.class);
	
	@Test
	public void testArchive() throws KettleException {
		String directory = "trans";
		String transformName = "archive";
		Map<String, String> params = new HashMap<String, String>();
		String seqNo = String.valueOf(SnowFlakeUtil.generate());
		params.put(KettleConstants.SEQ_NO, seqNo);
		params.put("host", "192.168.137.105");
		params.put("port", "3306");
		params.put("name", "zakiis");
		params.put("username", "root");
		params.put("password", "123456");
		params.put("sql", "select * from user");
		params.put("outputFile", "D:\\data\\kettle\\user-20220411");
		log.info("kettle transform init, seqNo:{}", seqNo);
		kettleService.runTransform(directory, transformName, params);
	}
	
	@Test
	public void testPurge() throws KettleException {
		String directory = "trans";
		String transformName = "purge";
		Map<String, String> params = new HashMap<String, String>();
		String seqNo = String.valueOf(SnowFlakeUtil.generate());
		params.put(KettleConstants.SEQ_NO, seqNo);
		params.put("host", "192.168.137.105");
		params.put("port", "3306");
		params.put("name", "zakiis");
		params.put("username", "root");
		params.put("password", "123456");
		params.put("sql", "delete from user where id < 10");
		log.info("kettle transform init, seqNo:{}", seqNo);
		kettleService.runTransform(directory, transformName, params);
	}
	
	@Test
	public void testRestore() throws KettleException, InterruptedException {
		String directory = "trans";
		String transformName = "restore";
		Map<String, String> params = new HashMap<String, String>();
		String seqNo = String.valueOf(SnowFlakeUtil.generate());
		params.put(KettleConstants.SEQ_NO, seqNo);
		params.put("host", "192.168.137.105");
		params.put("port", "3306");
		params.put("name", "zakiis");
		params.put("username", "root");
		params.put("password", "123456");
		log.info("kettle transform init, seqNo:{}", seqNo);
		
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				params.put("inputFile", "D:\\data\\kettle\\address-20220411.csv");
				params.put("table", "address");
				try {
					String seqNo = String.valueOf(SnowFlakeUtil.generate());
					params.put(KettleConstants.SEQ_NO, seqNo);
					log.info("kettle transform init, seqNo:{}", seqNo);
					kettleService.runTransform(directory, transformName, params);
				} catch (KettleException e) {
					e.printStackTrace();
				}
			}
		});
		thread.start();
		thread.join();
		
		params.put("inputFile", "D:\\data\\kettle\\user-20220411.csv");
		params.put("table", "user");
		kettleService.runTransform(directory, transformName, params);
	}
}
