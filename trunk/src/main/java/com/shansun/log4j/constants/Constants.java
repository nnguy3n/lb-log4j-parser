package com.shansun.log4j.constants;

import java.util.regex.Pattern;

/**  
 * Filename:    LogFieldConstants.java  
 * Description:   
 * Copyright:   Copyright (c)2010  
 * Company:     taobao  
 * @author:     lanbo 
 * @version:    1.0  
 * Create at:   2011-5-11 ÏÂÎç06:54:53  
 *  
 * Modification History:  
 * Date         Author      Version     Description  
 * ------------------------------------------------------------------  
 * 2011-5-11      lanbo        1.0        Version  
 */
public interface Constants {

	public static final String FIELD_CURRENT_LINE = "currLine";
	public static final String FIELD_IS_HIT = "isHit";
	public static final String FIELD_THROWABLE = "throwable";
	
	public static final Pattern EXTRACTION_PATTERN = Pattern.compile("%(-?(\\d+))?(\\.(\\d+))?([a-zA-Z])(\\{([^\\}]+)\\})?");
	public static final String CONVERSION_JUST_4TEST = "%d [] %-5p %c{2} - %m%n";
	public static final String PROP_DATEFORMAT = "dateFormat";

}
