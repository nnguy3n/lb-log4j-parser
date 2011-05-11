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

import com.shansun.log4j.constants.Constants;
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
 * Create at:   2011-4-22 ����11:31:02  
 *  
 * Modification History:  
 * Date         Author      Version     Description  
 * ------------------------------------------------------------------  
 * 2011-4-22      lanbo        1.0        Version  
 */
public class LogFileParser {

	private static Logger logger = Logger.getLogger(LogFileParser.class);
	private ConversionPatternParser conversionPatternParser;

	public LogFileParser() {
		conversionPatternParser = new ConversionPatternParser();
	}

	/**
	 * ������־�ļ���������־���Ƿ����ĳ�ؼ��֣�����¼������N��
	 * @param fileName ��־�ļ�·��
	 * @param conversionPattern Log4j�����õ�ConversionPattern
	 * @param content �ؼ�������
	 * @param upperLogNum ������־�е����϶�����
	 * @param lowerLogNum ������־�е����¶�����
	 * @return �ռ��ķ�����������־�м���
	 * @throws Exception
	 */
	public List<LogEntry> parse(String fileName, String conversionPattern, String content, Integer upperLogNum, Integer lowerLogNum) throws Exception {
		List<ConversionPatternEl> extractRules = conversionPatternParser.extractConversionPattern(conversionPattern);
		FileInputStream fis = new FileInputStream(new File(fileName));
		this.lineNo = 1;
		LineIterator iter = IOUtils.lineIterator(fis, "GBK");
		try {
			List<LogEntry> logLines = iterateLogLines(conversionPattern, extractRules, iter, content, upperLogNum, lowerLogNum);
			return logLines;
		} finally {
			LineIterator.closeQuietly(iter);
		}
	}

	/**
	 * ������־�ļ� 
	 * @param fileName ��־�ļ�·��
	 * @param conversionPattern Log4j�����õ�ConversionPattern
	 * @return �ռ��ķ�����������־�м���
	 * @throws Exception
	 */
	public List<LogEntry> parse(String fileName, String conversionPattern) throws Exception {
		return this.parse(fileName, conversionPattern, null, null, null);
	}

	/**
	 * ������־�ļ����õ���־��
	 * @param conversionPattern Log4j�����õ�ConversionPattern
	 * @param extractEls �����õ���ConversionPatternԪ�ؼ�
	 * @param iter �ļ���ö����
	 * @param content �ؼ�������
	 * @param upperLogNum ������־�е����϶�����
	 * @param lowerLogNum ������־�е����¶�����
	 * @return �ռ��ķ�����������־�м���
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private List<LogEntry> iterateLogLines(String conversionPattern, List<ConversionPatternEl> extractEls, LineIterator iter, String content, Integer upperLogNum, Integer lowerLogNum) throws Exception {
		boolean flag = true;
		List<LogEntry> result = new ArrayList<LogEntry>();
		BoundedFifoBuffer upperLogEntries = null;
		if (upperLogNum != null && upperLogNum > 0)
		    upperLogEntries = new BoundedFifoBuffer(upperLogNum);
		BoundedFifoBuffer lowerLogEntries = null;
		if (lowerLogNum != null && lowerLogNum > 0)
		    lowerLogEntries = new BoundedFifoBuffer(lowerLogNum);
		LogEntry unfinishedEntry = null;
		LogEntry currentEntry = fetchARecord(iter, conversionPattern, extractEls, unfinishedEntry);
		while (currentEntry != null) {
			if (content == null) {
				currentEntry.put(Constants.FIELD_IS_HIT, true);
				result.add(currentEntry);
				currentEntry = fetchARecord(iter, conversionPattern, extractEls, unfinishedEntry);
			} else {
				String msg = currentEntry.getMessage();
				boolean isHit = msg.contains(content);
				if (flag) {
					if (isHit) {
						//����
						flag = false;
						if (upperLogEntries != null) {
							result.addAll(upperLogEntries);
							upperLogEntries.clear();
						}
						currentEntry.put(Constants.FIELD_IS_HIT, true);
						result.add(currentEntry);
					} else {
						if (upperLogEntries != null) {
							if (upperLogEntries.isFull()) {
								upperLogEntries.remove();
							}
							upperLogEntries.add(currentEntry);
						}
					}
					currentEntry = fetchARecord(iter, conversionPattern, extractEls, unfinishedEntry);
					continue;
				} else {
					if (!isHit) {
						if (lowerLogNum != 0) {
							//δ����
							if (lowerLogEntries != null) {
								lowerLogEntries.add(currentEntry);
								if (lowerLogEntries.isFull()) {
									//ת��Lower�еļ�¼��LogList��
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
						currentEntry.put(Constants.FIELD_IS_HIT, true);
						result.add(currentEntry);
					}
					currentEntry = fetchARecord(iter, conversionPattern, extractEls, unfinishedEntry);
					continue;
				}
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
					currentEntry.put(Constants.FIELD_CURRENT_LINE, lineNo++);
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
				String msg = unfinishedEntry.getMessage();
				msg += '\n' + line;
				unfinishedEntry.setMessage(msg);
			}
		}
		if (unfinishedEntry != null) {
			currentEntry = unfinishedEntry;
		}
		if (currentEntry != null)
		    currentEntry.put(Constants.FIELD_CURRENT_LINE, lineNo++);
		return currentEntry;
	}

	private void extractField(LogEntry entry, ConversionPatternEl rule, String val) throws Exception {
		if (rule.getPlaceholderName().equals("d")) {
			DateFormat df = rule.getProperty(Constants.PROP_DATEFORMAT, DateFormat.class);
			entry.setTimestamp(df.parse(val.trim()));
		} else if (rule.getPlaceholderName().equals("p")) {
			Level lvl = Level.toLevel(val.trim());
			entry.setLevel(lvl);
		} else if (rule.getPlaceholderName().equals("c")) {
			entry.setLoggerName(val.trim());
		} else if (rule.getPlaceholderName().equals("t")) {
			entry.setThread(val.trim());
		} else if (rule.getPlaceholderName().equals("m")) {
			entry.setMessage(val.trim());
		} else if (rule.getPlaceholderName().equals("F")) {
			entry.setLocFileName(val.trim());
		} else if (rule.getPlaceholderName().equals("C")) {
			entry.setLocClass(val.trim());
		} else if (rule.getPlaceholderName().equals("M")) {
			entry.setLocMethod(val.trim());
		} else if (rule.getPlaceholderName().equals("L")) {
			entry.setLocLine(Long.parseLong(val.trim()));
		} else if (rule.getPlaceholderName().equals("x")) {
			entry.setNdc(val.trim());
		} else {
			throw new Exception("�쳣��Ϣ��δ����");
		}
	}
}
