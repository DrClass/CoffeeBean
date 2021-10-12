package com.octoconsulting.coffeebean.docker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import com.octoconsulting.coffeebean.objects.CodeBlock;
import com.octoconsulting.coffeebean.objects.CodeFile;
import com.octoconsulting.coffeebean.objects.CodeRepo;
import com.octoconsulting.coffeebean.objects.CodeReturn;
import com.octoconsulting.coffeebean.utils.Language;
import com.octoconsulting.coffeebean.utils.UnzipUtility;
import com.octoconsulting.coffeebean.CodeQueue;
import com.octoconsulting.coffeebean.bots.BotType;

public class DockerManager implements Runnable {
	public static boolean shouldRun = false;
	
	public static final String PATH_START = "/tmp/coffeebean/";
	
	@Override
	public void run() {
		shouldRun = true;
		System.out.println("Docker Manager is running!");
		while (shouldRun) {
			if (CodeQueue.codeQueue.peek() != null) {
				// All of this could be threaded to allow multiple containers to run at once
				try {
					CodeBlock block = CodeQueue.codeQueue.take();
					runContainers(block);
				} catch (InterruptedException | IOException e) {
					e.printStackTrace();
				}
			}
			if (CodeQueue.fileQueue.peek() != null) {
				// All of this could be threaded to allow multiple containers to run at once
				try {
					CodeFile file = CodeQueue.fileQueue.take();
					runContainers(file);
				} catch (InterruptedException | IOException e) {
					e.printStackTrace();
				}
			}
			if (CodeQueue.repoQueue.peek() != null) {
				// All of this could be threaded to allow multiple containers to run at once
				try {
					CodeRepo repo = CodeQueue.repoQueue.take();
					runContainers(repo);
				} catch (InterruptedException | IOException e) {
					e.printStackTrace();
				}
			}
		}
		System.out.println("Docker Manager stopped!");
	}
	
	private static ContainerStrings buildContainerStrings(CodeBlock block) {
		List<String> compile = new ArrayList<String>();
		List<String> execute = new ArrayList<String>();
		compile.add("docker");
		compile.add("run");
		compile.add("--rm");
		compile.add("-v");
		compile.add(PATH_START + block.getIdChain().stream().collect(Collectors.joining()) + "/:/usr/src/myapp");
		compile.add("-w");
		compile.add("/usr/src/myapp");		
		execute.add("docker");
		execute.add("run");
		execute.add("--rm");
		execute.add("-v");
		execute.add(PATH_START + block.getIdChain().stream().collect(Collectors.joining()) + "/:/usr/src/myapp");
		execute.add("-w");
		execute.add("/usr/src/myapp");
		switch (block.getLanguage()) {
			case JAVA:
				compile.add(block.getLanguage().container());
				compile.add("javac");
				compile.add("Main.java");
				execute.add(block.getLanguage().container());
				execute.add("java");
				execute.add("Main");
				break;
			case PYTHON:
				compile.add(block.getLanguage().container());
				execute.add(block.getLanguage().container());
				execute.add("python");
				execute.add("Main.py");
				break;
			case CPP:
				compile.add(block.getLanguage().container());
				compile.add("g++");
				compile.add("-o");
				compile.add("Main");
				compile.add("Main.cpp");
				execute.add(block.getLanguage().container());
				execute.add("./Main");
				break;
			default:
				break;
		}
		String[] compileArray = compile.stream().toArray(String[]::new);
		String[] executeArray = execute.stream().toArray(String[]::new);
		return new ContainerStrings(compileArray, executeArray);
	}
	
