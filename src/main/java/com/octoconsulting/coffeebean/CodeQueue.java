package com.octoconsulting.coffeebean;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;

import com.octoconsulting.coffeebean.bots.discord.Discord;
import com.octoconsulting.coffeebean.bots.slack.Slack;
import com.octoconsulting.coffeebean.bots.web.Web;
import com.octoconsulting.coffeebean.objects.CodeBlock;
import com.octoconsulting.coffeebean.objects.CodeFile;
import com.octoconsulting.coffeebean.objects.CodeRepo;
import com.octoconsulting.coffeebean.objects.CodeReturn;

public class CodeQueue implements Runnable {
	public static LinkedBlockingQueue<CodeBlock> codeQueue = new LinkedBlockingQueue<CodeBlock>();
	public static LinkedBlockingQueue<CodeFile> fileQueue = new LinkedBlockingQueue<CodeFile>();
	public static LinkedBlockingQueue<CodeRepo> repoQueue = new LinkedBlockingQueue<CodeRepo>();
	public static LinkedBlockingQueue<CodeReturn> codeReturns = new LinkedBlockingQueue<CodeReturn>();
	public static boolean shouldRun = false;
	
	public static void addToQueue(CodeBlock block) {
		codeQueue.add(block);
	}
	
	public static void addToQueue(CodeFile file) {
		fileQueue.add(file);
	}
	
	public static void addToQueue(CodeRepo repo) {
		repoQueue.add(repo);
	}
	
	@Override
	public void run() {
		shouldRun = true;
		System.out.println("Queue Listener is running!");
		while (shouldRun) {
			if (codeReturns.peek() != null) {
				try {
					CodeReturn output = codeReturns.take();
					try {
						switch (output.getBotType()) {
							case DISCORD:
								Discord.reply(output);
								break;
							case SLACK:
								Slack.reply(output);
								break;
							case TEAMS:
								break;
							case WEB:
								Web.reply(output);
							default:
								break;
						}
					} catch (ExecutionException e) {
						e.printStackTrace();
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		System.out.println("Queue Listener stopped!");
	}
}
