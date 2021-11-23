package com.polly.config;

public class Config {
	public static final String SERVER_IP_ADRESS = "pollyserver.ddns.net";
	public static final int SERVER_PORT = 1337;
	public static final int DATA_STREAM_MANAGER_REFRESH_DELAY = 20;

	public static long serverCommunicationId;

	private Config() {
		super();
	}

	public static void setServerCommunicationID(long id){
		serverCommunicationId = id;
	}

	public static long getServerCommunicationId(){
		return serverCommunicationId;
	}
}