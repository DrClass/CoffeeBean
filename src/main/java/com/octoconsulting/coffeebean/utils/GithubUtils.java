package com.octoconsulting.coffeebean.utils;

public class GithubUtils {
	
	/**
	 * This can be wrong, but will be able to get the right download link most of the time.
	 */
	public static String guessDownloadURL(String url) {
		if (url.contains("tree")) {
			url = url.replace("tree", "archive/refs/heads");
			url = url + ".zip";
		} else {
			url = url + "/archive/refs/heads/main.zip";
		}
		return url;
	}
}
