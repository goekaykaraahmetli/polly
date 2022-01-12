package com.polly.utils;

import java.io.IOException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import com.polly.config.Config;
import com.polly.utils.communication.SocketHandler;
import com.polly.utils.encryption.exceptions.FailedDecryptionException;
import com.polly.utils.encryption.exceptions.FailedEncryptionException;
import com.polly.utils.encryption.exceptions.FailedKeyGenerationException;
import com.polly.utils.encryption.utils.CipherKeyGenerator;
import com.polly.visuals.MainActivity;

import javax.crypto.SecretKey;

public class Organizer {
	private static SocketHandler socketHandler;
	private static boolean initialised = false;
	static {
		createSocketHandler(7500);
	}

	public Organizer(){
		super();
		while(!initialised) {
			emptyMethode();
		}
	}
	/*private static void sendNotifications(){

	}*/
	private static void createSocketHandler(int timeout){
		new Thread(() -> {
			try {
				socketHandler = new SocketHandler(Config.SERVER_IP_ADRESS, Config.SERVER_PORT, timeout);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (FailedKeyGenerationException e) {
				e.printStackTrace();
			} catch (InvalidKeySpecException e) {
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} catch (FailedDecryptionException e) {
				e.printStackTrace();
			} catch (FailedEncryptionException e) {
				e.printStackTrace();
			}
			initialised = true;
		}).start();
	}


	public static void tryReconnectToServer() {
		if(initialised == false)
			return;

		initialised = false;
		createSocketHandler(500);
		while(!initialised){
			emptyMethode();
		}
	}

	public static <T> boolean send(long sender, long receiver, long responseId, T data) throws IOException{
		if(socketHandler == null){
			tryReconnectToServer();
			if(socketHandler == null)
				throw new IOException("No connection to the server!");
		}

		return socketHandler.send(sender, receiver, responseId, data);
	}

	public static <T> boolean send(long sender, long receiver, T data) throws IOException{
		if(socketHandler == null){
			tryReconnectToServer();
			if(socketHandler == null)
				throw new IOException("No connection to the server!");
		}

		return socketHandler.send(sender, receiver, data);
	}

	private static SecretKey generateSecretKey() {
		try {
			return CipherKeyGenerator.generateAESSecretKey();
		} catch (FailedKeyGenerationException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static KeyPair generateKeyPair() {
		try {
			return CipherKeyGenerator.generateRSAKeyPair(8192);
		} catch (FailedKeyGenerationException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void emptyMethode() {
		// empty methode
	}
}
