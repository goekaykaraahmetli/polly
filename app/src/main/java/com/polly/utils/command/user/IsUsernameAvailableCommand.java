package com.polly.utils.command.user;

public class IsUsernameAvailableCommand {
	private final String username;
	
	public IsUsernameAvailableCommand(String username) {
		this.username = username;
	}

	public String getUsername() {
		return username;
	}
}
