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


public class PollManager extends Communicator {
	private static Map<Long, Poll> polls = new HashMap<>();

	public PollManager() {
		super();
	}

	@Override
	protected void handleInput(Message message) {
		//TODO
	}

	public static void registerPoll(Poll poll) {
		polls.put(poll.getId(), poll);
	}

	private static Poll getPollById(long id) throws NoSuchElementException{
		if(!polls.containsKey(id))
			throw new NoSuchElementException();
		return polls.get(id);
	}

	public long createPoll(String name, List<String> pollOptions) throws InterruptedException {
		Organizer.getSocketHandler().send(getCommunicationId(), Config.getServerCommunicationId(), new CreatePollCommand(name, pollOptions));
		long id = (long) getInput().getData();
		return id;
	}

	public Poll loadPoll(long id) throws InterruptedException {
		Organizer.getSocketHandler().send(getCommunicationId(), Config.getServerCommunicationId(), new LoadPollCommand(id));
		Poll poll = (Poll) getInput().getData();
		return poll;
	}

	public List<String> loadPollOptions(long id) throws InterruptedException {
		Organizer.getSocketHandler().send(getCommunicationId(), Config.getServerCommunicationId(), new LoadPollOptionsCommand(id));
		List<Object> optionsObject = (List<Object>) getInput().getData();
		List<String> options = new ArrayList<>();
		for(Object obj : optionsObject){
			options.add((String) obj);
		}
		return options;
	}

	public boolean vote(long id, String option) throws InterruptedException {
		Organizer.getSocketHandler().send(getCommunicationId(), Config.getServerCommunicationId(), new VotePollCommand(id, option));
		boolean bool = (boolean) getInput().getData();
		return bool;
	}

	public void editPoll(long id) {
		throw new UnsupportedOperationException();
	}
}