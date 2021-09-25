package com.octoconsulting.coffeebean.utils;

import org.apache.commons.lang3.SystemUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

public class Selenium {
	private WebDriver driver;
	private Github github;
	
	public Selenium() {
		DesiredCapabilities caps = new DesiredCapabilities();
		caps.setJavascriptEnabled(false); // we just need to rip some raw text, js is not needed
		if (SystemUtils.IS_OS_WINDOWS) {
			 caps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, "phantomjs/windows/phantomjs.exe");
		} else if (SystemUtils.IS_OS_LINUX) {
			caps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, "phantomjs/linux/phantomjs");
		}
		driver = new PhantomJSDriver(caps);
	}

	public WebDriver getDriver() {
		return driver;
	}
	
	public Github github() {
		return github;
	}

	public class Github {
		public boolean isValidLink(String link) {
			getDriver().get(link);
			return false;
		}
	}
}
