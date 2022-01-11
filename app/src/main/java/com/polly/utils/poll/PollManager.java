package com.polly.utils.poll;

import com.polly.config.Config;
import com.polly.utils.Area;
import com.polly.utils.command.GetMyPollsCommand;
import com.polly.utils.command.GetParticipatedPollsCommand;
import com.polly.utils.command.poll.GetPollResultsCommand;
import com.polly.utils.wrapper.ErrorWrapper;
import com.polly.utils.wrapper.Message;
import com.polly.utils.command.poll.GetPollOptionsCommand;
import com.polly.utils.wrapper.PollListWrapper;
import com.polly.utils.wrapper.PollOptionsWrapper;
import com.polly.utils.wrapper.PollResultsWrapper;
import com.polly.utils.command.poll.create.CreateCustomPollCommand;
import com.polly.utils.command.poll.create.CreateGeofencePollCommand;
import com.polly.utils.command.poll.create.CreatePrivatePollCommand;
import com.polly.utils.command.poll.create.CreatePublicPollCommand;
import com.polly.utils.communicator.ResponseCommunicator;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

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
        if(response.getDataType() == Long.class)
            return (long) response.getData();
        if(response.getDataType() == ErrorWrapper.class)
            throw new IOException(((ErrorWrapper) response.getData()).getMessage());
        throw new IOException("Something went wrong!");
    }

    public static long createGeofencePoll(String name, PollDescription description, LocalDateTime expirationTime, List<String> options, Area area) throws IOException {
        Message response = communicator.sendWithResponse(Config.serverCommunicationId, new CreateGeofencePollCommand(name, description, expirationTime, options, area));
        if(response.getDataType() == Long.class)
            return (long) response.getData();
        if(response.getDataType() == ErrorWrapper.class)
            throw new IOException(((ErrorWrapper) response.getData()).getMessage());
        throw new IOException("Something went wrong!");
    }

    public static long createCustomPoll(String name, PollDescription description, LocalDateTime expirationTime, List<String> options, List<String> canSee, List<String> canSeeResults) throws IOException {
        Message response = communicator.sendWithResponse(Config.serverCommunicationId, new CreateCustomPollCommand(name, description, expirationTime, options, canSee, canSeeResults));
        if(response.getDataType() == Long.class)
            return (long) response.getData();
        if(response.getDataType() == ErrorWrapper.class)
            throw new IOException(((ErrorWrapper) response.getData()).getMessage());
        throw new IOException("Something went wrong!");
    }

    public static long createPrivatePoll(String name, PollDescription description, LocalDateTime expirationTime, List<String> options, long usergroup) throws IOException {
        Message response = communicator.sendWithResponse(Config.serverCommunicationId, new CreatePrivatePollCommand(name, description, expirationTime, options, usergroup));
        if(response.getDataType() == Long.class)
            return (long) response.getData();
        if(response.getDataType() == ErrorWrapper.class)
            throw new IOException(((ErrorWrapper) response.getData()).getMessage());
        throw new IOException("Something went wrong!");
    }

    public static boolean vote(long id, String option) throws IOException {
        Message response = communicator.sendWithResponse(Config.serverCommunicationId, new VoteCommand(id, option));
        if(response.getDataType().equals(Boolean.class))
            return (boolean) response.getData();
        if(response.getDataType() == ErrorWrapper.class)
            throw new IOException(((ErrorWrapper) response.getData()).getMessage());
        throw new IOException("Something went wrong!");
    }

    public static PollOptionsWrapper getPollOptions(long id) throws IOException {
        Message response = communicator.sendWithResponse(Config.serverCommunicationId, new GetPollOptionsCommand(id));
        if(response.getDataType() == PollOptionsWrapper.class)
            return (PollOptionsWrapper) response.getData();
        if(response.getDataType() == ErrorWrapper.class)
            throw new IOException(((ErrorWrapper) response.getData()).getMessage());
        throw new IOException("Something went wrong!");
    }

    public static PollResultsWrapper getPollResults(long id) throws IOException {
        Message response = communicator.sendWithResponse(Config.serverCommunicationId, new GetPollResultsCommand(id));
        if(response.getDataType() == PollResultsWrapper.class)
            return (PollResultsWrapper) response.getData();
        if(response.getDataType() == ErrorWrapper.class)
            throw new IOException(((ErrorWrapper) response.getData()).getMessage());
        throw new IOException("Something went wrong!");
    }

    public static List<PollResultsWrapper> getMyPolls() throws IOException {
        Message response = communicator.sendWithResponse(Config.serverCommunicationId, new GetMyPollsCommand());
        if(response.getDataType() == PollListWrapper.class)
            return ((PollListWrapper) response.getData()).getList();
        if(response.getDataType() == ErrorWrapper.class)
            throw new IOException(((ErrorWrapper) response.getData()).getMessage());
        throw new IOException("Something went wrong!");
    }

    public static List<PollResultsWrapper> getParticipatedPolls() throws IOException {
        Message response = communicator.sendWithResponse(Config.serverCommunicationId, new GetParticipatedPollsCommand());
        if(response.getDataType() == PollListWrapper.class)
            return ((PollListWrapper) response.getData()).getList();
        if(response.getDataType() == ErrorWrapper.class)
            throw new IOException(((ErrorWrapper) response.getData()).getMessage());
        throw new IOException("Something went wrong!");
    }
}