package com.shansun.log4j.conversion;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Appender;
import org.apache.log4j.Layout;
import org.apache.log4j.PatternLayout;

import com.shansun.log4j.constants.Constants;
import com.shansun.log4j.model.ConversionPatternEl;
import com.shansun.log4j.utils.RegexUtils;

/**  
 * Filename:    Log4JConversionPatternTranslator.java  
 * Description:   
 * Copyright:   Copyright (c)2010  
 * Company:     taobao  
 * @author:     lanbo 
 * @version:    1.0  
 * Create at:   2011-4-21 下午06:26:00  
 *  
 * Modification History:  
 * Date         Author      Version     Description  
 * ------------------------------------------------------------------  
 * 2011-4-21      lanbo        1.0        Version  
 */
public class ConversionPatternParser {

	public ConversionPatternParser() {}

	public String getConversionPattern(Appender appender) {
		Layout layout = appender.getLayout();
		if (layout instanceof PatternLayout) {
			PatternLayout patternLayout = (PatternLayout) layout;
			return patternLayout.getConversionPattern();
		}
		return null;
	}

	/**
	 * 将ConversionPattern转化为正则表达式 
	 * @param conversionPattern log4j表达式
	 * @return
	 * @throws Exception
	 */
	public Pattern getRegexPattern(String conversionPattern) throws Exception {
		return Pattern.compile(toRegexPattern(prepare(conversionPattern)));
	}

	/**
	 * 将ConversionPattern转化为正则表达式 
	 * @param conversionPattern log4j表达式
	 * @return 正则表达式
	 * @throws Exception
	 */
	protected String toRegexPattern(String conversionPattern) throws Exception {
		int idx = 0;
		List<ConversionPatternEl> els = this.extractConversionPattern(conversionPattern);
		if (els != null) {
			ConversionPatternEl prevRule = null;
			for (ConversionPatternEl rule : els) {
				if ((rule.getBeginIndex() > idx) && (prevRule != null)) {
					prevRule.setFollowedByQuotedString(true);
				}
				idx = rule.getBeginIndex();
				idx += rule.getLength();
				prevRule = rule;
			}
			if ((conversionPattern.length() > idx) && (prevRule != null)) {
				prevRule.setFollowedByQuotedString(true);
			}
			StringBuilder sb = new StringBuilder();
			idx = 0;
			for (ConversionPatternEl el : els) {
				if (el.getBeginIndex() > idx) {
					sb.append(Pattern.quote(conversionPattern.substring(idx, el.getBeginIndex())));
				}
				idx = el.getBeginIndex();
				String regex = this.getRegexForPatternEl(el);
				sb.append(regex);
				idx += el.getLength();
			}
			if (conversionPattern.length() > idx) {
				sb.append(Pattern.quote(conversionPattern.substring(idx)));
			}
			return sb.toString();
		}
		throw new Exception("无法解析ConversionPattern!");
	}

	/**
	 * 将Log4j的ConversionPattern转化为内部使用的ConversionRule
	 * @param conversionPattern
	 * @return
	 * @throws Exception
	 */
	public List<ConversionPatternEl> extractConversionPattern(String conversionPattern) throws Exception {
		conversionPattern = prepare(conversionPattern);
		Matcher m = Constants.EXTRACTION_PATTERN.matcher(conversionPattern);
		List<ConversionPatternEl> ret = new ArrayList<ConversionPatternEl>();
		while (m.find()) {
			String minWidthModifier = m.group(2);
			String maxWidthModifier = m.group(4);
			String conversionName = m.group(5);
			String conversionModifier = m.group(7);
			int minWidth = -1;
			if ((minWidthModifier != null) && (minWidthModifier.length() > 0)) {
				minWidth = Integer.parseInt(minWidthModifier);
			}
			int maxWidth = -1;
			if ((maxWidthModifier != null) && (maxWidthModifier.length() > 0)) {
				maxWidth = Integer.parseInt(maxWidthModifier);
			}
			ConversionPatternEl rule = new ConversionPatternEl();
			rule.setBeginIndex(m.start());
			rule.setLength(m.end() - m.start());
			rule.setMaxWidth(maxWidth);
			rule.setMinWidth(minWidth);
			rule.setPlaceholderName(conversionName);
			rule.setModifier(conversionModifier);
			rewrite(rule);
			ret.add(rule);
		}
		return ret;
	}

