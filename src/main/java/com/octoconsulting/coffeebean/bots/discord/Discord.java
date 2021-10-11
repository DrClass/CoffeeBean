package com.octoconsulting.coffeebean.bots.discord;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.event.message.MessageCreateEvent;

import com.octoconsulting.coffeebean.CodeQueue;
import com.octoconsulting.coffeebean.bots.BotType;
import com.octoconsulting.coffeebean.bots.common.MessageUtils;
import com.octoconsulting.coffeebean.objects.CodeBlock;
import com.octoconsulting.coffeebean.objects.CodeFile;
import com.octoconsulting.coffeebean.objects.CodeRepo;
import com.octoconsulting.coffeebean.objects.CodeReturn;
import com.octoconsulting.coffeebean.utils.GithubUtils;
import com.octoconsulting.coffeebean.utils.Language;
import com.octoconsulting.coffeebean.utils.Selenium;

public class Discord {

	public static DiscordApi discordBot = null;

	public static void init() {
		File file = new File("src/main/resources/token.txt");
		String token = "";
		try {
			token = FileUtils.readFileToString(file, Charset.defaultCharset()).trim();
		} catch (IOException e) {
			e.printStackTrace();
		}
		discordBot = new DiscordApiBuilder().setToken(token).login().join();
		discordBot.addMessageCreateListener(event -> {
			if (!event.getMessageAuthor().isBotUser()) {
				if (event.getMessage().getMentionedUsers().stream().map(u -> u.getId()).collect(Collectors.toList())
						.contains(discordBot.getClientId())
						|| event.getMessageContent().toLowerCase().startsWith("!coffeebean")
						|| event.getMessageContent().toLowerCase().startsWith("/coffeebean")) {
					if (MessageUtils.hasCodeBlock(event.getMessageContent())) {
						CodeBlock block = new CodeBlock(BotType.DISCORD,
								Language.valueOf(
										MessageUtils.extractCodeLanguage(event.getMessageContent()).toUpperCase()),
								MessageUtils.extractCodeBlock(event.getMessageContent()), buildIdTrace(event));
						CodeQueue.addToQueue(block);
					} else if (MessageUtils.hasGitHubBlobLink(event.getMessageContent())) {
						CodeFile codeFile = new CodeFile(BotType.DISCORD,
								MessageUtils.extractGitHubBlobFileLanguage(event.getMessageContent()),
								Selenium.getFileContents(MessageUtils.extractGitHubBlobLink(event.getMessageContent())),
								buildIdTrace(event),
								MessageUtils.extractGitHubBlobFileName(event.getMessageContent()));
						CodeQueue.addToQueue(codeFile);
					} else if (MessageUtils.hasGitHubRepoLink(event.getMessageContent())) {
						CodeRepo codeRepo = new CodeRepo(BotType.DISCORD, null, null,
								GithubUtils.guessDownloadURL(
										MessageUtils.extractGitHubRepoLink(event.getMessageContent())),
								buildIdTrace(event));
						CodeQueue.addToQueue(codeRepo);
						event.getMessage().reply(codeRepo.toString());
					} else {
						CodeQueue.codeReturns.add(new CodeReturn(BotType.DISCORD, "Error: No code or valid links detected.", buildIdTrace(event)));
					}
				}
			}
		});
	}

	public static void reply(CodeReturn output) throws InterruptedException, ExecutionException {
		discordBot.getServerById(output.getIdChain().get(0)).get().getChannelById(output.getIdChain().get(1)).get()
				.asTextChannel().get().getMessageById(output.getIdChain().get(2)).get().reply(output.getOutput());
	}

	private static List<String> buildIdTrace(MessageCreateEvent event) {
		List<String> trace = new ArrayList<String>();
		trace.add(event.getServer().get().getIdAsString());
		trace.add(event.getChannel().getIdAsString());
		trace.add(event.getMessage().getIdAsString());
		return trace;
	}
}
