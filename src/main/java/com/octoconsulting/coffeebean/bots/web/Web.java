package com.octoconsulting.coffeebean.bots.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;

import com.octoconsulting.coffeebean.CodeQueue;
import com.octoconsulting.coffeebean.objects.CodeBlock;
import com.octoconsulting.coffeebean.objects.CodeReturn;
import com.octoconsulting.coffeebean.utils.Language;
import com.octoconsulting.coffeebean.bots.BotType;

public class Web extends Thread {
	public static ServerSocket socket;
	public static BufferedReader in;
	public static PrintWriter out;
	
	public static LinkedBlockingQueue<CodeReturn> outMessages = new LinkedBlockingQueue<CodeReturn>();
	
	public Web() {
		try {
			socket = new ServerSocket(3001);
			Socket client = socket.accept();
			in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			out = new PrintWriter(client.getOutputStream());
			new Thread(() -> {
				while (true) {
					try {
						String message = in.readLine();
						// We don't actually need an id for this one, so just make a random one.
						List<String> idChain = new ArrayList<String>();
						idChain.add(String.valueOf(new Random().nextLong()));
						// THIS IS A REALLY BAD WAY OF DOING THIS!
						List<String> temp = Arrays.asList(message.split(":"));
						CodeQueue.codeQueue.put(new CodeBlock(BotType.WEB, Language.valueOf(temp.get(0)), temp.get(1), idChain));
					} catch (IOException | InterruptedException e) {
						e.printStackTrace();
					}
				}
			}).start();
			new Thread(() -> {
				while (true) {
					if (outMessages.peek() != null) {
						try {
							CodeReturn codeReturn = outMessages.take();
							out.println(codeReturn.getOutput());
							out.flush();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			});
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void reply(CodeReturn codeReturn) {
		outMessages.add(codeReturn);
	}
	
	public void run() {
		@SuppressWarnings("unused")
		Web web = new Web();
	}
}
