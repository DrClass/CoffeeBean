package com.octoconsulting.coffeebean;

import com.octoconsulting.coffeebean.bots.discord.Discord;
import com.octoconsulting.coffeebean.docker.DockerManager;
import com.octoconsulting.coffeebean.utils.Selenium;

public class CoffeeBean {
	public static Selenium selenium;
	
	public static void main(String[] args) {
		Discord.init();
		//selenium = new Selenium();
		new Thread(new DockerManager()).start();
		new Thread(new CodeQueue()).start();
	}
}
