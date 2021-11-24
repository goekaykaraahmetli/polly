package com.polly.utils.encryption.utils;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import com.polly.utils.encryption.exceptions.FailedKeyGenerationException;

public class CipherKeyGenerator {
	private static final String ALGORITHM_AES = "AES";
	private static final String ALGORITHM_RSA = "RSA";
	
	private CipherKeyGenerator() {
		super();
	}
	
	public static SecretKey generateAESSecretKey() throws FailedKeyGenerationException{
		try {
			return KeyGenerator.getInstance(ALGORITHM_AES).generateKey();
		} catch (NoSuchAlgorithmException e) {
			throw new FailedKeyGenerationException(e);
		}
	}
	
	public static KeyPair generateRSAKeyPair(int keySize) throws FailedKeyGenerationException {
		try {
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM_RSA);
			keyPairGenerator.initialize(keySize);
			return keyPairGenerator.generateKeyPair();
		} catch(NoSuchAlgorithmException e) {
			throw new FailedKeyGenerationException(e);
		}
	}
}
