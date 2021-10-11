package com.octoconsulting.coffeebean.docker;

public class ContainerStrings {
	private String[] compile;
	private String[] execute;
	
	public ContainerStrings(String[] compile, String[] execute) {
		this.compile = compile;
		this.execute = execute;
	}

	public String[] getCompile() {
		return compile;
	}

	public String[] getExecute() {
		return execute;
	}
}
