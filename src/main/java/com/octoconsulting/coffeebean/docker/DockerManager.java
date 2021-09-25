package com.octoconsulting.coffeebean.docker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.lang3.SystemUtils;

import com.octoconsulting.coffeebean.CodeBlock;
import com.octoconsulting.coffeebean.CodeQueue;
import com.octoconsulting.coffeebean.CodeReturn;

public class DockerManager implements Runnable {
	public static boolean shouldRun = false;
	public static final String DOCKER_START = "/c/Program Files/Docker Toolbox/start.cmd";
	
	@Override
	public void run() {
		shouldRun = true;
		System.out.println("Docker Manager is running!");
		while (shouldRun) {
			if (CodeQueue.codeQueue.peek() != null) {
				// All of this could be threaded to allow multiple containers to run at once
				try {
					CodeBlock block = CodeQueue.codeQueue.take();
					String folderID = block.getIdChain().stream().collect(Collectors.joining());
					String compile = "";
					String run = "";
					switch (block.getLanguage().toLowerCase()) {
						case "java":
							compile = "openjdk:16 javac Main.java";
							run = "openjdk:16 java Main";
							break;
						default:
							break;
					}
					try {
						File path = new File("/home/" + folderID + "/");
						path.mkdirs();
						
						FileWriter myWriter = new FileWriter("/home/" + folderID + "/" + "Main.java");
						myWriter.write(block.getCode());
						myWriter.close();
						
						
						
						ProcessBuilder pb = new ProcessBuilder("C:\\Users\\Ella\\eclipse-workspace\\CoffeeBean\\CoffeeBean\\src\\main\\go\\hello.exe", "openjdk:16", "javac", "Main.java");
						Process p = pb.start();
						boolean success = p.waitFor(30, TimeUnit.SECONDS);
						if (!success) {
							CodeQueue.codeReturns.add(new CodeReturn(block.getBotType(), "TIMEOUT ERROR!!!\nDoing some sudoku...", block.getIdChain()));
						}
						
						pb = new ProcessBuilder("C:\\Users\\Ella\\eclipse-workspace\\CoffeeBean\\CoffeeBean\\src\\main\\go\\hello.exe", "openjdk:16", "java", "Main");
						p = pb.start();
						success = p.waitFor(30, TimeUnit.SECONDS);
						if (!success) {
							CodeQueue.codeReturns.add(new CodeReturn(block.getBotType(), "TIMEOUT ERROR!!!\nDoing some sudoku...", block.getIdChain()));
						}

						System.out.println(path.toString());
						CodeQueue.codeReturns.add(new CodeReturn(block.getBotType(), printResults(p), block.getIdChain()));
						deleteDirectory(path);
					} catch (IOException e) {
						e.printStackTrace();
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		System.out.println("Docker Manager stopped!");
	}
	
	public static void deleteDirectory(File file) throws IOException {
		if (file.isDirectory()) {
			File[] entries = file.listFiles();
			if (entries != null) {
				for (File entry : entries) {
					deleteDirectory(entry);
				}
			}
		}
		if (!file.delete()) {
			throw new IOException("Failed to delete " + file);
		}
	}
	
	public static String printResults(Process process) throws IOException {
	    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
	    String line = "";
	    String output = "";
	    while ((line = reader.readLine()) != null) {
	        //System.out.println(line);
	    	output += line + "\n";
	    }
	    return output;
	}
}
