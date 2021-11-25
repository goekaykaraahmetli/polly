package com.polly.utils.encryption.ciphers;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;


import com.polly.utils.encryption.exceptions.FailedDecryptionException;
import com.polly.utils.encryption.exceptions.FailedEncryptionException;

public class RSACipher {
	public static final String RSA_TRANSFORMATION = "RSA/ECB/PKCS1Padding";
	
	private RSACipher() {
		super();
	}
	
	public static byte[] encrypt(byte[] input, PublicKey publicKey) throws FailedEncryptionException {
		try {
			Cipher cipher = Cipher.getInstance(RSA_TRANSFORMATION);
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			return cipher.doFinal(input);
		} catch(NoSuchAlgorithmException|NoSuchPaddingException|InvalidKeyException|IllegalBlockSizeException|BadPaddingException e) {
			throw new FailedEncryptionException();
		}
	}
	
	public static byte[] decrypt(byte[] input, PrivateKey privateKey) throws FailedDecryptionException {
		try {
			Cipher cipher = Cipher.getInstance(RSA_TRANSFORMATION);
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			return cipher.doFinal(input);
		} catch(NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
			throw new FailedDecryptionException(e);
		}
		
	}
}