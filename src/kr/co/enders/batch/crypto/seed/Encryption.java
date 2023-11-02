package kr.co.enders.batch.crypto.seed;

import java.io.UnsupportedEncodingException;

public class Encryption {
	
	private static final String key = "gritis_sec_saracco";

	public static String encodeMessage(String message) throws UnsupportedEncodingException {
		SeedCipher seed = new SeedCipher();
		String encryptText = Base64.encode(seed.encrypt(message, key.getBytes(), "UTF-8"));
		
		return encryptText;
	}

	public static String decodeMessage(String message) throws UnsupportedEncodingException {
		SeedCipher seed = new SeedCipher();
		
		byte[] encryptbytes = Base64.decode(message);
		String decryptText = seed.decryptAsString(encryptbytes, key.getBytes(), "UTF-8");
		return decryptText;
	}
	
	public static boolean isMatch(String message, String enCryptedMessage) throws UnsupportedEncodingException{
		SeedCipher seed = new SeedCipher();
		
		byte[] encryptbytes = Base64.decode(enCryptedMessage);
		String decryptText = seed.decryptAsString(encryptbytes, key.getBytes(), "UTF-8");
		
		return message.equals(decryptText);
	}
}
