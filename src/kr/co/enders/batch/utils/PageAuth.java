package kr.co.enders.batch.utils;


import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

public class PageAuth {
    private static Logger logger = Logger.getLogger(PageAuth.class);
    
    // 복호화
    public static String decryptData(String dataSpec, String decKey){
        dataSpec = StringUtils.replaceChars(dataSpec, '-', '+');
        dataSpec = StringUtils.replaceChars(dataSpec, '_', '/');
        String authJson = "";
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec keySpec = new SecretKeySpec(decKey.getBytes(), "AES");
            IvParameterSpec ivParamSpec = new IvParameterSpec(decKey.substring(0, 16).getBytes());
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivParamSpec);
            
            byte[] decodedBytes = Base64.getDecoder().decode(dataSpec);
            byte[] decrypted = cipher.doFinal(decodedBytes);
            authJson = new String(decrypted, StandardCharsets.UTF_8);
            
        }catch (Exception e) {
            logger.debug( "error : " +  e );
        }
        return authJson;
    }
    
    // 암호화
    public static String ecryptData(String dataSpec, String decKey) {
        String authJson = "";
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec keySpec = new SecretKeySpec(decKey.getBytes(), "AES");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(decKey.substring(0, 16).getBytes()));
            byte[] encrypted = cipher.doFinal(dataSpec.getBytes());
            authJson = new String(Base64.getEncoder().encode(encrypted));
            authJson = StringUtils.replaceChars(authJson, '+', '-');
            authJson = StringUtils.replaceChars(authJson, '/', '_');
        }catch (Exception e) {
            logger.debug( "error : " +  e );
        }
        return authJson;
    }
}