	/**
	 * 将ConversionPattern元素转化为正则表达式 
	 * @param el ConversionPattern元素 
	 * @return 正则表达式 
	 * @throws Exception
	 */
	private String getRegexForPatternEl(ConversionPatternEl el) throws Exception {
		if (el.getPlaceholderName().equals("d")) {
			return "(" + RegexUtils.getRegexForSimpleDateFormat(el.getModifier()) + ")";
		} else if (el.getPlaceholderName().equals("p")) {
			String lnHint = RegexUtils.getLengthHint(el);
			if (lnHint.length() > 0) {
				return "([ A-Z]" + lnHint + ")";
			}
			return "([A-Z]{4,5})";
		} else if (el.getPlaceholderName().equals("c")) {
			return "(.*" + RegexUtils.getLengthHint(el) + RegexUtils.getLazySuffix(el) + ")";
		} else if (el.getPlaceholderName().equals("t")) {
			return "(.*" + RegexUtils.getLengthHint(el) + RegexUtils.getLazySuffix(el) + ")";
		} else if (el.getPlaceholderName().equals("m")) {
			return "(.*" + RegexUtils.getLengthHint(el) + RegexUtils.getLazySuffix(el) + ")";
		} else if (el.getPlaceholderName().equals("F")) {
			return "(.*" + RegexUtils.getLengthHint(el) + RegexUtils.getLazySuffix(el) + ")";
		} else if (el.getPlaceholderName().equals("C")) {
			return "(.*" + RegexUtils.getLengthHint(el) + RegexUtils.getLazySuffix(el) + ")";
		} else if (el.getPlaceholderName().equals("M")) {
			return "(.*" + RegexUtils.getLengthHint(el) + RegexUtils.getLazySuffix(el) + ")";
		} else if (el.getPlaceholderName().equals("L")) {
			return "([0-9]*" + RegexUtils.getLengthHint(el) + ")";
		} else if (el.getPlaceholderName().equals("x")) {
			return "(.*" + RegexUtils.getLengthHint(el) + RegexUtils.getLazySuffix(el) + ")";
		}
		throw new Exception("无法找到对应的表达式描述!");
	}

	/**
	 * 预处理ConversionPattern
	 * @param conversionPattern
	 * @return 预处理后的ConversionPattern
	 * @throws Exception
	 */
	private String prepare(String conversionPattern) throws Exception {
		if (!conversionPattern.endsWith("%n")) {
			return conversionPattern;
		}
		conversionPattern = conversionPattern.substring(0, conversionPattern.length() - 2);
		if (conversionPattern.contains("%n")) {
			throw new Exception("ConversionPattern不合法!");
		}
		return conversionPattern;
	}

	/**
	 * 覆写ConversionPattern元素
	 * @param el ConversionPattern元素
	 * @throws Exception
	 */
	private void rewrite(ConversionPatternEl el) throws Exception {
		if (el.getPlaceholderName().equals("d")) {
			applyDefaults(el);
			if (el.getModifier().equals("ABSOLUTE")) {
				el.setModifier("HH:mm:ss,SSS");
			} else if (el.getModifier().equals("DATE")) {
				el.setModifier("dd MMM yyyy HH:mm:ss,SSS");
			} else if (el.getModifier().equals("ISO8601")) {
				el.setModifier("yyyy-MM-dd HH:mm:ss,SSS");
			}
			try {
				// 放置日期格式到ConversionPattern元素中
				el.putProperty(Constants.PROP_DATEFORMAT, new SimpleDateFormat(el.getModifier()));
			} catch (IllegalArgumentException e) {
				throw new Exception(e);
			}
		}
	}

	private void applyDefaults(ConversionPatternEl el) throws Exception {
		if (el.getModifier() == null) {
			// ISO8601 is the default
			el.setModifier("ISO8601");
		}
	}
}