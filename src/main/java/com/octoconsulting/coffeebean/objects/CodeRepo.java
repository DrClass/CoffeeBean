package com.octoconsulting.coffeebean.objects;

import java.util.List;
import java.util.stream.Collectors;

import com.octoconsulting.coffeebean.bots.BotType;
import com.octoconsulting.coffeebean.utils.Language;

public class CodeRepo {
	private BotType botType;
	private Language language;
	private String main;
	private String url;
	private List<String> idChain;
	
	@SuppressWarnings("unused")
	private CodeRepo() {
		// Stop empty initialization
	}

	public CodeRepo(BotType botType, Language language, String main, String url, List<String> idChain) {
		this.botType = botType;
		this.language = language;
		this.main = main;
		this.url = url;
		this.idChain = idChain;
	}
	
	public void setLanguage(Language language) {
		this.language = language;
	}

	public void setMain(String main) {
		this.main = main;
	}

	public BotType getBotType() {
		return botType;
	}

	public Language getLanguage() {
		return language;
	}

	public String getMain() {
		return main;
	}
	
	public String getURL() {
		return url;
	}

	public List<String> getIdChain() {
		return idChain;
	}
	
	public String toString() {
		return String.format("BotType: %s%nLanguage: %s%nID Chain: %s%nMain Location: %s%nRepo URL: %s", getBotType(),
				getLanguage(), getIdChain().stream().collect(Collectors.joining(" -> ")), getMain(), getURL());
	}
}
