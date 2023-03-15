package com.zakiis.kettle.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Map;

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.repository.RepositoryDirectoryInterface;
import org.pentaho.di.repository.kdr.KettleDatabaseRepository;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.steps.csvinput.CsvInputMeta;
import org.pentaho.di.trans.steps.textfileinput.TextFileInputField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zakiis.kettle.service.listener.KettleTransStatusListener;

@Service
@SuppressWarnings("deprecation")
public class KettleService {
	
	@Autowired
	KettleDatabaseRepository repository;
	@Autowired
	KettleTransStatusListener kettleTransStatusListener;
	
	Logger log = LoggerFactory.getLogger(KettleService.class);
	
	public void runTransform(String directory, String transformName, Map<String, String> params) throws KettleException {
		RepositoryDirectoryInterface directoryTree = repository.findDirectory(directory);
		TransMeta transMeta = repository.loadTransformation(transformName, directoryTree, null, true, null);
		Trans trans=new Trans(transMeta);
		trans.addTransListener(kettleTransStatusListener);
		if (params != null) {
			for (Map.Entry<String, String> param: params.entrySet()) {
				trans.setVariable(param.getKey(), param.getValue());
			}
		}
		if (params.containsKey("inputFile")) {
			setCsvInputFileds(transMeta, params.get("inputFile"));
		}
        trans.execute(null);
//        trans.waitUntilFinished();//等待直到数据结束 
	}
	
	private void setCsvInputFileds(TransMeta transMeta, String csvFileName) throws KettleException {
		try (BufferedReader br = new BufferedReader(new FileReader(csvFileName))) {
			String line = null;
			if ((line = br.readLine()) != null) {
				String[] columnNames = line.split(",");
				TextFileInputField[] inputFields = new TextFileInputField[columnNames.length];
				for (int i = 0; i < columnNames.length; i++) {
					String columnName = columnNames[i];
					if (columnName.startsWith("\"")) {
						columnName = columnName.substring(1, columnName.length() - 1);
					}
					TextFileInputField inputField = new TextFileInputField(columnName, i, 0);
					inputFields[i] = inputField;
				}
				((CsvInputMeta)transMeta.getStep(0).getStepMetaInterface()).setInputFields(inputFields);
			}
		} catch (Exception e) {
			throw new KettleException("Analysis CSV header fields got an exception", e);
		}
		//(CsvInputMeta)transMeta.getStep(0).getStepMetaInterface()).setInputFields();
	}
}
