package com.octoconsulting.coffeebean.docker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient.Request;
import com.octoconsulting.coffeebean.CodeBlock;
import com.octoconsulting.coffeebean.CodeQueue;
import com.octoconsulting.coffeebean.CodeReturn;

public class DockerManager implements Runnable {
	public static boolean shouldRun = false;
	
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
						File path = new File("/tmp/coffeebean/" + folderID + "/");
						path.mkdirs();
						File file = new File("/tmp/coffeebean/" + folderID + "/" + "Main.java");
						file.createNewFile();
						FileWriter myWriter = new FileWriter(file);
						myWriter.write(block.getCode());
						myWriter.close();
						
						//  --rm -v \"$PWD\":/usr/src/myapp openjdk:16 javac Main.java
						ProcessBuilder pb = new ProcessBuilder("docker", "run", "--rm", "-v", path.toString() + ":/usr/src/myapp", "-w", "/usr/src/myapp", "openjdk:16", "javac", "Main.java");
						Process p = pb.start();
						boolean success = p.waitFor(30, TimeUnit.SECONDS);

						System.out.println(path.toString());

						ProcessBuilder runpb = new ProcessBuilder("docker", "run", "--rm", "-v", path.toString() + ":/usr/src/myapp", "-w", "/usr/src/myapp", "openjdk:16", "java", "Main");
						Process runProcess = runpb.start();
						boolean runSuccess = runProcess.waitFor(60, TimeUnit.SECONDS);

						if (p.isAlive() || runProcess.isAlive()) {
							CodeQueue.codeReturns.add(new CodeReturn(block.getBotType(), "Could not get exit code as process is still running", block.getIdChain()));
						} else {
							String exitValue = String.format("Exited with code: %d", p.exitValue());
							CodeQueue.codeReturns.add(new CodeReturn(block.getBotType(), exitValue, block.getIdChain()));
						}
						CodeQueue.codeReturns.add(new CodeReturn(block.getBotType(), "Errors:\n" + printError(p), block.getIdChain()));
						CodeQueue.codeReturns.add(new CodeReturn(block.getBotType(), "Errors:\n" + printError(runProcess), block.getIdChain()));
						CodeQueue.codeReturns.add(new CodeReturn(block.getBotType(), "Output:\n" + printResults(p), block.getIdChain()));
						CodeQueue.codeReturns.add(new CodeReturn(block.getBotType(), "Output:\n" + printResults(runProcess), block.getIdChain()));

						if (!success || !runSuccess) {
							CodeQueue.codeReturns.add(new CodeReturn(block.getBotType(), "TIMEOUT ERROR!!!\nDoing some sudoku...", block.getIdChain()));
							p.destroyForcibly();
							Thread.sleep(2);
						}

						deleteDirectory(path);
					} catch (IOException e) {
						StringWriter sw = new StringWriter();
						e.printStackTrace(new PrintWriter(sw));
						String exception = sw.toString();
						CodeQueue.codeReturns.add(new CodeReturn(block.getBotType(), exception, block.getIdChain()));
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
	
	public static String printError(Process process) throws IOException {
	    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
	    String line = "";
	    String output = "";
	    while ((line = reader.readLine()) != null) {
	        //System.out.println(line);
	    	output += line + "\n";
	    }
	    return output;
	}
}
