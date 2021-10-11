package com.octoconsulting.coffeebean.bots.slack;

import java.io.IOException;
import java.util.List;

import com.octoconsulting.coffeebean.CodeQueue;
import com.octoconsulting.coffeebean.bots.BotType;
import com.octoconsulting.coffeebean.bots.common.MessageUtils;
import com.octoconsulting.coffeebean.objects.CodeBlock;
import com.octoconsulting.coffeebean.objects.CodeReturn;
import com.octoconsulting.coffeebean.utils.Language;
import com.slack.api.bolt.App;
import com.slack.api.bolt.jetty.SlackAppServer;
import com.slack.api.methods.SlackApiException;
import com.slack.api.model.event.AppMentionEvent;

public class Slack implements Runnable {
	public static App app;
	public static SlackAppServer server;
	
	public static void init() throws Exception {
		app = new App();
		app.event(AppMentionEvent.class, (payload, ctx) -> {
			CodeBlock block = new CodeBlock(BotType.SLACK,
					Language.valueOf(MessageUtils.extractCodeLanguage(payload.getEvent().getText()).toUpperCase()),
					MessageUtils.fixSpaces(MessageUtils.extractCodeBlock(payload.getEvent().getText())),
					List.of(payload.getEvent().getChannel()));
			CodeQueue.addToQueue(block);
			return ctx.ack();
		});
		
		server = new SlackAppServer(app);
		server.start();
	}
	
	public static void reply(CodeReturn output) {
		try {
			com.slack.api.Slack.getInstance().methods().chatPostMessage(r -> r.token(System.getenv("SLACK_BOT_TOKEN")).channel(output.getIdChain().get(0)).text(output.getOutput()));
		} catch (IOException | SlackApiException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
			init();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
