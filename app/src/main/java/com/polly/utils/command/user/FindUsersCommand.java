package com.polly.utils.command.user;

public class FindUsersCommand {
    private final String prefix;

    public FindUsersCommand(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }
}