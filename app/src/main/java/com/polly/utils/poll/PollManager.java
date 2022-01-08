package com.polly.utils.poll;

import com.polly.config.Config;
import com.polly.utils.Message;
import com.polly.utils.commandold.CreatePollCommand;
import com.polly.utils.commandold.ErrorCommand;
import com.polly.utils.commandold.GetMyPollsCommand;
import com.polly.utils.commandold.GetParticipatedPollsCommand;
import com.polly.utils.commandold.LoadPollCommand;
import com.polly.utils.commandold.LoadPollOptionsCommand;
import com.polly.utils.commandold.VotePollCommand;
import com.polly.utils.communication.DataStreamManager;
import com.polly.utils.communicator.ResponseCommunicator;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;


public class PollManager{
	private static Map<Long, Poll> polls = new HashMap<>();
	private static ResponseCommunicator communicator = initialiseCommunicator();

	public static void registerPoll(Poll poll) {
		polls.put(poll.getId(), poll);
	}

	private static Poll getPollById(long id) throws NoSuchElementException{
		if(!polls.containsKey(id))
			throw new NoSuchElementException();
		return polls.get(id);
	}

	public static long createPoll(String name, List<String> pollOptions) throws InterruptedException, IllegalArgumentException, IOException {
		Message response = communicator.sendWithResponse(Config.getServerCommunicationId(), Message.getNextResponseId(), new CreatePollCommand(name, pollOptions));
		if(response.getDataType().equals(Long.class)){
			return (long) response.getData();
		} else if(response.getDataType().equals(ErrorCommand.class)){
			throw new IllegalArgumentException(((ErrorCommand) response.getData()).getMessage());
		} else {
			throw new InterruptedException("received response of wrong dataType");
		}
	}

	public static Poll loadPoll(long id) throws InterruptedException, IllegalArgumentException, IOException {
		Message response = communicator.sendWithResponse(Config.getServerCommunicationId(), Message.getNextResponseId(), new LoadPollCommand(id));
		if(response.getDataType().equals(Poll.class)){
			return (Poll) response.getData();
		} else if(response.getDataType().equals(ErrorCommand.class)){
			throw new IllegalArgumentException(((ErrorCommand) response.getData()).getMessage());
		} else {
			throw new InterruptedException("received response of wrong dataType");
		}
	}

	public static Poll loadPollOptions(long id) throws InterruptedException, IllegalArgumentException, IOException {
		Message response = communicator.sendWithResponse(Config.getServerCommunicationId(), Message.getNextResponseId(), new LoadPollOptionsCommand(id));
		if(response.getDataType().equals(Poll.class)){
			return (Poll) response.getData();
		} else if(response.getDataType().equals(ErrorCommand.class)){
			throw new IllegalArgumentException(((ErrorCommand) response.getData()).getMessage());
		} else {
			throw new InterruptedException("received response of wrong dataType");
		}
	}

	public static boolean vote(long id, String option) throws InterruptedException, IllegalArgumentException, IOException {
		Message response = communicator.sendWithResponse(Config.getServerCommunicationId(), Message.getNextResponseId(), new VotePollCommand(id, option));
		if(response.getDataType().equals(Boolean.class)){
			return (boolean) response.getData();
		} else if(response.getDataType().equals(ErrorCommand.class)){
			throw new IllegalArgumentException(((ErrorCommand) response.getData()).getMessage());
		} else {
			throw new InterruptedException("received response of wrong dataType");
		}
	}

	public static void editPoll(long id) {
		throw new UnsupportedOperationException();
	}

	public static List<Poll> getParticipatedPolls() throws InterruptedException, IllegalArgumentException, IOException {
		Message response = communicator.sendWithResponse(Config.getServerCommunicationId(), Message.getNextResponseId(), new GetParticipatedPollsCommand());
		if(DataStreamManager.isList(response.getDataType())) {
			if (response.getGenerics().get(0).equals(Poll.class)) {
				return (List<Poll>) response.getData();
			} else {
				throw new InterruptedException("received response with wrong generics");
			}
		} else if(response.getDataType().equals(ErrorCommand.class)){
			throw new IllegalArgumentException(((ErrorCommand) response.getData()).getMessage());
		} else {
			throw new InterruptedException("received response of wrong dataType");
		}
	}

	public static List<Poll> getMyPolls() throws InterruptedException, IllegalArgumentException, IOException {
		Message response = communicator.sendWithResponse(Config.getServerCommunicationId(), Message.getNextResponseId(), new GetMyPollsCommand());
		if(DataStreamManager.isList(response.getDataType())) {
			if (response.getGenerics().get(0).equals(Poll.class)) {
				return (List<Poll>) response.getData();
			} else {
				throw new InterruptedException("received response with wrong generics");
			}
		} else if(response.getDataType().equals(ErrorCommand.class)){
			throw new IllegalArgumentException(((ErrorCommand) response.getData()).getMessage());
		} else {
			throw new InterruptedException("received response of wrong dataType");
		}
	}

	private static ResponseCommunicator initialiseCommunicator(){
		return new ResponseCommunicator() {
			@Override
			public void handleInput(Message message) {
				//TODO

				System.out.println("received message from " + message.getSender() + " with responseId " + message.getResponseId());
				System.out.println("from type: " + message.getDataType().getName());

				for(Long l : communicator.responseIds){
					System.out.println(l);
				}

				// no default input handling
			}
		};
	}
}