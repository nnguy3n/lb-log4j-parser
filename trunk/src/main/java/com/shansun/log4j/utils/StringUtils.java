package com.shansun.log4j.utils;

/**  
 * Filename:    StringUtils.java  
 * Description:   
 * Copyright:   Copyright (c)2010  
 * Company:     taobao  
 * @author:     lanbo 
 * @version:    1.0  
 * Create at:   2011-5-11 обнГ04:33:55  
 *  
 * Modification History:  
 * Date         Author      Version     Description  
 * ------------------------------------------------------------------  
 * 2011-5-11      lanbo        1.0        Version  
 */
public class StringUtils {

	public static boolean isBlank(String line) {
		if (line == null || line.isEmpty()) return true;
		return false;
	}
}
