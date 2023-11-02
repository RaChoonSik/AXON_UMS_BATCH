package kr.co.enders.batch.utils;

import java.io.File;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.FastDateFormat;

public class StringUtil {
	
	public static String nullToSpace(String str)
	{
		if(str == null || "".equals(str) || "null".equals(str))
			return "";
		
		return str;
	}
		
	public static String nullToSpaceTrim(String str)
	{
		if(null != str && !"".equals(str)){
			if("".equals(str.trim()))
				return "";
			else 
				return str;
		}
		else if("null".equals(str)){
			return "";
		}
		else
			return "";	
	}
	
	public static String nullToSpace(String str, int strLength)
	{
		
		if(str == null || "".equals(nullToSpaceTrim(str)))		// 값이 없을 경우 공백처리 
		{
			str = "";
			
			for (int i = 0; i < strLength; i++) {
				str += " ";
			}
		}
		else if(str.length() < strLength)		// 넘겨줄 자리가 부족할 경우 공백처리
		{
			int spaceLength = strLength - str.length(); 
			
			for (int i = 0; i < spaceLength; i++) {
				str += " ";
			}
		}

		return str;
	}
	
	public static String nullToSpace(String str, String defaultStr)
	{
		
		if("".equals(nullToSpace(str)))
			return defaultStr;
		else
			return str;
	}
		
	// 현재 시간을 패턴별로 리턴
	public static String formatDateTimeToString(String pattern) {
		
		if(!StringUtils.isEmpty(pattern)){
			Date date = new Date(System.currentTimeMillis());
			FastDateFormat format = FastDateFormat.getInstance(pattern, TimeZone.getDefault(), Locale.getDefault());
			return format.format(date);
		}
		else
			return "";
	}
	
	/**
	 * NULL 혹은 공백인지 확인
	 * @param str
	 * @return null 혹은 ""일 경우 true
	 */
	public static boolean isNullOrSpace(String str) {
		if (str == null) {
			return true;
		}
		return str.isEmpty();
	}
	
	/**
	 * 숫자형 자리수를 맞출때 사용.
	 * @param num
	 * @param size : 맞춰야할 자리수
	 * @return String
	 */
	public static String lpad(long num, int size) {
		String f = "%0"+size+"d";
		return String.format(f, num);  
	}
	
	/**
	 * str 오른쪽으로 공백을 채워 지정한 자릿수를 맞춘다.
	 * @param size
	 * @param str
	 * @return
	 */
	public static String rPadSpace(String str, int size) {
		if (str == null) str = "";
		String f = "%-"+ size + "s";
		return String.format(f, str);
	}
	
	/**
	 * str 왼쪽으로 공백을 채워 지정한 자릿수를 맞춘다.
	 * @param size
	 * @param str
	 * @return
	 */
	public static String lPadSpace(String str, int size) {
		if (str == null) str = "";
		String f = "%"+ size + "s";
		return String.format(f, str);
	}
	
	/**
	 * 숫자인지 아닌지 판단.(숫자로 형변환이 가능한 경우 true, 아닌경우 false)
	 * @param str
	 * @return boolean
	 */
	public static boolean isNumber(String str) {
		
		if("".equals(nullToSpace(str))) return false;
		
		for (int i = 0; i < str.length(); i++) {
			if(!Character.isDigit(str.charAt(i)))
				return false;
		}
		
		return true;  
	}
	
	/**
	 * Map<key, String> 의 전체 데이터를 문자열로 나열한다.
	 * 1. [,]등 구분자에 의한 구분 없음
	 * 2. 전문등 별도 구분자 없이 나열된 문자열 생성을 위해 사용
	 * 
	 * @param arg value가 String형식인 Map
	 * @return 문자열로 나열된 결과
	 */
	public static StringBuilder mapToString(Map<?, String> arg) {
		StringBuilder sb = new StringBuilder();
		Iterator<?> it = arg.keySet().iterator();
		while (it.hasNext()) {
			sb.append(arg.get(it.next()));
		}
		return sb;
	}
	
	public static StringBuilder mapToString(Map<?, String> arg, String rowDil, String itemDil) {
		StringBuilder sb = new StringBuilder();
		Iterator<?> it = arg.keySet().iterator();
		Object key = null;
		boolean next = false;
		while (it.hasNext()) {
			key =  it.next();
			if (next) { 
				sb.append(rowDil);
			}
			sb.append(key).append(itemDil).append(arg.get(key));
			next = true;
		}
		return sb;
	}
	
	/**
	 * 1차원 String 배열 정보를 Set<String>으로 변환 
	 * @param params
	 * @return
	 */
	public static Set<String> convArryToSet(String[] params) {
		Set<String> set = new HashSet<String>();
		for (String param : params) {
			set.add(param);
		}
		return set;
	}
	
	/**
	 * 1차원 File 배열 정보를 Set<File>으로 변환 
	 * @param params
	 * @return
	 */
	public static Set<File> convArryToSet(File[] params) {
		Set<File> set = new HashSet<File>();
		for (File param : params) {
			set.add(param);
		}
		return set;
	}
	
	public static void main(String[] args) {
		System.out.println("["+lPadSpace("F133B962-3462-4573-9D2F-20455527DA17", 40)+"]");
		System.out.println("["+lPadSpace(null, 40)+"]");
				
		System.out.println("   ".trim().isEmpty());
	}

	public static String stackTraceToString(Throwable e)
	{
		StringBuilder sb = new StringBuilder();
		for (StackTraceElement element : e.getStackTrace()) {
			sb.append(element.toString() + "\n");
		}
		return sb.toString();
	}

	static public String nvl(String src, String tgt)
	{
		String res = tgt;
		if (tgt == null) {
			res = "";
		}

		if (src == null) {
			return res;
		}
		else if (src.equals("")) {
			return res;
		}
		else {
			return src;
		}
	}
}
