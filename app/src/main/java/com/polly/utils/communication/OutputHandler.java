package com.polly.utils.communication;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import com.polly.utils.wrapper.Message;

class OutputHandler extends DataStreamHandler{
	private static final int MAX_QUEUE_LENGTH = 200;
	protected final BlockingQueue<Message> handleQueue = new ArrayBlockingQueue<>(MAX_QUEUE_LENGTH);
	
	protected OutputHandler(DataStreamManager dataStreamManager) {
		super(dataStreamManager);
	}
	
	@Override
	protected void handleDataStream() {
		try {
			dataStreamManager.send(handleQueue.take());
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public <T> boolean send(long sender, long receiver, long responseId, T data) {
		return handleQueue.offer(new Message(sender, receiver, responseId, data.getClass(), data));
	}

	public <T> boolean send(long sender, long receiver, T data) {
		return handleQueue.offer(new Message(sender, receiver, Message.getNextResponseId(), data.getClass(), data));
	}
}