package com.polly.utils.communicator;

import java.util.concurrent.ArrayBlockingQueue;

import com.polly.config.Config;
import com.polly.utils.wrapper.Message;

public class DefaultCommunicator extends Communicator {
	static final int MAX_QUEUE_LENGTH = 20;
	final ArrayBlockingQueue<Message> inputQueue = new ArrayBlockingQueue<>(MAX_QUEUE_LENGTH);

	public DefaultCommunicator() {
		super();
		new Thread(() -> {
			try {
				Config.serverCommunicationId = getInput().getSender();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			while (true) {
				try {
					handleInput(getInput());
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
	}

	public void handleInput(Message message) {
		// TODO change!
		System.out.println("Sender: " + message.getSender());
		System.out.println("Receiver: " + message.getReceiver());
		System.out.println(message.getDataType().getName());
	}
}