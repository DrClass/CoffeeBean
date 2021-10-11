package com.octoconsulting.coffeebean.bots.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.octoconsulting.coffeebean.utils.Language;

public class MessageUtils {
	private static Pattern codeBlock = Pattern.compile("(?<=```).+\\n[\\s\\S]*?(?=```)");
	private static Pattern gitHubBlob = Pattern.compile("https:\\/\\/github.com\\/\\S+?\\/\\S+?\\/blob\\/\\S+");
	private static Pattern gitHubRepo = Pattern.compile("https:\\/\\/github.com\\/\\S+?\\/\\S+?\\s");
	
	public static boolean hasCodeBlock(String message) {
		Matcher m = codeBlock.matcher(message);
		return m.find();
	}
	
	public static boolean hasGitHubBlobLink(String message) {
		Matcher m = gitHubBlob.matcher(message);
		return m.find();
	}
	
	public static boolean hasGitHubRepoLink(String message) {
		Matcher m = gitHubRepo.matcher(message + " ");
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
	
	public static String extractGitHubBlobLink(String message) {
		Matcher m = gitHubBlob.matcher(message);
		m.find();
		return m.group(0).trim();
	}
	
	public static String extractGitHubRepoLink(String message) {
		Matcher m = gitHubRepo.matcher(message + " ");
		m.find();
		return m.group(0).trim();
	}
	
	public static String extractGitHubBlobFileName(String message) {
		String blob = extractGitHubBlobLink(message);
		return blob.substring(blob.lastIndexOf('/') + 1, blob.lastIndexOf('.'));
	}
	
	public static Language extractGitHubBlobFileLanguage(String message) {
		String blob = extractGitHubBlobLink(message);
		return Language.getLanguageFromExtension(blob.substring(blob.lastIndexOf('.') + 1));	
	}
	
	public static String fixSpaces(String message) {
		char invalid = '\u00a0';
		return message.replaceAll(String.valueOf(invalid), " ");
	}
}
