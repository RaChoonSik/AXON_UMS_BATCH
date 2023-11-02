package kr.co.enders.batch.crypto.seed;

import java.io.IOException;

import org.apache.commons.codec.binary.Base64;

public class CodeGenerator {

	public static void main(String[] args)   throws IOException {
		try {
			String param = "xcosmos";
			generateCode(param);
			
			param = "saruser";
			generateCode(param);
			
			param = "1234qwer!";
			generateCode(param);
			
			param = "8nC5xPaJCNRnDmC9FkZCCg==";
			DeCode(param);
			
			genBase64("saruser");
			genBase64("1234qwer!");
			decodeBase64("d2NhbmE=");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void generateCode(String param) throws Exception {
		try {
			String sEncodeString = "";
			sEncodeString = Encryption.encodeMessage(param);
			System.out.println("sEncodeString : " + sEncodeString);
		} catch (Exception e) {
			throw e;
		}
	}
	
	private static void DeCode(String param) throws Exception {
		try {
			String sEncodeString = "";
			sEncodeString = Encryption.decodeMessage(param);
			System.out.println("DeCode : " + sEncodeString);
		} catch (Exception e) {
			throw e;
		}
	}

	private static void genBase64(String param) throws Exception {
		try {
			System.out.println(Base64.encodeBase64String(param.getBytes()));
		} catch (Exception e) {
			throw e;
		}
	}
	
	private static void decodeBase64(String param) throws Exception {
		try {
			System.out.println(new String(Base64.decodeBase64(param)));
		} catch (Exception e) {
			throw e;
		}
	}
	
}
