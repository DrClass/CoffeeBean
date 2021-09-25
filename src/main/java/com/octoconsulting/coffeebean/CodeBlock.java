package com.octoconsulting.coffeebean;

import java.util.List;
import java.util.stream.Collectors;

import com.octoconsulting.coffeebean.bots.BotType;

public class CodeBlock {
	private BotType botType;
	private String language;
	private String code;
	private List<String> idChain;
	
	@SuppressWarnings("unused")
	private CodeBlock() {
		// Stop empty initialization
	}

	public CodeBlock(BotType botType, String language, String code, List<String> idChain) {
		this.botType = botType;
		this.language = language;
		this.code = code;
		this.idChain = idChain;
	}
	
	public BotType getBotType() {
		return botType;
	}

	public String getLanguage() {
		return language;
	}

	public String getCode() {
		return code;
	}

	public List<String> getIdChain() {
		return idChain;
	}
	
	public String toString() {
		return String.format("BotType: %s%nLanguage: %s%nID Chain: %s%nCode: %n```%n%s%n```", getBotType(),
				getLanguage(), getIdChain().stream().collect(Collectors.joining(" -> ")), getCode());
	}
}
