package com.polly.utils.communicator;

import com.polly.config.Config;
import com.polly.utils.wrapper.Message;

public class DefaultCommunicator extends Communicator {
	static final int MAX_QUEUE_LENGTH = 20;
	private boolean connecting;

	public DefaultCommunicator() {
		super();
		new Thread(() -> {
			connecting = true;
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
		if(connecting)
			connect(message);
		else {
			System.out.println("Sender: " + message.getSender());
			System.out.println("Receiver: " + message.getReceiver());
			System.out.println(message.getDataType().getName());
		}
	}

	public void connect(Message message) {
		connecting = false;
		if(message.getDataType() != Long.class)
			return;

		Long commId = (Long) message.getData();

		Config.serverCommunicationId = commId;
	}

	public void connecting() {
		connecting = true;
	}
}