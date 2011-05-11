package com.shansun.log4j.parser;


import java.io.File;
import java.io.FileInputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import org.apache.commons.collections.buffer.BoundedFifoBuffer;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.shansun.log4j.conversion.ConversionPatternParser;
import com.shansun.log4j.model.ConversionPatternEl;
import com.shansun.log4j.model.LogEntry;
import com.shansun.log4j.utils.StringUtils;

/**  
 * Filename:    LogFileParser.java  
 * Description:   
 * Copyright:   Copyright (c)2010  
 * Company:     taobao  
 * @author:     lanbo 
 * @version:    1.0  
 * Create at:   2011-4-22 上午11:31:02  
 *  
 * Modification History:  
 * Date         Author      Version     Description  
 * ------------------------------------------------------------------  
 * 2011-4-22      lanbo        1.0        Version  
 */
public class LogFileParser {

	public static final String FIELD_LOGGER = "logger";
	public static final String FIELD_TIMESTAMP = "timestamp";
	public static final String FIELD_LEVEL = "level";
	public static final String FIELD_THREAD = "thread";
	public static final String FIELD_MESSAGE = "message";
	public static final String FIELD_CURRENT_LINE = "currLine";
	public static final String FIELD_IS_HIT = "isHit";
	public static final String FIELD_NDC = "ndc";
	public static final String FIELD_THROWABLE = "throwable";
	public static final String FIELD_LOC_FILENAME = "locFilename";
	public static final String FIELD_LOC_CLASS = "locClass";
	public static final String FIELD_LOC_METHOD = "locMethod";
	public static final String FIELD_LOC_LINE = "locLine";
	private static Logger logger = Logger.getLogger(LogFileParser.class);
	private ConversionPatternParser conversionPatternParser;

	public LogFileParser() {
		conversionPatternParser = new ConversionPatternParser();
	}

	public List<LogEntry> parse(String fileName, String conversionPattern, String content, Integer upperLogNum, Integer lowerLogNum) throws Exception {
		List<ConversionPatternEl> extractRules = conversionPatternParser.extractConversionPattern(conversionPattern);
		FileInputStream fis = new FileInputStream(new File(fileName));
		this.lineNo = 1;
		LineIterator iter = IOUtils.lineIterator(fis, "GBK");
		try {
			List<LogEntry> logLines = iterateLogLines(conversionPattern, extractRules, iter, upperLogNum, lowerLogNum, content);
			return logLines;
		} finally {
			LineIterator.closeQuietly(iter);
		}
	}

	@SuppressWarnings("unchecked")
	private List<LogEntry> iterateLogLines(String conversionPattern, List<ConversionPatternEl> extractRules, LineIterator iter, Integer upperLogNum, Integer lowerLogNum, String content) throws Exception {
		boolean flag = true;
		List<LogEntry> result = new ArrayList<LogEntry>();
		BoundedFifoBuffer upperLogEntries = null;
		if (upperLogNum != null && upperLogNum > 0)
		    upperLogEntries = new BoundedFifoBuffer(upperLogNum);
		BoundedFifoBuffer lowerLogEntries = null;
		if (lowerLogNum != null && lowerLogNum > 0)
		    lowerLogEntries = new BoundedFifoBuffer(lowerLogNum);
		LogEntry unfinishedEntry = null;
		LogEntry currentEntry = fetchARecord(iter, conversionPattern, extractRules, unfinishedEntry);
		while (currentEntry != null) {
			String msg = currentEntry.get(FIELD_MESSAGE);
			boolean isHit = msg.contains(content);
			if (flag) {
				if (isHit) {
					//命中
					flag = false;
					if (upperLogEntries != null) {
						result.addAll(upperLogEntries);
						upperLogEntries.clear();
					}
					currentEntry.put(FIELD_IS_HIT, true);
					result.add(currentEntry);
				} else {
					if (upperLogEntries != null) {
						if (upperLogEntries.isFull()) {
							upperLogEntries.remove();
						}
						upperLogEntries.add(currentEntry);
					}
				}
				currentEntry = fetchARecord(iter, conversionPattern, extractRules, unfinishedEntry);
				continue;
			} else {
				if (!isHit) {
					if (lowerLogNum != 0) {
						//未命中
						if (lowerLogEntries != null) {
							lowerLogEntries.add(currentEntry);
							if (lowerLogEntries.isFull()) {
								//转移Lower中的记录到LogList中
								flag = true;
								result.addAll(lowerLogEntries);
								lowerLogEntries.clear();
							}
						}
					} else {
						flag = true;
					}
				} else {
					if (lowerLogEntries != null) {
						result.addAll(lowerLogEntries);
						lowerLogEntries.clear();
					}
					currentEntry.put(FIELD_IS_HIT, true);
					result.add(currentEntry);
				}
				currentEntry = fetchARecord(iter, conversionPattern, extractRules, unfinishedEntry);
				continue;
			}
		}
		return result;
	}

