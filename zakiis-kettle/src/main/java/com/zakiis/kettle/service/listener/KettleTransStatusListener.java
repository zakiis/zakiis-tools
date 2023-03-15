package com.zakiis.kettle.service.listener;

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.zakiis.kettle.constants.KettleConstants;

@Service
public class KettleTransStatusListener implements TransListener {
	
	Logger log = LoggerFactory.getLogger(KettleTransStatusListener.class);

	@Override
	public void transStarted(Trans trans) throws KettleException {
		String seqNo = trans.getVariable(KettleConstants.SEQ_NO);
		log.info("kettle transform start, seqNo:{}", seqNo);
	}

	@Override
	public void transActive(Trans trans) {
	}

	@Override
	public void transFinished(Trans trans) throws KettleException {
		String seqNo = trans.getVariable(KettleConstants.SEQ_NO);
		String executeResult = "success";
		if (trans.getErrors() > 0) {
			executeResult = "fail";
		}
		log.info("kettle transform end, seqNo:{}, execute {}", seqNo, executeResult);
	}

}
