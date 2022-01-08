package com.polly.utils.command;

public class LoginCommand {
	private final String idToken;
	
	public LoginCommand(String idToken) {
		this.idToken = idToken;
	}

	public String getIdToken() {
		return idToken;
	}
}