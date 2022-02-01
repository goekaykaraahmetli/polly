package com.polly.utils.communicator;

import com.polly.utils.wrapper.Message;

public class DefaultCommunicator extends Communicator {

	public DefaultCommunicator() {
		super();
		new Thread(() -> {
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
		System.out.println("Sender: " + message.getSender());
		System.out.println("Receiver: " + message.getReceiver());
		System.out.println(message.getDataType().getName());
	}
}