	private static ContainerStrings buildContainerStrings(CodeFile file) {
		List<String> compile = new ArrayList<String>();
		List<String> execute = new ArrayList<String>();
		compile.add("docker");
		compile.add("run");
		compile.add("--rm");
		compile.add("-v");
		compile.add(PATH_START + file.getIdChain().stream().collect(Collectors.joining()) + "/:/usr/src/myapp");
		compile.add("-w");
		compile.add("/usr/src/myapp");		
		execute.add("docker");
		execute.add("run");
		execute.add("--rm");
		execute.add("-v");
		execute.add(PATH_START + file.getIdChain().stream().collect(Collectors.joining()) + "/:/usr/src/myapp");
		execute.add("-w");
		execute.add("/usr/src/myapp");
		switch (file.getLanguage()) {
			case JAVA:
				compile.add(file.getLanguage().container());
				compile.add("javac");
				compile.add(file.getFileName() + "." + file.getLanguage().fileExtension());
				execute.add(file.getLanguage().container());
				execute.add("java");
				execute.add(file.getFileName());
				break;
			case PYTHON:
				compile.add(file.getLanguage().container());
				execute.add(file.getLanguage().container());
				execute.add("python");
				execute.add(file.getFileName() + "." + file.getLanguage().fileExtension());
				break;
			case CPP:
				compile.add(file.getLanguage().container());
				compile.add("g++");
				compile.add("-o");
				compile.add(file.getFileName());
				compile.add(file.getFileName()+"."+file.getLanguage().fileExtension());
				execute.add(file.getLanguage().container());
				execute.add("./" + file.getFileName());
				break;
			default:
				break;
		}
		String[] compileArray = compile.stream().toArray(String[]::new);
		String[] executeArray = execute.stream().toArray(String[]::new);
		return new ContainerStrings(compileArray, executeArray);
	}
	
	private static ContainerStrings buildContainerStrings(CodeRepo repo) {
		List<String> compile = new ArrayList<String>();
		List<String> execute = new ArrayList<String>();
		compile.add("docker");
		compile.add("run");
		compile.add("--rm");
		compile.add("-v");
		compile.add(PATH_START + repo.getIdChain().stream().collect(Collectors.joining()) + "/:/usr/src/myapp");
		compile.add("-w");
		compile.add("/usr/src/myapp");		
		execute.add("docker");
		execute.add("run");
		execute.add("--rm");
		execute.add("-v");
		execute.add(PATH_START + repo.getIdChain().stream().collect(Collectors.joining()) + "/:/usr/src/myapp");
		execute.add("-w");
		execute.add("/usr/src/myapp");
		switch (repo.getLanguage()) {
			case JAVA:
				compile.add(repo.getLanguage().container());
				compile.add("javac");
				compile.add(repo.getMain());
				execute.add(repo.getLanguage().container());
				execute.add("java");
				execute.add(repo.getMain());
				break;
			case PYTHON:
				compile.add(repo.getLanguage().container());
				execute.add(repo.getLanguage().container());
				execute.add("python");
				execute.add(repo.getMain());
				break;
			case CPP:
				compile.add(repo.getLanguage().container());
				compile.add("g++");
				compile.add("-o");
				compile.add("Main");
				compile.add(repo.getMain().strip());
				execute.add(repo.getLanguage().container());
				execute.add("./Main");
				break;
			default:
				break;
		}
		String[] compileArray = compile.stream().toArray(String[]::new);
		String[] executeArray = execute.stream().toArray(String[]::new);
		return new ContainerStrings(compileArray, executeArray);
	}
	
	private static void runContainers(CodeBlock block) throws IOException, InterruptedException {
		ContainerStrings strings = buildContainerStrings(block);
		String folderID = block.getIdChain().stream().collect(Collectors.joining());
		createDirectory(folderID);
		createFile(folderID, "Main", block.getLanguage().fileExtension(), block.getCode());
		String[] compileResults = compileExecuteCode(strings.getCompile());
		String[] executeResults = compileExecuteCode(strings.getExecute());
		sendReturnMessages(block.getBotType(), block.getIdChain(), compileResults, executeResults);
		deleteDirectory(new File(PATH_START + folderID + "/"));
	}
	
	private static void runContainers(CodeFile file) throws IOException, InterruptedException {
		ContainerStrings strings = buildContainerStrings(file);
		String folderID = file.getIdChain().stream().collect(Collectors.joining());
		createDirectory(folderID);
		createFile(folderID, file.getFileName(), file.getLanguage().fileExtension(), file.getCode());
		String[] compileResults = compileExecuteCode(strings.getCompile());
		String[] executeResults = compileExecuteCode(strings.getExecute());
		sendReturnMessages(file.getBotType(), file.getIdChain(), compileResults, executeResults);
		deleteDirectory(new File(PATH_START + folderID + "/"));
	}
	
