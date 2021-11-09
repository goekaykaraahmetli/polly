package com.polly.utils.communication;

import java.io.IOException;
import java.util.NoSuchElementException;

import com.polly.utils.Message;
import com.polly.utils.communicator.CommunicatorManager;

class InputHandler extends DataStreamHandler{	
	
	protected InputHandler(DataStreamManager dataStreamManager) {
		super(dataStreamManager);
	}

	@Override
	protected void handleDataStream() {
		try {
			Message message = dataStreamManager.receive();
			System.out.println("received input");
			System.out.println("receiver: " + message.getReceiver());

			CommunicatorManager.getCommunicatorById(message.getReceiver()).addInput(message);
		} catch (NoSuchElementException | ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}