	private long lineNo = 1;

	private LogEntry fetchARecord(LineIterator iter, String conversionPattern, List<ConversionPatternEl> extractRules, LogEntry unfinishedEntry) throws Exception {
		LogEntry currentEntry = null;
		boolean found = true;
		if (unfinishedEntry == null) {
			found = false;
		}
		if (!iter.hasNext()) {
			return null;
		}
		while (iter.hasNext()) {
			// Error handling 
			String line = iter.nextLine();
			while (StringUtils.isBlank(line) && iter.hasNext()) {
				line = iter.nextLine();
			}
			Matcher m = conversionPatternParser.getRegexPattern(conversionPattern).matcher(line);
			if (m.find()) {
				//It's next entry, unfinished
				if (found) {
					currentEntry = unfinishedEntry;
					unfinishedEntry = new LogEntry();
					for (int i = 0; i < m.groupCount(); i++) {
						try {
							this.extractField(unfinishedEntry, extractRules.get(i), m.group(i + 1));
						} catch (Exception e) {
							// Mark for interruption 
							logger.warn(e);
						}
					}
					currentEntry.put(FIELD_CURRENT_LINE, lineNo++);
					return currentEntry;
				} else {
					unfinishedEntry = new LogEntry();
					found = true;
					for (int i = 0; i < m.groupCount(); i++) {
						try {
							this.extractField(unfinishedEntry, extractRules.get(i), m.group(i + 1));
						} catch (Exception e) {
							// Mark for interruption 
							logger.warn(e);
						}
					}
				}
			} else if (unfinishedEntry != null) {
				String msg = unfinishedEntry.get(FIELD_MESSAGE);
				msg += '\n' + line;
				unfinishedEntry.put(FIELD_MESSAGE, msg);
			}
		}
		if (unfinishedEntry != null) {
			currentEntry = unfinishedEntry;
		}
		if (currentEntry != null)
		    currentEntry.put(FIELD_CURRENT_LINE, lineNo++);
		return currentEntry;
	}

	private void extractField(LogEntry entry, ConversionPatternEl rule, String val) throws Exception {
		if (rule.getPlaceholderName().equals("d")) {
			DateFormat df = rule.getProperty(ConversionPatternParser.PROP_DATEFORMAT, DateFormat.class);
			entry.put(FIELD_TIMESTAMP, df.parse(val.trim()));
		} else if (rule.getPlaceholderName().equals("p")) {
			Level lvl = Level.toLevel(val.trim());
			entry.put(FIELD_LEVEL, lvl);
		} else if (rule.getPlaceholderName().equals("c")) {
			entry.put(FIELD_LOGGER, val.trim());
		} else if (rule.getPlaceholderName().equals("t")) {
			entry.put(FIELD_THREAD, val.trim());
		} else if (rule.getPlaceholderName().equals("m")) {
			entry.put(FIELD_MESSAGE, val.trim());
		} else if (rule.getPlaceholderName().equals("F")) {
			entry.put(FIELD_LOC_FILENAME, val.trim());
		} else if (rule.getPlaceholderName().equals("C")) {
			entry.put(FIELD_LOC_CLASS, val.trim());
		} else if (rule.getPlaceholderName().equals("M")) {
			entry.put(FIELD_LOC_METHOD, val.trim());
		} else if (rule.getPlaceholderName().equals("L")) {
			entry.put(FIELD_LOC_LINE, val.trim());
		} else if (rule.getPlaceholderName().equals("x")) {
			entry.put(FIELD_NDC, val.trim());
		} else {
			throw new Exception("异常消息暂未设置");
		}
	}

	public static void main(String[] args) {
		//		try {
		//			String conversionPattern = ConversionPatternParser.CONVERSION_JUST_4TEST;
		//			String fileName = "C:\\mywlb.log";
		//			LogFileParser parser = new LogFileParser();
		//			BoundedFifoBuffer logEntries2 = parser.parse(fileName, conversionPattern, "Exception", 5);
		//			System.out.println(SerializeUtil.serialize(logEntries2));
		//		} catch (Exception e) {
		//			e.printStackTrace();
		//		}
	}
}
