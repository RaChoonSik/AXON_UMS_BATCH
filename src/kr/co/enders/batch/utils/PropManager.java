package kr.co.enders.batch.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * =======================================================
 * 설명 : property파일을 읽어들이는 클래스
 *        
 *        사용방법
 *        1. 아래와 같이, 사용할 프로퍼티명을 나열
 *           readProperty("config.properties", "sumlog_config.properties");
 *           
 *        2. 사용할 프로퍼티명을 String[]에 담아 readProperty()을 호출
 * 
 * ※ config.properties는 기본 프로퍼티로써 필수
 * ※ 지정한 파일이 존재하지 않을 경우, 예외가 발생함
 * ※ 예외처리는 스택트레이스만 출력 (로그 출력안함)
 *     -> 이후 처리에서 프로퍼티 정보 취득에 실패하는 형태로 에러가 발생할 것으로 예상
 *     
 * 호환성 : 문법상 Java 1.5이상 필수
 * =======================================================
 * < history >
 */
public class PropManager {
	
	private static final Logger	   logger  = Logger.getLogger("DAILY");
	
	private static final Map<String,Properties> propMap = new HashMap<String, Properties>();

	// 디폴트 프로퍼티 파일을 읽어들인다.
	static {
		readProperty("config.properties","dbConfig.properties","account.properties");
	}
	
	/**
	 * 지정한 프로퍼티 파일을 읽어들인다.
	 * @param propNames
	 */
	protected static void readProperty(String... propNames) {
		Properties prop = null;
		ClassLoader cl = null;
		try {
			cl = Thread.currentThread().getContextClassLoader();
			if (cl == null) {
				cl = ClassLoader.getSystemClassLoader();
			}
			for (String propName : propNames) {
				prop = new Properties();
				URL url = cl.getResource(propName);
				logger.info("  속성정보 위치 " + url + "]" );
				
				if (url == null) {
					continue; // 지정한 프로퍼티가 존재하지 않을 경우 무시
				}
				prop.load(url.openStream());
				propMap.put(propName, prop);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 기본환경설정파일로 부터, key에 해당하는 String value를 반환
	 * ※ 기본환경설정파일 : config.properties를 검색
	 * @param key
	 * @return value
	 */
	public static String getStrValue(String key) {
		return propMap.get("config.properties").getProperty(key);
	}
	/**
	 * 기본환경설정파일로 부터, key에 해당하는 integer value를 반환
	 * ※ 기본환경설정파일 : config.properties를 검색
	 * @param key
	 * @return value (value가 null일 경우 0을 반환)
	 */
	public static int getIntValue(String key) {
		String value = getStrValue(key);
		if (value != null && !value.equals("")) {
			return Integer.parseInt(value);
		}
		return 0;
	}
	/**
	 * 지정한 프로퍼티로 부터, key에 해당하는 String value를 반환
	 * @param propName
	 * @param key
	 * @return value
	 */
	public static String getStrValue(String propName, String key) {
		return propMap.get(propName).getProperty(key);
	}
	/**
	 * 지정한 프로퍼티로 부터, key에 해당하는 integer value를 반환
	 * @param propName
	 * @param key
	 * @return value (value가 null일 경우 0을 반환)
	 */
	public static int getIntValue(String propName, String key) {
		String value = getStrValue(propName, key);
		if (value != null && !"".equals(value)) {
			return Integer.parseInt(value);
		}
		return 0;
	}
	/**
	 * 지정한 프로퍼티로 부터, key에 해당하는 String value를 반환, 값이 없을경우 default 반환
	 * @param propName
	 * @param key
	 * @param defaultVal
	 * @return value
	 */
	public static String getStrValue(String propName, String key, String defaultVal) {
		String returnVal = getStrValue(propName, key);
		if("".equals(StringUtil.nullToSpace(returnVal))){
			returnVal = defaultVal;
		}
		return returnVal;
	}
	/**
	 * 지정한 프로퍼티로 부터, key에 해당하는 String value를 반환, 값이 없을경우 default 반환
	 * @param propName
	 * @param key
	 * @param defaultVal
	 * @return value
	 */
	public static int getIntValue(String propName, String key, int defaultVal) {
		String tempVal = getStrValue(propName, key);
		if("".equals(StringUtil.nullToSpace(tempVal))){
			return defaultVal;
		}
		return Integer.parseInt(tempVal);
	}
	/**
	 * DB설정용 프로퍼티로 부터, key에 해당하는 String value를 반환
	 * @param key
	 * @return value
	 */
	public static String getDBStrValue(String key) {
		return propMap.get("dbConfig.properties").getProperty(key);
	}
	/**
	 * DB설정용 프로퍼티로 부터, key에 해당하는 String value를 반환
	 * @param key
	 * @param defaultVal
	 * @return value
	 */
	public static String getDBStrValue(String key, String defaultVal) {
		return getStrValue("dbConfig.properties", key, defaultVal);
	}
	/**
	 * 키 정보용프로티
	 * @param key
	 * @return
	 */
	public static String getAccStr(String key) {
		return getStrValue("account.properties", key);
	}
	
	/////////////////////////////////////////-- 이하 디버그용 코드 --/////////////////////////////////////////
	/**
	 * UT용 메인메서드
	 * @param args 
	 */
	public static void main(String[] args) {
		System.out.println(PropManager.getStrValue("MSSQL_DRV_NAME"));
		System.out.println(PropManager.getStrValue("config.properties", "MSSQL_DRV_NAME"));
		System.out.println(PropManager.getIntValue("config.properties", "AAA"));
		System.out.println(PropManager.getStrValue("config.properties", "KOREAN_TEST"));
	}
}
