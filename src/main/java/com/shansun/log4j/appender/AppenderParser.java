package com.shansun.log4j.appender;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Appender;
import org.apache.log4j.xml.DOMConfigurator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**  
 * Filename:    Log4jDOMConfiguator.java  
 * Description:   
 * Copyright:   Copyright (c)2010  
 * Company:     taobao  
 * @author:     lanbo 
 * @version:    1.0  
 * Create at:   2011-4-22 上午09:20:54  
 *  
 * Modification History:  
 * Date         Author      Version     Description  
 * ------------------------------------------------------------------  
 * 2011-4-22      lanbo        1.0        Version  
 */
public class AppenderParser extends DOMConfigurator {

	/**	放置Appender的Mapping */
	private Map<String, Appender> appenderBag = new HashMap<String, Appender>();
	private Document doc = null;
	private DocumentBuilderFactory dbf;
	private DocumentBuilder db = null;

	public AppenderParser() throws ParserConfigurationException {
		dbf = DocumentBuilderFactory.newInstance();
		db = dbf.newDocumentBuilder();
		db.setEntityResolver(new IgnoreDTDEntityResolver());
	}

	/**
	 * 解析Log4j的配置文件，得到Appenders
	 * @param configFile 配置文件路径，给出相对路径即可
	 * @throws Exception
	 */
	public void parse(String... configFiles) throws Exception {
		for (String fileName : configFiles) {
			doc = getDocument(fileName);
			NodeList appenderList = doc.getElementsByTagName("appender");
			for (int t = 0; t < appenderList.getLength(); t++) {
				Node node = appenderList.item(t);
				NamedNodeMap map = node.getAttributes();
				Node attrNode = map.getNamedItem("name");
				if (getAppenderBag().get(attrNode.getNodeValue()) == null) {
					Appender appender = parseAppender((Element) node);
					appenderBag.put(attrNode.getNodeValue(), appender);
				}
			}
		}
	}

	/**
	 * 根据提供的配置文件路径，解析出Document对象
	 * @param configFile 配置文件路径
	 * @return Document对象
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	private Document getDocument(String configFile) throws ParserConfigurationException, SAXException, IOException {
		return db.parse(new File(configFile));
	}

	public void setAppenderBag(Map<String, Appender> appenderBag) {
		this.appenderBag = appenderBag;
	}

	public Map<String, Appender> getAppenderBag() {
		return appenderBag;
	}

	class IgnoreDTDEntityResolver implements EntityResolver {

		@Override
		public InputSource resolveEntity(String arg0, String arg1) throws SAXException, IOException {
			return new InputSource(new ByteArrayInputStream("<?xml version='1.0' encoding='UTF-8'?>".getBytes()));
		}
	}
}
