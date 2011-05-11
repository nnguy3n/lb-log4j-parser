package com.shansun.log4j.model;

import java.util.HashMap;
import java.util.Map;

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

	private Map<String, Object> map = new HashMap<String, Object>();

	/**
	 * 将日志的属性作为键值对保存到map中
	 * @param <T>
	 * @param key 日志属性名称，如日志打印时间、日志等级等
	 * @param value 日志属性值
	 */
	public final <T> void put(String key, T value) {
		map.put(key, value);
	}

	/**
	 * 获取日志属性
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
}
