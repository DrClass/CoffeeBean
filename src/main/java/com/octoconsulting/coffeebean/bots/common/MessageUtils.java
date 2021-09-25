package com.octoconsulting.coffeebean.bots.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageUtils {
	private static Pattern codeBlock = Pattern.compile("(?<=```).+\\n[\\s\\S]*?\\n(?=```)");
	
	public static boolean hasCodeBlock(String message) {
		Matcher m = codeBlock.matcher(message);
		return m.find();
	}
	
	public static String extractCodeBlock(String message) {
		Matcher m = codeBlock.matcher(message);
		m.find();
		String match = m.group(0).trim();
		return match.substring(match.indexOf('\n')).trim();
	}
	
	public static String extractCodeLanguage(String message) {
		Matcher m = codeBlock.matcher(message);
		m.find();
		String match = m.group(0).trim();
		return match.substring(0, match.indexOf('\n')).trim();
	}
}
