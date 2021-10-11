package com.octoconsulting.coffeebean;

import com.octoconsulting.coffeebean.bots.discord.Discord;
import com.octoconsulting.coffeebean.bots.slack.Slack;
import com.octoconsulting.coffeebean.bots.web.Web;
import com.octoconsulting.coffeebean.docker.DockerManager;

public class CoffeeBean {
	
	public static void main(String[] args) {
		Discord.init();
		//new Thread(new Slack()).start();
		//new Thread(new Web()).start();
		new Thread(new DockerManager()).start();
		new Thread(new CodeQueue()).start();
	}
}
