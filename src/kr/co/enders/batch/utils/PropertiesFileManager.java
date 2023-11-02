package kr.co.enders.batch.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Properties;

import org.apache.commons.codec.binary.Base64;

import kr.co.enders.batch.crypto.seed.Encryption;

public class PropertiesFileManager {

	public static void modProp(String[] args) throws Exception {
		String actionType = args[0];							// 행위정의 (C: 생성, R:읽기, U:수정, D:삭제)
		String propertiesPath = args[1];						// 프로퍼티 파일 풀경로
		String key = args[2];										// 행위를 할 key
		String value = args[3];									// 수정 및 생성할 value
		String codeYn = args[4];								// 암호화 여부(Y: 암호화 , N: 그대로 저장)
		String codeType = null;									// 암호화 알고리즘(BASE64, SEED)
		if (args.length > 5) {
			codeType = args[5];
		}
		
		String returnMsg = "";
		String propertiesVal = "";
		Properties props = null;
		
		try {
			// 필수 파라메터 
			if(!"".equals(StringUtil.nullToSpace(actionType))
					&& !"".equals(StringUtil.nullToSpace(propertiesPath))
					&& !"".equals(StringUtil.nullToSpace(key))
					&& !"".equals(StringUtil.nullToSpace(codeYn))){
				
				props = new Properties();
				
				props.load(new FileInputStream(propertiesPath));
				
				actionType = actionType.toUpperCase();
				codeYn = codeYn.toUpperCase();
				
				// 프로퍼티 값 생성
				if("C".equals(actionType)){
					// 암호화 여부가 Y 로 들어올경우 value 를 암호화 해서 저장
					if("Y".equals(codeYn)){
						if ("BASE64".equals(codeType)) {
							value = Base64.encodeBase64String(value.getBytes());
						} else if ("AES".equals(codeType)) {
							value = Encryption.encodeMessage(value);	
						}
					}
					
					props.setProperty(key, value);
					
					returnMsg = "프로퍼티 추가 ";
					
				}
				// 프로퍼티 값 읽기
				else if("R".equals(actionType)){
					propertiesVal = props.getProperty(key);
					
					// 암호화 여부가 Y 로 들어올경우 암호화 된 값을 복호화 해서 조회해준다.
					if("Y".equals(codeYn)){
						propertiesVal = Encryption.decodeMessage(propertiesVal);
					}
					
				}
				// 프로퍼티 값 수정 (기존 값을 삭제하고 새로 생성)
				else if("U".equals(actionType)){
					
					// 기존 값 삭제
					props.remove(key);
					
					// 암호화 여부가 Y 로 들어올경우 value 를 암호화 해서 저장
					if("Y".equals(codeYn)){
						if ("BASE64".equals(codeType)) {
							value = Base64.encodeBase64String(value.getBytes());
						} else if ("SEED".equals(codeType)) {
							value = Encryption.encodeMessage(value);	
						}
					}
					
					props.setProperty(key, value);
					
					returnMsg = "프로퍼티 수정 ";
				}
				// 프로퍼티 값 삭제
				else if("D".equals(actionType)){
					
					props.remove(key);
					
					returnMsg = "프로퍼티 삭제 ";
				}
				
				// 프로퍼티 파일 저장
				props.store(new FileOutputStream(propertiesPath), "properties save success~!!");
				
				if("R".equals(actionType)){
					System.out.println("조회 값 ::: "+key +" = "+ propertiesVal);
				}
				else{
					System.out.println(propertiesPath + " 파일 수정 성공!!");
					System.out.println(returnMsg + key +" = "+ value );
				}
				
			}
			else{
				System.err.println("파일을 수정할 파라메터 수가 모자랍니다.");
				System.err.println("actionType : ["+actionType+"]");
				System.err.println("propertiesPath : ["+propertiesPath+"]");
				System.err.println("key : ["+key+"]");
				System.err.println("value : ["+value+"]");
				System.err.println("codeYn : ["+codeYn+"]");
			}
			
		}
		catch (FileNotFoundException fe) {
			System.err.println(propertiesPath + " 파일을 찾을 수 없습니다.");
			throw fe;
		}
		catch (Exception e) {
			throw e;
		}
		finally{
			props = null;	
		}
	}
	
	public static void main(String[] args) throws Exception{
//		args = new String[6];
//		args[0] = "C";
//		args[1] = "C:\\Development\\workspace\\IAS_BATCH\\src\\account.properties";
//		args[2] = "SWAT_PW";
//		args[3] = "tmdhkt123!";
//		args[4] = "Y";
//		args[5] = "BASE64";
		modProp(args);
	}
}
