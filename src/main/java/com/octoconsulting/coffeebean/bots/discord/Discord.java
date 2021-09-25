package com.octoconsulting.coffeebean.bots.discord;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.event.message.MessageCreateEvent;

import com.octoconsulting.coffeebean.CodeBlock;
import com.octoconsulting.coffeebean.CodeQueue;
import com.octoconsulting.coffeebean.CodeReturn;
import com.octoconsulting.coffeebean.bots.BotType;
import com.octoconsulting.coffeebean.bots.common.MessageUtils;

public class Discord {

	public static DiscordApi discordBot = null;

	public static void init() {
		// TODO: Remove the bot token and move it to a config which is git ignored.
		// Discord gets mad when you hardcode bot tokens.
		String token = "NjE2NjgyOTE0NjU0MDYwNTY0.XWgIuA.INC4Y88CYjYv-VYjyspArU7Qb4s";
		discordBot = new DiscordApiBuilder().setToken(token).login().join();
		discordBot.addMessageCreateListener(event -> {
			if (!event.getMessageAuthor().isBotUser()) {
				if (event.getMessage().getMentionedUsers().stream().map(u -> u.getId()).collect(Collectors.toList())
						.contains(discordBot.getClientId())
						|| event.getMessageContent().toLowerCase().startsWith("!coffeebean")
						|| event.getMessageContent().toLowerCase().startsWith("/coffeebean")) {
					CodeBlock block = new CodeBlock(BotType.DISCORD, MessageUtils.extractCodeLanguage(event.getMessageContent()),
							MessageUtils.extractCodeBlock(event.getMessageContent()), buildIdTrace(event));
					CodeQueue.addToQueue(block);
					// REMOVE THIS LINE OF CODE! IT IS FOR DEBUG ONLY
					event.getMessage().reply(block.toString());
				}
			}
		});
	}
	
	public static void reply(CodeReturn output) throws InterruptedException, ExecutionException {
		discordBot.getServerById(output.getIdChain().get(0)).get().getChannelById(output.getIdChain().get(1)).get().asTextChannel().get().getMessageById(output.getIdChain().get(2)).get().reply(output.getOutput());
	}
	
	private static List<String> buildIdTrace(MessageCreateEvent event) {
		List<String> trace = new ArrayList<String>();
		trace.add(event.getServer().get().getIdAsString());
		trace.add(event.getChannel().getIdAsString());
		trace.add(event.getMessage().getIdAsString());
		return trace;
	}
}
