package com.polly.utils.poll;

import com.polly.config.Config;
import com.polly.utils.Area;
import com.polly.utils.command.poll.GetPollResultsCommand;
import com.polly.utils.wrapper.Message;
import com.polly.utils.command.poll.GetPollOptionsCommand;
import com.polly.utils.wrapper.PollOptionsWrapper;
import com.polly.utils.wrapper.PollResultsWrapper;
import com.polly.utils.wrapper.VoteAnswerWrapper;
import com.polly.utils.command.poll.VoteCommand;
import com.polly.utils.command.poll.create.CreateCustomPollCommand;
import com.polly.utils.command.poll.create.CreateGeofencePollCommand;
import com.polly.utils.command.poll.create.CreatePrivatePollCommand;
import com.polly.utils.command.poll.create.CreatePublicPollCommand;
import com.polly.utils.communicator.ResponseCommunicator;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class PollManager {
    private static ResponseCommunicator communicator = initialiseCommunicator();
    private static ResponseCommunicator initialiseCommunicator(){
        return new ResponseCommunicator() {
            @Override
            public void handleInput(Message message) {
                System.out.println("PollManager received message from " + message.getSender() + " with responseId " + message.getResponseId());
                System.out.println("from type: " + message.getDataType().getName());

                for(Long l : communicator.responseIds){
                    System.out.println(l);
                }

                // no default input handling
            }
        };
    }



    public static long createPublicPoll(String name, PollDescription description, LocalDateTime expirationTime, List<String> options) throws IOException {
        Message response = communicator.sendWithResponse(Config.serverCommunicationId, new CreatePublicPollCommand(name, description, expirationTime, options));
        return (long) response.getData();
    }

    public static long createGeofencePoll(String name, PollDescription description, LocalDateTime expirationTime, List<String> options, Area area) throws IOException {
        Message response = communicator.sendWithResponse(Config.serverCommunicationId, new CreateGeofencePollCommand(name, description, expirationTime, options, area));
        return (long) response.getData();
    }

    public static long createCustomPoll(String name, PollDescription description, LocalDateTime expirationTime, List<String> options, List<Long> canSee, List<Long> canSeeResults) throws IOException {
        Message response = communicator.sendWithResponse(Config.serverCommunicationId, new CreateCustomPollCommand(name, description, expirationTime, options, canSee, canSeeResults));
        return (long) response.getData();
    }

    public static long createPrivatePoll(String name, PollDescription description, LocalDateTime expirationTime, List<String> options, long usergroup) throws IOException {
        Message response = communicator.sendWithResponse(Config.serverCommunicationId, new CreatePrivatePollCommand(name, description, expirationTime, options, usergroup));
        return (long) response.getData();
    }

    public static boolean vote(long id, String option) throws IOException {
        Message response = communicator.sendWithResponse(Config.serverCommunicationId, new VoteCommand(id, option));
        VoteAnswerWrapper answer = (VoteAnswerWrapper) response.getData();
        return answer.isSuccessful();
    }

    public static List<String> getPollOptions(long id) throws IOException {
        Message response = communicator.sendWithResponse(Config.serverCommunicationId, new GetPollOptionsCommand(id));
        return ((PollOptionsWrapper) response.getData()).getPollOptions();
    }

    public static Map<String, Integer> getPollResults(long id) throws IOException {
        Message response = communicator.sendWithResponse(Config.serverCommunicationId, new GetPollResultsCommand(id));
        return ((PollResultsWrapper) response.getData()).getPollResults();
    }

}