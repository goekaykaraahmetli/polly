package com.polly.utils.command;

public class RequestPollUpdatesCommand {
    private final long pollId;
    private final int startStop;

    public enum RequestType {
        START(1), STOP(0);

        private int value;

        RequestType(int value){
            this.value = value;
        }

        public int getValue(){
            return value;
        }
    }

    public RequestPollUpdatesCommand(long pollId, RequestType requestType){
        this.pollId = pollId;
        this.startStop = requestType.getValue();
    }

    public long getPollId(){
        return pollId;
    }

    public int getStartStop(){
        return startStop;
    }
}