	private static void runContainers(CodeRepo repo) throws IOException, InterruptedException {
		String folderID = repo.getIdChain().stream().collect(Collectors.joining());
		createDirectory(folderID);
		FileUtils.copyURLToFile(new URL(repo.getURL()), new File(PATH_START + folderID + "/archive.zip"));
		UnzipUtility.unzip(PATH_START + folderID + "/archive.zip", PATH_START + folderID + "/");
		// Copy all files from the nested folder to the working dir
		File workingDir = new File(PATH_START + folderID + "/");
		File[] files = workingDir.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				FileUtils.copyDirectory(file, workingDir);
			}
		}
		files = workingDir.listFiles();
		for (File file : files) {
			if (file.getName().equals(".coffeebean")) {
				String content = Files.readString(Path.of(file.getAbsolutePath()));
				List<String> args = Arrays.asList(content.split(" "));
				repo.setLanguage(Language.valueOf(args.get(0)));
				repo.setMain(args.get(1));
			}
		}
		// Done copying files
		if (repo.getLanguage() == null) {
			CodeQueue.codeReturns.put(new CodeReturn(repo.getBotType(), "ERROR: No .coffeebean file found!", repo.getIdChain()));
		} else {
			ContainerStrings strings = buildContainerStrings(repo);
			String[] compileResults = compileExecuteCode(strings.getCompile());
			String[] executeResults = compileExecuteCode(strings.getExecute());
			sendReturnMessages(repo.getBotType(), repo.getIdChain(), compileResults, executeResults);
		}
		deleteDirectory(new File(PATH_START + folderID + "/"));
	}
	
	public static void sendReturnMessages(BotType botType, List<String> idChain, String[] compilerMessages, String[] runtimeMessages) throws InterruptedException {
		if (!StringUtils.isBlank(compilerMessages[0])) {
			CodeQueue.codeReturns.put(new CodeReturn(botType, "Compiler Errors:\n" + compilerMessages[0], idChain));
		}
		if (!StringUtils.isBlank(compilerMessages[1])) {
			CodeQueue.codeReturns.put(new CodeReturn(botType, "Compiler Messages:\n" + compilerMessages[1], idChain));
		}
		if (!StringUtils.isBlank(runtimeMessages[0])) {
			CodeQueue.codeReturns.put(new CodeReturn(botType, "Runtime Errors:\n" + runtimeMessages[0], idChain));
		}
		if (!StringUtils.isBlank(runtimeMessages[1])) {
			CodeQueue.codeReturns.put(new CodeReturn(botType, "Runtime Messages:\n" + runtimeMessages[1], idChain));
		}
		if (StringUtils.isBlank(compilerMessages[0]) && StringUtils.isBlank(compilerMessages[1]) && StringUtils.isBlank(runtimeMessages[0]) && StringUtils.isBlank(runtimeMessages[1])) {
			CodeQueue.codeReturns.put(new CodeReturn(botType, "ERROR - The compiler and or runtime did not return any messages or errors!", idChain));
		}
	}
	
	public static void createDirectory(String id) {
		File path = new File(PATH_START + id + "/");
		path.mkdirs();
	}
	
	public static void createFile(String id, String fileName, String extension, String code) throws IOException {
		File file = new File(PATH_START + id + "/" + fileName + "." + extension);
		file.createNewFile();
		FileWriter myWriter = new FileWriter(file);
		myWriter.write(code);
		myWriter.close();
	}
	
	public static String[] compileExecuteCode(String[] args) throws IOException, InterruptedException {
		ProcessBuilder processBuilder = new ProcessBuilder(args);
		Process process = processBuilder.start();
		boolean success = process.waitFor(30, TimeUnit.SECONDS);
		String[] outputs = new String[2];
		outputs[0] = printError(process);
		outputs[1] = printResults(process);
		if (!success) {
			process.destroyForcibly();
			Thread.sleep(5);
		}
		return outputs;
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
