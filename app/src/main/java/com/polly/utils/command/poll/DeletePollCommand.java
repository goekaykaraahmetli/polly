package com.polly.utils.command.poll;

public class DeletePollCommand {
    private final long id;

    public DeletePollCommand(long id) {
        this.id = id;
    }

    public long getId(){
        return id;
    }
}
