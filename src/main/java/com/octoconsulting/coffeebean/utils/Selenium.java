package com.octoconsulting.coffeebean.utils;

import org.apache.commons.lang3.SystemUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

public class Selenium {
	public static String getFileContents(String url) {
		WebDriver driver = buildWebDriver();
		driver.get(url);
		driver.manage().window().setSize(new Dimension(1920, 1080));
		driver.findElement(By.id("raw-url")).click();
		String code = driver.getPageSource();
		code = code.substring(84, code.indexOf("</pre></body></html>"));
		driver.close();
		return code;
	}
	
	private static WebDriver buildWebDriver() {
		DesiredCapabilities caps = new DesiredCapabilities();
		//caps.setJavascriptEnabled(false); // we just need to rip some raw text, JavaScript is not needed
		if (SystemUtils.IS_OS_WINDOWS) {
			 caps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, "src/main/resources/phantomjs/windows/phantomjs.exe");
		} else if (SystemUtils.IS_OS_LINUX) {
			caps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, "src/main/resources/phantomjs/linux/phantomjs");
		}
		WebDriver driver = new PhantomJSDriver(caps);
		return driver;
	}
}
