package kr.co.enders.batch.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ListUtil {
	/**
	 * 리스트 내 문자열에서 중복을 제거한다.
	 * @param param 중복 제거 할 리스트
	 */
	public static void distinctList(List<String> param) {
		Set<String> set = new HashSet<String>();
		for (String str : param) {
			set.add(str);
		}
		param.clear();
		for (String str : set) {
			param.add(str);
		}
	}
	
	/**
	 * 리스트 객체에서 중복을 제거한다. (문자열 OK)
	 * @param param 중복 제거 할 리스트
	 * @return 중복 제거된 정보를 배열형식으로 반환
	 */
	public static Object[] distinctListToArray(List<Object> param) {
		Set<Object> set = new HashSet<Object>();
		for (Object str : param) {
			set.add(str);
		}
		return set.toArray();
	}
	
	
	public static void main(String[] args) {
		System.out.println("start");
		List<String> aaa = new ArrayList<String>();
		aaa.add("010111122223333");
		aaa.add("010111122223334");
		aaa.add("010111122223334");
		aaa.add("010111122223331");
		aaa.add("010111122223333");
		aaa.add("010111122223332");
		distinctList(aaa);
		for (String string : aaa) {
			System.out.println(string);
		}
		
		System.out.println("=====================================");
		
		List<Object> ccc = new ArrayList<Object>();
		ccc.add("010111122223333");
		ccc.add("010111122223334");
		ccc.add("010111122223334");
		ccc.add("010111122223331");
		ccc.add("010111122223333");
		ccc.add("010111122223332");
		Object[] bbb = distinctListToArray(ccc);
		for (Object string : bbb) {
			System.out.println((String) string);
		}
		
		
	}
}
