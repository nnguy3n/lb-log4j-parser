package com.shansun.log4j.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Level;

/**
 * Filename:    LogEntry.java  
 * Description:   
 * Copyright:   Copyright (c)2010  
 * Company:     taobao  
 * @author:     lanbo 
 * @version:    1.0  
 * Create at:   2011-5-11 ����04:43:46  
 *  
 * Modification History:  
 * Date         Author      Version     Description  
 * ------------------------------------------------------------------  
 * 2011-5-11      lanbo        1.0        Version
 */
public class LogEntry {
	/** ��־��ӡʱ�� */
	private Date timestamp;
	/** ��־�ȼ�*/
	private Level level;
	/** ��־����*/
	private String loggerName;
	/** ��־�߳�*/
	private String thread;
	/** ��־����*/
	private String message;
	/** ������־�ļ�*/
	private String locFileName;
	/** ������*/
	private String locClass;
	/** ���ڷ���*/
	private String locMethod;
	/** ���ڴ�����*/
	private Long locLine;
	/** nested diagnostic contexts*/
	private Object ndc;

	private Map<String, Object> map = new HashMap<String, Object>();

	/**
	 * ������δָ������־��������Ϊ��ֵ�Ա��浽map��
	 * @param <T>
	 * @param key ��־�������ƣ�����־��ӡʱ�䡢��־�ȼ���
	 * @param value ��־����ֵ
	 */
	public final <T> void put(String key, T value) {
		map.put(key, value);
	}

	/**
	 * ��ȡ������δָ������־����
	 * @param <T>
	 * @param key ��־�������ƣ�����־��ӡʱ�䡢��־�ȼ���
	 * @return ��־����ֵ
	 */
	@SuppressWarnings("unchecked")
	public final <T> T get(String key) {
		return (T) map.get(key);
	}

	/**
	 * �ж�map���Ƿ񱣴�ĳ��־����
	 * @param <T>
	 * @param key ��־�������ƣ�����־��ӡʱ�䡢��־�ȼ���
	 * @return
	 */
	public final <T> boolean contains(String key) {
		return map.containsKey(key);
	}

	public String toString() {
		return map.toString();
	}

	
    public Date getTimestamp() {
    	return timestamp;
    }

	
    public void setTimestamp(Date timestamp) {
    	this.timestamp = timestamp;
    }

	
    public Level getLevel() {
    	return level;
    }

	
    public void setLevel(Level level) {
    	this.level = level;
    }

	
    public String getLoggerName() {
    	return loggerName;
    }

	
    public void setLoggerName(String loggerName) {
    	this.loggerName = loggerName;
    }

	
    public String getThread() {
    	return thread;
    }

	
    public void setThread(String thread) {
    	this.thread = thread;
    }

	
    public String getMessage() {
    	return message;
    }

	
    public void setMessage(String message) {
    	this.message = message;
    }

	
    public String getLocFileName() {
    	return locFileName;
    }

	
    public void setLocFileName(String locFileName) {
    	this.locFileName = locFileName;
    }

	
    public String getLocClass() {
    	return locClass;
    }

	
    public void setLocClass(String locClass) {
    	this.locClass = locClass;
    }

	
    public String getLocMethod() {
    	return locMethod;
    }

	
    public void setLocMethod(String locMethod) {
    	this.locMethod = locMethod;
    }

	
    public Long getLocLine() {
    	return locLine;
    }

	
    public void setLocLine(Long locLine) {
    	this.locLine = locLine;
    }

	
    public Object getNdc() {
    	return ndc;
    }

	
    public void setNdc(Object ndc) {
    	this.ndc = ndc;
    }

	
    public Map<String, Object> getMap() {
    	return map;
    }

	
    public void setMap(Map<String, Object> map) {
    	this.map = map;
    }
}
