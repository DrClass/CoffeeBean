package com.octoconsulting.coffeebean.utils;

public enum Language {
	JAVA("openjdk:16", "java"),
	PYTHON("python:3-slim", "py");
	
	private String container;
	private String fileExtension;
	
	Language(String container, String fileExtension) {
		this.container = container;
		this.fileExtension = fileExtension;
	}
	
	public String container() {
		return container;
	}
	
	public String fileExtension() {
		return fileExtension;
	}
	
	public static Language getLanguageFromExtension(String extension) {
		for (Language lang : Language.values()) {
			if (lang.fileExtension().equalsIgnoreCase(extension)) {
				return lang;
			}
		}
		return null;
	}
}
