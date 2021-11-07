package com.polly.utils.commands;

import java.util.List;

public abstract class CommandCreator {
    public enum CommandNames{
        POLLCOMMAND {String getString() {return "PollCommand";}};


        abstract String getString();
    }

    public enum PollCommandActions{
        CREATE {String getString() {return "create";}},
        LOAD {String getString() {return "load";}},
        VOTE {String getString() {return "vote";}};

        abstract String getString();
    }

    private static Command createCommand(String commandName, String... args){
        return new Command(commandName, args);
    }

    public static Command createPollCommand(PollCommandActions action, String pollName, String[] pollOptions){
        String[] args = new String[pollOptions.length + 2];
        args[0] = action.getString();
        args[1] = pollName;
        System.arraycopy(pollOptions, 0, args, 2, pollOptions.length);
        return CommandCreator.createCommand(CommandNames.POLLCOMMAND.getString(), args);
    }

    public static Command createPollCommand(PollCommandActions action, String pollName, List<String> pollOptions){
        String[] args = new String[pollOptions.size() + 2];
        args[0] = action.getString();
        args[1] = pollName;
        for(int i = 0;i<pollOptions.size();i++){
            args[i+2] = pollOptions.get(i);
        }
        return CommandCreator.createCommand(CommandNames.POLLCOMMAND.getString(), args);
    }

    public static Command createPollCommand(PollCommandActions action, String pollName, String pollOption){
        String[] args = new String[3];
        args[0] = action.getString();
        args[1] = pollName;
        args[2] = pollOption;
        return CommandCreator.createCommand(CommandNames.POLLCOMMAND.getString(), args);
    }
    public static Command createPollCommand(PollCommandActions action, String pollName){
        String[] args = new String[2];
        args[0] = action.getString();
        args[1] = pollName;
        return CommandCreator.createCommand(CommandNames.POLLCOMMAND.getString(), args);
    }
}