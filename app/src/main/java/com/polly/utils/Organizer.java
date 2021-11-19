package com.polly.utils;

import java.io.IOException;

import com.polly.config.Config;
import com.polly.utils.communication.SocketHandler;
import com.polly.utils.poll.PollManager;

public class Organizer {
	private static final SocketHandler socketHandler;
	static {
		socketHandler = createSocketHandler();
	}
	
	private static SocketHandler createSocketHandler() {
		SocketHandler sh = null;
		try {
            sh = new SocketHandler(Config.SERVER_IP_ADRESS, Config.SERVER_PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
		return sh;
	}
	
	public static SocketHandler getSocketHandler() {
		return socketHandler;
	}
}
