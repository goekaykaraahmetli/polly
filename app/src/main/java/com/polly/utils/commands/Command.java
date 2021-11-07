package com.polly.utils.commands;

public class Command {
    private String[] params;
    private String commandName;

    public Command(String commandName, String... params){
        this.commandName = commandName;
        this.params = params;
    }

    public String getCommandName() {
        return commandName;
    }

    public String[] getParams(){
        return params;
    }
}
