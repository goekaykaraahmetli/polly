package com.polly.utils;

import com.google.firebase.auth.FirebaseAuth;
import com.polly.config.Config;
import com.polly.utils.communication.SocketHandler;
import com.polly.utils.encryption.exceptions.FailedDecryptionException;
import com.polly.utils.encryption.exceptions.FailedEncryptionException;
import com.polly.utils.encryption.exceptions.FailedKeyGenerationException;
import com.polly.utils.encryption.utils.CipherKeyGenerator;
import com.polly.visuals.LoginFragment;
import com.polly.visuals.MainActivity;

import java.io.IOException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.SecretKey;

public class Organizer {
	private static SocketHandler socketHandler;
	private static boolean initialised = false;
	private static MainActivity mainActivity;

	private static final long DEFAULT_COMMUNICATION_ID = 0L;
	private static boolean loggedIn;

	static {
		createSocketHandler(7500);
	}

	public Organizer(MainActivity mainActivity){
		super();
		Organizer.mainActivity = mainActivity;
		while(!initialised) {
			emptyMethode();
		}

		if(FirebaseAuth.getInstance().getCurrentUser() != null){
			LoginFragment.sendTokenToServer(true);
		}
	}

	private static void createSocketHandler(int timeout){
		new Thread(() -> {
			try {
				socketHandler = new SocketHandler(Config.SERVER_IP_ADRESS, Config.SERVER_PORT, timeout, DEFAULT_COMMUNICATION_ID);
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
		socketHandler = null;
		createSocketHandler(500);
		while(!initialised){
			emptyMethode();
		}
		if(socketHandler != null){
			if(FirebaseAuth.getInstance().getCurrentUser() != null)
				LoginFragment.sendTokenToServer(true);
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

	public static void timedOut() {
		socketHandler = null;
	}

	public static MainActivity getMainActivity() {
		return mainActivity;
	}

	public static SocketHandler getSocketHandler() {
		return socketHandler;
	}

	public static boolean isLoggedIn() {
		return loggedIn;
	}

	public static void setLoggedIn(boolean loggedIn) {
		Organizer.loggedIn = loggedIn;
	}
}
