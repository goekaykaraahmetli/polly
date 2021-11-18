package com.polly.utils.poll;

import com.polly.config.Config;
import com.polly.utils.Message;
import com.polly.utils.Organizer;
import com.polly.utils.command.CreatePollCommand;
import com.polly.utils.command.LoadPollCommand;
import com.polly.utils.command.LoadPollOptionsCommand;
import com.polly.utils.command.VotePollCommand;
import com.polly.utils.communicator.Communicator;
import com.polly.utils.communicator.CommunicatorManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;


public class PollManager{
	private static Map<Long, Poll> polls = new HashMap<>();
	private static Communicator communicator = initialiseCommunicator();

	public static void registerPoll(Poll poll) {
		polls.put(poll.getId(), poll);
	}

	private static Poll getPollById(long id) throws NoSuchElementException{
		if(!polls.containsKey(id))
			throw new NoSuchElementException();
		return polls.get(id);
	}

	public static long createPoll(String name, List<String> pollOptions) throws InterruptedException {
		Organizer.getSocketHandler().send(communicator.getCommunicationId(), Config.getServerCommunicationId(), new CreatePollCommand(name, pollOptions));
		long id = (long) communicator.getInput().getData();
		return id;
	}

	public static Poll loadPoll(long id) throws InterruptedException {
		Organizer.getSocketHandler().send(communicator.getCommunicationId(), Config.getServerCommunicationId(), new LoadPollCommand(id));
		Poll poll = (Poll) communicator.getInput().getData();
		return poll;
	}

	public static Poll loadPollOptions(long id) throws InterruptedException {
		Organizer.getSocketHandler().send(communicator.getCommunicationId(), Config.getServerCommunicationId(), new LoadPollOptionsCommand(id));
		return (Poll) communicator.getInput().getData();
	}

	public static boolean vote(long id, String option) throws InterruptedException {
		Organizer.getSocketHandler().send(communicator.getCommunicationId(), Config.getServerCommunicationId(), new VotePollCommand(id, option));
		boolean bool = (boolean) communicator.getInput().getData();
		return bool;
	}

	public static void editPoll(long id) {
		throw new UnsupportedOperationException();
	}

	private static Communicator initialiseCommunicator(){
		return new Communicator() {
			@Override
			protected void handleInput(Message message) {
				// no default input handling
			}
		};
	}
}