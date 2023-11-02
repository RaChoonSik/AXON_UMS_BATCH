package kr.co.enders.batch.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;

import org.apache.log4j.Logger;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.salt.StringFixedSaltGenerator;

public class EncryptUtil {
    private static Logger logger = Logger.getLogger(EncryptUtil.class);
    
    /**
     * 문자열을 SHA256으로 암호화(해싱)한다.
     * @param str
     * @return
     */
    public static String getEncryptedSHA256(String str) {
        String result = "";
        if(str == null) {
            return "";
        } else {
            try {
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                digest.reset();
                digest.update(str.getBytes());
                byte[] hash = digest.digest();
                StringBuffer sb = new StringBuffer();
                for (int i = 0; i < hash.length; i++) {
                    //sb.append(Integer.toString((hash[i]&0xff), 16).substring(1));
                    sb.append(Integer.toString((hash[i]&0xff)+0x100, 16).substring(1));
                }
                result = sb.toString();
            } catch (NoSuchAlgorithmException nsae) {
                result = str;
            }
            return result;
        }
    }
    
    /**
     * 문자열을 SHA256으로 암호화(해싱)한다.
     * @param str
     * @return
     */
    public static String getEncrypted32BitSHA256(String str) {
        String result = "";
        if(str == null) {
            return "";
        } else {
            try {
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                digest.reset();
                digest.update(str.getBytes());
                byte[] hash = digest.digest();
                StringBuffer sb = new StringBuffer();
                String strHash = "";
                for (int i = 0; i < hash.length; i++) {
                    strHash = Integer.toString((hash[i]&0xff), 16); 
                    if( strHash.length() == 1 ) {
                        sb.append( strHash );
                    }
                    else {
                        sb.append( strHash.substring(1));
                    }
                }
                result = sb.toString();
            } catch (NoSuchAlgorithmException nsae) {
                result = str;
            }
            return result;
        }
    }
    
    /**
     * 문자열을 Jasypt library로 암호화한다.
     * @param algorithm
     * @param password
     * @param str
     * @return
     */
    public static String getJasyptEncryptedString(String algorithm, String password, String str) {
        try {
            StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
            encryptor.setAlgorithm(algorithm);
            encryptor.setPassword(password);
            encryptor.setSaltGenerator(new StringFixedSaltGenerator(password));
            return encryptor.encrypt(str);
        } catch(Exception e) {
            logger.error("getJasyptEncryptedString error = " + e);
            return str;
        }
    }

    /**
     * 문자열을 Jasypt library로 복호화한다.
     * @param algorithm
     * @param password
     * @param str
     * @return
     */
    public static String getJasyptDecryptedString(String algorithm, String password, String str) {
        try {
            StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
            encryptor.setAlgorithm(algorithm);
            encryptor.setPassword(password);
            encryptor.setSaltGenerator(new StringFixedSaltGenerator(password));
            return encryptor.decrypt(str);
        } catch(Exception e) {
            logger.error("getJasyptDecryptedString error = " + e + "[str]" + str + "[password]" + password);
            return str;
        }
    }
    
    /**
     * 문자열을 Jasypt library로 암호화한다.
     * @param algorithm
     * @param password
     * @param str
     * @return
     */
    public static String getJasyptEncryptedUnFixString(String algorithm, String password, String str) {
        try {
            StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
            encryptor.setAlgorithm(algorithm);
            encryptor.setPassword(password);
            return encryptor.encrypt(str);
        } catch(Exception e) {
            logger.error("getJasyptEncryptedUnFixString error = " + e + "[str]" + str + "[password]" + password);
            return str;
        }
    }
    
    /**
     * 문자열을 Jasypt library로 복호화한다.
     * @param algorithm
     * @param password
     * @param str
     * @return
     */
    public static String getJasyptDecryptedUnFixString(String algorithm, String password, String str) {
        try {
            StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
            encryptor.setAlgorithm(algorithm);
            encryptor.setPassword(password);            
            return encryptor.decrypt(str);
        } catch(Exception e) {
            logger.error("getJasyptDecryptedUnFixString error = " + e + "[str]" + str + "[password]" + password);
            return str;
        }
    }
    
    /**
     * 문자열을 Base64로 인코딩한다.
     * @param str
     * @return
     */
    public static String getBase64EncodedString(String str) {
        try {
            Encoder encoder = Base64.getEncoder();
            return new String(encoder.encode(str.getBytes()));
        } catch(Exception e) {
            logger.error("getBase64EncodedString Error = " + e.getMessage() + "[str]" + str);
            return str;
        }
    }
    
