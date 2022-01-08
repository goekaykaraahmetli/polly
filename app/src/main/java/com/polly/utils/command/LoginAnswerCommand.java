package com.polly.utils.command;

public class LoginAnswerCommand {
    private final boolean successful;

    public LoginAnswerCommand(boolean successful) {
        this.successful = successful;
    }

    public boolean isSuccessful() {
        return successful;
    }
}