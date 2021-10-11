package com.octoconsulting.coffeebean.objects;

import java.util.List;
import java.util.stream.Collectors;

import com.octoconsulting.coffeebean.bots.BotType;
import com.octoconsulting.coffeebean.utils.Language;

public class CodeFile {
	private BotType botType;
	private Language language;
	private String code;
	private List<String> idChain;
	private String fileName;
	
	@SuppressWarnings("unused")
	private CodeFile() {
		// Stop empty initialization
	}

	public CodeFile(BotType botType, Language language, String code, List<String> idChain, String fileName) {
		this.botType = botType;
		this.language = language;
		this.code = code;
		this.idChain = idChain;
		this.fileName = fileName;
	}
	
	public BotType getBotType() {
		return botType;
	}

	public Language getLanguage() {
		return language;
	}

	public String getCode() {
		return code;
	}

	public List<String> getIdChain() {
		return idChain;
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public String toString() {
		return String.format("BotType: %s%nFile Name: %s%nLanguage: %s%nID Chain: %s%nCode: %n```%n%s%n```",
				getBotType(),
				getFileName() == null ? "null" : getFileName(),
				getLanguage(),
				getIdChain().stream().collect(Collectors.joining(" -> ")),
				getCode());
	}
}
