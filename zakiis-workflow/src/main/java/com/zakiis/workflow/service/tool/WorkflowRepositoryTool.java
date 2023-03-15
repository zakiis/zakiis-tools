package com.zakiis.workflow.service.tool;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Optional;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.zakiis.workflow.exception.WorkflowRuntimeException;


public class WorkflowRepositoryTool {

	
	public static String getProcessDefinitionName(String processDefinitionContent) {
		try {
			SAXReader reader = new SAXReader();
			// ignore public key checking
			reader.setEntityResolver(new EntityResolver() {
				@Override
				public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
					InputSource is = new InputSource(new ByteArrayInputStream(new byte[0]));
					is.setPublicId(publicId);
					is.setSystemId(systemId);
					return is;
				}
			});
			Document document = reader.read(new ByteArrayInputStream(processDefinitionContent.getBytes()));
			Element rootElement = document.getRootElement();
			Element process = rootElement.element("process");
			String name = getAttributeValue(process.attribute("id"));
			if (name == null) {
				name = getAttributeValue(process.attribute("name"));
			}
			if (name == null) {
				throw new WorkflowRuntimeException("can't resolve process definition name");
			}
			return name;
		} catch (Exception e) {
			throw new WorkflowRuntimeException("resolve process definition got an exception", e);
		}
	}
	
	private static String getAttributeValue(Attribute attr) {
		return Optional.ofNullable(attr).map(Attribute::getValue).orElse(null);
	}
	
}
