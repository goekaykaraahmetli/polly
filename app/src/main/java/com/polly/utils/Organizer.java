package com.polly.utils;

import java.io.IOException;
import com.polly.config.Config;
import com.polly.utils.communication.SocketHandler;

public class Organizer {
	private static SocketHandler socketHandler;
	static {
		socketHandler = createSocketHandler();
	}
	
	private static SocketHandler createSocketHandler() {
		try {
            return new SocketHandler(Config.SERVER_IP_ADRESS, Config.SERVER_PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
		return null;
	}


	//TODO nutzen
	public static void connectToServer() throws IOException {
		socketHandler = new SocketHandler(Config.SERVER_IP_ADRESS, Config.SERVER_PORT);
	}

	public static <T> boolean send(long sender, long receiver, long responseId, T data) throws IOException{
		if(socketHandler == null){
			throw new IOException("No connection to the server!");
		}

		return socketHandler.send(sender, receiver, responseId, data);
	}

	public static <T> boolean send(long sender, long receiver, T data) throws IOException{
		if(socketHandler == null){
			throw new IOException("No connection to the server!");
		}

		return socketHandler.send(sender, receiver, data);
	}
}
