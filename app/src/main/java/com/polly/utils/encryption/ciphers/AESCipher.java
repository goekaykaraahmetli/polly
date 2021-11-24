package com.polly.utils.encryption.ciphers;

import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

import com.polly.encryption.exceptions.FailedDecryptionException;
import com.polly.encryption.exceptions.FailedEncryptionException;

public class AESCipher {
	private static final String CIPHER_TRANSFORMATION_GCM = "AES/GCM/NoPadding";
	
	private static final int IV_LENGTH_BYTE = 12;
	private static final int TAG_LENGTH_BIT = 128;
	
	private AESCipher() {
		super();
	}
	
	public static byte[] encrypt(byte[] input, SecretKey secretKey) throws FailedEncryptionException {
		try {
			Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION_GCM);
			byte[] iv = new byte[IV_LENGTH_BYTE];
			SecureRandom secureRandom = new SecureRandom();
			secureRandom.nextBytes(iv);
			GCMParameterSpec paramSpec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
			cipher.init(Cipher.ENCRYPT_MODE, secretKey, paramSpec);
			byte[] encrypted = cipher.doFinal(input);
			
			return concatGCM(iv, encrypted);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
			throw new FailedEncryptionException(e);
		}
		
	}
	
	public static byte[] decrypt(byte[] input, SecretKey secretKey) throws FailedDecryptionException {
		try {
			ByteBuffer byteBuffer = ByteBuffer.wrap(input);
			int ivLength = byteBuffer.get();
			byte[] iv = new byte[ivLength];
			byteBuffer.get(iv);
			byte[] encrypted = new byte[byteBuffer.remaining()];
			byteBuffer.get(encrypted);
			Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION_GCM);
			
			GCMParameterSpec paramSpec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
			cipher.init(Cipher.DECRYPT_MODE, secretKey, paramSpec);
			return cipher.doFinal(encrypted);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
			throw new FailedDecryptionException(e);
		}
	}
	
	private static byte[] concatGCM(byte[] iv, byte[] encryptedText) {

		ByteBuffer byteBuffer = ByteBuffer.allocate(1 + iv.length + encryptedText.length);
		byteBuffer.put((byte) iv.length);
		byteBuffer.put(iv);
		byteBuffer.put(encryptedText);

		return byteBuffer.array();
	}
}
