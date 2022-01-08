package com.polly.utils.command;

public class RegisterCommand {
	private final String idToken;
	private final String name;
	
	public RegisterCommand(String idToken, String name) {
		this.idToken = idToken;
		this.name = name;
	}

	public String getIdToken() {
		return idToken;
	}

	public String getName() {
		return name;
	}
}