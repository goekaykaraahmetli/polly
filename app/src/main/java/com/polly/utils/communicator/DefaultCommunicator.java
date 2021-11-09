package com.polly.utils.communicator;

import com.polly.utils.Message;

class DefaultCommunicator implements Communicator {
	public DefaultCommunicator() {
		registerCommunicator();
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
	
	private void handleInput(Message message) {
		// TODO change!
		System.out.println("Sender: " + message.getSender());
		System.out.println("Receiver: " + message.getReceiver());
		System.out.println(message.getDataType().cast(message.getData()));
	}
}