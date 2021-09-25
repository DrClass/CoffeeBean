package com.octoconsulting.coffeebean;

import java.util.List;
import java.util.stream.Collectors;

import com.octoconsulting.coffeebean.bots.BotType;

public class CodeReturn {
	private BotType botType;
	private String output;
	private List<String> idChain;
	
	@SuppressWarnings("unused")
	private CodeReturn() {
		// Stop empty initialization
	}

	public CodeReturn(BotType botType, String output, List<String> idChain) {
		this.botType = botType;
		this.output = output;
		this.idChain = idChain;
	}
	
	public BotType getBotType() {
		return botType;
	}

	public String getOutput() {
		return output;
	}

	public List<String> getIdChain() {
		return idChain;
	}
	
	public String toString() {
		return String.format("BotType: %s%nID Chain: %s%nCode: %n```%n%s%n```", getBotType(),
				getIdChain().stream().collect(Collectors.joining(" -> ")), getOutput());
	}
}
