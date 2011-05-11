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
 * Create at:   2011-5-11 下午04:43:46  
 *  
 * Modification History:  
 * Date         Author      Version     Description  
 * ------------------------------------------------------------------  
 * 2011-5-11      lanbo        1.0        Version
 */
public class LogEntry {
	/** 日志打印时间 */
	private Date timestamp;
	/** 日志等级*/
	private Level level;
	/** 日志名称*/
	private String loggerName;
	/** 日志线程*/
	private String thread;
	/** 日志内容*/
	private String message;
	/** 所在日志文件*/
	private String locFileName;
	/** 所在类*/
	private String locClass;
	/** 所在方法*/
	private String locMethod;
	/** 所在代码行*/
	private Long locLine;
	/** nested diagnostic contexts*/
	private Object ndc;

	private Map<String, Object> map = new HashMap<String, Object>();

	/**
	 * 将其他未指定的日志的属性作为键值对保存到map中
	 * @param <T>
	 * @param key 日志属性名称，如日志打印时间、日志等级等
	 * @param value 日志属性值
	 */
	public final <T> void put(String key, T value) {
		map.put(key, value);
	}

	/**
	 * 获取将其他未指定的日志属性
	 * @param <T>
	 * @param key 日志属性名称，如日志打印时间、日志等级等
	 * @return 日志属性值
	 */
	@SuppressWarnings("unchecked")
	public final <T> T get(String key) {
		return (T) map.get(key);
	}

	/**
	 * 判断map中是否保存某日志属性
	 * @param <T>
	 * @param key 日志属性名称，如日志打印时间、日志等级等
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
