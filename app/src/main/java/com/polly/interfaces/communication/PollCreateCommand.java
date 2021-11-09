package com.polly.interfaces.communication;

import java.util.List;

public class PollCreateCommand {
    public String name;
    public List<String> options;

    public PollCreateCommand(String name, List<String> options) {
        this.name = name;
        this.options = options;
    }
}
