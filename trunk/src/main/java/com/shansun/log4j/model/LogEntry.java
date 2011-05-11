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
 * Create at:   2011-5-11 ����04:43:46  
 *  
 * Modification History:  
 * Date         Author      Version     Description  
 * ------------------------------------------------------------------  
 * 2011-5-11      lanbo        1.0        Version
 */
public class LogEntry {

	private Map<String, Object> map = new HashMap<String, Object>();

	/**
	 * ����־��������Ϊ��ֵ�Ա��浽map��
	 * @param <T>
	 * @param key ��־�������ƣ�����־��ӡʱ�䡢��־�ȼ���
	 * @param value ��־����ֵ
	 */
	public final <T> void put(String key, T value) {
		map.put(key, value);
	}

	/**
	 * ��ȡ��־����
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
}
