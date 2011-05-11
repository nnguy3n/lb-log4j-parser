package com.shansun.log4j.search;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.log4j.Appender;
import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.PatternLayout;

import com.shansun.log4j.appender.AppenderParser;
import com.shansun.log4j.model.LogEntry;
import com.shansun.log4j.parser.LogFileParser;

/**  
 * Filename:    LoggerSearch.java  
 * Description:   
 * Copyright:   Copyright (c)2010  
 * Company:     taobao  
 * @author:     lanbo 
 * @version:    1.0  
 * Create at:   2011-4-22 ÏÂÎç06:52:39  
 *  
 * Modification History:  
 * Date         Author      Version     Description  
 * ------------------------------------------------------------------  
 * 2011-4-22      lanbo        1.0        Version  
 */
public class LoggerSearch {

	private AppenderParser appenderParser;
	private LogFileParser logParser;
	private Map<String, Appender> appenders;
	private String configFilePath = "log4j.xml";
	List<LogEntry> logEntries;
	Map<String, List<LogEntry>> allLogEntries;

	@PostConstruct
	public void init() throws Exception {
		appenderParser = new AppenderParser();
		logParser = new LogFileParser();
		appenderParser.parse(getConfigFilePath());
		appenders = appenderParser.getAppenderBag();
		allLogEntries = new HashMap<String, List<LogEntry>>();
	}

	public Map<String, List<LogEntry>> searchAll(String content, Integer upperLogNum, Integer lowerLogNum) throws Exception {
		for (Appender appender : appenders.values()) {
			if (appender instanceof FileAppender) {
				FileAppender fileAppender = (FileAppender) appender;
				if (appender instanceof DailyRollingFileAppender) {
					Layout layout = fileAppender.getLayout();
					if (layout instanceof PatternLayout) {
						PatternLayout patternLayout = (PatternLayout) layout;
						String conversionPattern = patternLayout.getConversionPattern();
						String fileName = fileAppender.getFile();
						logEntries = logParser.parse(fileName, conversionPattern, content, upperLogNum, lowerLogNum);
						allLogEntries.put(new File(fileName).getName(), logEntries);
					}
				}
			}
		}
		return allLogEntries;
	}

	public void setConfigFilePath(String configFilePath) {
		this.configFilePath = configFilePath;
	}

	public String getConfigFilePath() {
		return LoggerSearch.class.getClassLoader().getResource(configFilePath).getFile();
	}
}
