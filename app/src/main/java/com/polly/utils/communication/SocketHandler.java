package com.polly.utils.communication;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

public class SocketHandler {
	private Socket socket;
	private InputHandler inputHandler;
	private OutputHandler outputHandler;
	
	public SocketHandler(String ip, int port) throws IOException {
		connect(ip, port);
		init();
	}
	
	public SocketHandler(Socket socket) throws IOException{
		this.socket = socket;
		init();
	}
	
	private void init() throws IOException{
		DataStreamManager dataStreamManager = new DataStreamManager(socket.getInputStream(), socket.getOutputStream());
        inputHandler = new InputHandler(dataStreamManager);
        outputHandler = new OutputHandler(dataStreamManager);
		inputHandler.start();
		outputHandler.start();
	}
	
	public void close() {
		new Thread(() -> {
			try {
				socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}).start();
		inputHandler.stopHandler();
		outputHandler.stopHandler();
	}
	
	private void connect(String ip, int port) throws IOException {
		connect(ip, port, 0);
    }
	
	private void connect(String ip, int port, int tryCounter) throws IOException {
		if(tryCounter > 10) {
			throw new IOException("could not connect to the given server!");
		}
		
        AtomicBoolean connecting = new AtomicBoolean(true);
        new Thread(() -> {
            try {
            	socket = new Socket(ip, port);
            } catch (IOException e) {
                e.printStackTrace();
            }
            connecting.set(false);
        }).start();

        while(connecting.get()){}
        
        if(socket == null){
            connect(ip, port, tryCounter+1);
        }
    }
	
	public <T> boolean send(long sender, long receiver, T data) {
		return outputHandler.send(sender, receiver, data);
	}
}
