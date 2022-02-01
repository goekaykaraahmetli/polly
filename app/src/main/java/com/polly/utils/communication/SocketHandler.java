package com.polly.utils.communication;

import com.polly.utils.encryption.exceptions.FailedDecryptionException;
import com.polly.utils.encryption.exceptions.FailedEncryptionException;
import com.polly.utils.encryption.exceptions.FailedKeyGenerationException;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

public class SocketHandler {
	private Socket socket;
	private InputHandler inputHandler;
	private OutputHandler outputHandler;
	private DataStreamManager dataStreamManager;

	public SocketHandler(String ip, int port, long defaultCommunicationId) throws IOException, FailedKeyGenerationException, InvalidKeySpecException, NoSuchAlgorithmException, FailedDecryptionException, FailedEncryptionException {
		connect(ip, port, 7500);
		init(defaultCommunicationId);
	}

	public SocketHandler(String ip, int port, int timeout, long defaultCommunicationId) throws IOException, FailedKeyGenerationException, InvalidKeySpecException, NoSuchAlgorithmException, FailedDecryptionException, FailedEncryptionException {
		connect(ip, port, timeout);
		init(defaultCommunicationId);
	}

	private void init(long defaultCommunicationId) throws IOException, FailedKeyGenerationException, InvalidKeySpecException, NoSuchAlgorithmException, FailedEncryptionException, FailedDecryptionException {
		dataStreamManager = new DataStreamManager(socket.getInputStream(), socket.getOutputStream(), defaultCommunicationId);
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
	
	private void connect(String ip, int port, int timeout) throws IOException {
		AtomicBoolean connecting = new AtomicBoolean(true);
		new Thread(() -> {
			new Timer().schedule(new TimerTask() {
				@Override
				public void run() {
					connecting.set(false);
				}
			}, timeout);


			try {
				socket = new Socket(ip, port);
			} catch (IOException e) {
				System.out.println("could not connect!");
				e.printStackTrace();
			}
			connecting.set(false);
		}).start();
		while(connecting.get()){}
		if(socket == null){
			throw new IOException("could not connect to the given server!");
		}
    }
	
	public <T> boolean send(long sender, long receiver, long responseId, T data) {
		return outputHandler.send(sender, receiver, responseId, data);
	}

	public <T> boolean send(long sender, long receiver, T data) {
		return outputHandler.send(sender, receiver, data);
	}
}