    /**
     * 문자열을 Base64로 디코딩한다.
     * @param str
     * @return
     */
    public static String getBase64DecodedString(String str) {
        try {
            Decoder decoder = Base64.getDecoder();
            return new String(decoder.decode(str.getBytes()));
        } catch(Exception e) {
            logger.error("getBase64DecodedString Error = " + e.getMessage() + "[str]" + str);
            return str;    
        }
    }

    
	public static void main(String args[]) throws Exception  {
        
		String ALGORITHM = "PBEWithMD5AndDES";
		//String KEYSTRING = "ENDERSUMS";
		String KEYSTRING = "!END#ERSUMS";
		 
		//String KEYSTRING = "NOT_RNNO";
		
		EncryptUtil enc =  new EncryptUtil();
		String enc_data1 = enc.getJasyptEncryptedUnFixString(ALGORITHM, KEYSTRING, "jdbc:oracle:thin:@127.0.0.1:1521:xe");
		String enc_data2 = enc.getJasyptEncryptedUnFixString(ALGORITHM, KEYSTRING, "ums");
		String enc_data3 = enc.getJasyptEncryptedUnFixString(ALGORITHM, KEYSTRING, "enders1!");
		String enc_data33 = enc.getJasyptEncryptedString(ALGORITHM, KEYSTRING, "enders1!");
		
		String enc_data4 = enc.getJasyptEncryptedUnFixString(ALGORITHM, KEYSTRING, "hun1110@enders.co.kr");
		String enc_data5 = enc.getJasyptEncryptedUnFixString(ALGORITHM, KEYSTRING, "hun1110@hanmail.net");
		String enc_data6 = enc.getJasyptEncryptedUnFixString(ALGORITHM, KEYSTRING, "hun1010616@naver.com");
		String enc_data7 = enc.getJasyptEncryptedUnFixString(ALGORITHM, KEYSTRING, "hun1010616@nate.com");
		String enc_data8 = enc.getJasyptEncryptedUnFixString(ALGORITHM, KEYSTRING, "enders1!@");
		String enc_data9 = enc.getJasyptEncryptedString(ALGORITHM, KEYSTRING, "enders1!@");
		
		System.out.println("enc_data1 : " +enc_data1);
		System.out.println("enc_data2 : " +enc_data2);
		System.out.println("enc_data3 : " +enc_data3);
		System.out.println("enc_data33 : " +enc_data33);
		
		System.out.println("enc_data4 : " +enc_data4);
		System.out.println("enc_data5 : " +enc_data5);
		System.out.println("enc_data6 : " +enc_data6);
		System.out.println("enc_data7 : " +enc_data7);
		System.out.println("enc_data8 : " +enc_data8);
		System.out.println("enc_data9 : " +enc_data9);
		System.out.println("");
		
		String dec_data1 = enc.getJasyptDecryptedUnFixString(ALGORITHM, KEYSTRING, enc_data1);
		String dec_data2 = enc.getJasyptDecryptedUnFixString(ALGORITHM, KEYSTRING, enc_data2);
		String dec_data3 = enc.getJasyptDecryptedUnFixString(ALGORITHM, KEYSTRING, enc_data3);
		String dec_data33 = enc.getJasyptDecryptedString(ALGORITHM, KEYSTRING, enc_data33);
		
		String dec_data4 = enc.getJasyptDecryptedUnFixString(ALGORITHM, KEYSTRING, enc_data4);
		String dec_data5 = enc.getJasyptDecryptedUnFixString(ALGORITHM, KEYSTRING, enc_data5);
		String dec_data6 = enc.getJasyptDecryptedUnFixString(ALGORITHM, KEYSTRING, enc_data6);
		String dec_data7 = enc.getJasyptDecryptedUnFixString(ALGORITHM, KEYSTRING, enc_data7);
		String dec_data8 = enc.getJasyptDecryptedUnFixString(ALGORITHM, KEYSTRING, "835d6dc88b708bc646d6db82c853ef4182fabbd4a8de59c213f2b5ab3ae7d9be");
		String dec_data9 = enc.getJasyptDecryptedString(ALGORITHM, KEYSTRING, "65b9eb843c160160c9b5bbef82d9af774c5cfac66f1c09251a65d1d1855fac11");
		

		System.out.println("dec_data1 : " +dec_data1);
		System.out.println("dec_data2 : " +dec_data2);
		System.out.println("dec_data3 : " +dec_data3);
		System.out.println("dec_data33 : " +dec_data33);
		System.out.println("dec_data4 : " +dec_data4);
		System.out.println("dec_data5 : " +dec_data5);
		System.out.println("dec_data6 : " +dec_data6);
		System.out.println("dec_data7 : " +dec_data7);
		System.out.println("dec_data8 : " +dec_data8);
		System.out.println("dec_data9 : " +dec_data9);
		
	}
}
