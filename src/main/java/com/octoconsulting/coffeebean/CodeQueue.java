package com.octoconsulting.coffeebean;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;

import com.octoconsulting.coffeebean.bots.discord.Discord;

public class CodeQueue implements Runnable {
	public static LinkedBlockingQueue<CodeBlock> codeQueue = new LinkedBlockingQueue<CodeBlock>();
	public static LinkedBlockingQueue<CodeReturn> codeReturns = new LinkedBlockingQueue<CodeReturn>();
	public static boolean shouldRun = false;
	
	public static void addToQueue(CodeBlock block) {
		codeQueue.add(block);
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
								break;
							case TEAMS:
								break;
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
