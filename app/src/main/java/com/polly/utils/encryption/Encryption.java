package com.polly.utils.encryption;

import java.security.KeyPair;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.polly.utils.encryption.ciphers.AESCipher;
import com.polly.utils.encryption.ciphers.RSACipher;
import com.polly.utils.encryption.exceptions.FailedDecryptionException;
import com.polly.utils.encryption.exceptions.FailedEncryptionException;
import com.polly.utils.encryption.exceptions.FailedKeyGenerationException;
import com.polly.utils.encryption.utils.CipherKeyGenerator;

public class Encryption {
/**
	public static void main(String[] args) {
		try {
			KeyPair keyPair = CipherKeyGenerator.generateRSAKeyPair(8192);
			SecretKey secretKey = CipherKeyGenerator.generateAESSecretKey();
			
			
			
			
			String test = "%y�vG�=����?�2w���f8Op�p}��p?e�mS��U��X��ӕ1-JM�Y�g6�P��u�(��m$�l��D<�B�D��X&UfE���,��b���>_+s)h#���U�dn�1>�����>���\r��f�T��?b�S茫#8C������-sLH|:T{��ݘ�Ф����d8ؾ�ZT��i=ۣ΃��ƄG|�L��뽙�a^~o��v�qnd�ܵ�CEˑ����(X�[{�?���������������G�P$v7lmԎ?��@�R[";
			byte[] testBytes = test.getBytes();
			
			
			byte[] encryptedBytes = AESCipher.encrypt(testBytes, secretKey);
			
			byte[] encryptedKey = RSACipher.encrypt(secretKey.getEncoded(), keyPair.getPublic());
			
			byte[] decryptedKey = RSACipher.decrypt(encryptedKey, keyPair.getPrivate());
			
			SecretKey sk = new SecretKeySpec(decryptedKey, "AES");
			
			byte[] decryptedBytes = AESCipher.decrypt(encryptedBytes, sk);
			System.out.println(test);
			System.out.println(new String(decryptedBytes));
			
			System.out.println(test.equals(new String(decryptedBytes)));
			
			
		} catch (FailedKeyGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
 **/
}
