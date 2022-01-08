package com.polly.utils.command;

public class ErrorCommand {
    private final String message;

    public ErrorCommand(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}