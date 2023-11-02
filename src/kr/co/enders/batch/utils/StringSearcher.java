package kr.co.enders.batch.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * =======================================================
 * 설명 : 문자열 내에서 특정문자를 검색해 추출하는 유틸
 * =======================================================
 */
public class StringSearcher {
	/**
	 * ============================================================
	 * 문자열 내에서 특정문자를 검색해 추출해 내는 기능
	 * ============================================================
	 * 주로 <></>같은 태그형식내의 문자열이나 태그의 속성값을 추출하는데 사용가능하다.
	 * 같은 형식의 태그가 복수개 존재하는 경우 모두 추출 가능하며 ","로 구분한다.
	 * 
	 * ex) "<text>abcde</text>"에서 태그내 문자열을 추출하고 싶은 경우
	 *     searchStringValues("<text>abcde</text>", "<text>","</text>",0, rtnStr);
	 *     rtnStr.toString();
	 * 
	 * ※ 검색횟수에 제한없음
	 * @param str 문자열 원본
	 * @param startSrchExp 검색시작 문자열 (혹은 여는태그)
	 * @param endSrchExp 검색종료 문자열 (혹은 닫는태그)
	 * @param startIdx 검색 시작인덱스
	 * @param rtnStr 검색된 문자열(들)
	 */
	public static String searchStringValues(String str, String startSrchExp, String endSrchExp, int startIdx) {	
		return searchStringValues(str, startSrchExp, endSrchExp, startIdx, 0);
	}
	/**
	 * ============================================================
	 * 문자열 내에서 특정문자를 검색해 추출해 내는 기능
	 * ============================================================
	 * 주로 <></>같은 태그형식내의 문자열이나 태그의 속성값을 추출하는데 사용가능하다.
	 * 같은 형식의 태그가 복수개 존재하는 경우 모두 추출 가능하며 ","로 구분한다.
	 * 
	 * ex) "<text>abcde</text>"에서 태그내 문자열을 추출하고 싶은 경우
	 *     searchStringValues("<text>abcde</text>", "<text>","</text>",0, rtnStr);
	 *     rtnStr.toString();
	 * 
	 * @param str 문자열 원본
	 * @param startSrchExp 검색시작 문자열 (혹은 여는태그)
	 * @param endSrchExp 검색종료 문자열 (혹은 닫는태그)
	 * @param startIdx 검색 시작인덱스
	 * @param rtnStr 검색된 문자열(들)
	 * @param seekCnt 검색횟수 제한 (0은 제한없음)
	 */
	public static String searchStringValues(String str, String startSrchExp, String endSrchExp, int startIdx, int seekCnt) {	
		StringBuilder sb = new StringBuilder();
		List<String> rtnStr = new ArrayList<String>();
		searchStringValues(str, startSrchExp, endSrchExp, startIdx, rtnStr, seekCnt, 1);
		int count = rtnStr.size();
		for (int i = 0; i < count; i++) {
			sb.append(rtnStr.get(i));
			if (i < (count - 1)) {
				sb.append(",");
			}
		}
		return sb.toString();
	}
	
	/**
	 * ============================================================
	 * 문자열 내에서 특정문자를 검색해 추출해 내는 기능 (재귀)
	 * ============================================================
	 * 주로 <></>같은 태그형식내의 문자열이나 태그의 속성값을 추출하는데 사용가능하다.
	 * 같은 형식의 태그가 복수개 존재하는 경우 모두 추출 가능.
	 * 
	 * ex) "<text>abcde</text>"에서 태그내 문자열을 추출하고 싶은 경우
	 *     searchStringValues("<text>abcde</text>", "<text>","</text>",0, rtnStr);
	 *     
	 * ※ 예외처리
	 *    1. 여는 태그가 있지만, 닫는 태그가 없을 경우, 그 시점에서 처리를 중단한다.
	 *       -> 그 이전에 처리된 데이터는 리턴한다.
	 *    2. 해당문자열이 2회이상 반복되며, 중간에 닫는 태그가 없는 경우(<A>abc<B>qqq</B><A>123</A>),
	 *       의도하지 않은 값을 리턴할 수 있다.
	 *       (여는태그, 닫는태그가 철저하게 들어있다는 전제하에 사용할 것)
	 * @param str 문자열 원본
	 * @param startSrchExp 검색시작 문자열 (혹은 여는태그)
	 * @param endSrchExp 검색종료 문자열 (혹은 닫는태그)
	 * @param startIdx 검색 시작인덱스
	 * @param rtnStr 검색된 문자열(들)
	 * TODO 아래 seekCnt가 들어간 처리 하나만 남기고 이건 삭제하자
	 */
	public static void searchStringValues(String str, String startSrchExp, String endSrchExp, int startIdx, List<String> rtnStr) {	
		if (startIdx > 0) {
			startIdx = str.indexOf(startSrchExp, startIdx);
		} else {
			startIdx = str.indexOf(startSrchExp);
			if (startIdx < 0) {
				return;
			}
		}
		int endIdx = str.indexOf(endSrchExp, startIdx);
		if (endIdx < 0) {
			return;                                // 닫는 태그가 없는 경우, 이 시점에서 처리를 종료한다.
		}
		String content = str.substring(startIdx + startSrchExp.length(), endIdx);
		if (!rtnStr.contains(content)) {             // 중복체크
			rtnStr.add(content);
		}
		if (str.indexOf(startSrchExp, endIdx) > 0) {
			searchStringValues(str, startSrchExp, endSrchExp, startIdx + startSrchExp.length(), rtnStr);
		}
	}
	
	/**
	 * ============================================================
	 * 문자열 내에서 특정문자를 검색해 추출해 내는 기능 (재귀)
	 * ============================================================
	 * 주로 <></>같은 태그형식내의 문자열이나 태그의 속성값을 추출하는데 사용가능하다.
	 * 같은 형식의 태그가 복수개 존재하는 경우 모두 추출 가능.
	 * 
	 * ex) "<text>abcde</text>"에서 태그내 문자열을 추출하고 싶은 경우
	 *     searchStringValues("<text>abcde</text>", "<text>","</text>",0, rtnStr);
	 *     
	 * ※ 예외처리
	 *    1. 여는 태그가 있지만, 닫는 태그가 없을 경우, 그 시점에서 처리를 중단한다.
	 *       -> 그 이전에 처리된 데이터는 리턴한다.
	 *    2. 해당문자열이 2회이상 반복되며, 중간에 닫는 태그가 없는 경우(<A>abc<B>qqq</B><A>123</A>),
	 *       의도하지 않은 값을 리턴할 수 있다.
	 *       (여는태그, 닫는태그가 철저하게 들어있다는 전제하에 사용할 것)
	 * @param str 문자열 원본
	 * @param startSrchExp 검색시작 문자열 (혹은 여는태그)
	 * @param endSrchExp 검색종료 문자열 (혹은 닫는태그)
	 * @param startIdx 검색 시작인덱스
	 * @param rtnStr 검색된 문자열(들)
	 * @param seekCnt 검색횟수 제한 (0은 제한없음)
	 * @param currentCnt 현재 검색횟수
	 */
	public static void searchStringValues(String str, String startSrchExp, String endSrchExp, int startIdx, List<String> rtnStr, int seekCnt, int currentCnt) {	
		if (startIdx > 0) {
			startIdx = str.indexOf(startSrchExp, startIdx);
		} else {
			startIdx = str.indexOf(startSrchExp);
			if (startIdx < 0) {
				return;
			}
		}
		int endIdx = str.indexOf(endSrchExp, startIdx);
		if (endIdx < 0) {
			return;                                // 닫는 태그가 없는 경우, 이 시점에서 처리를 종료한다.
		}
		String content = str.substring(startIdx + startSrchExp.length(), endIdx);
		if (!rtnStr.contains(content)) {             // 중복체크
			rtnStr.add(content);
		}
		if (str.indexOf(startSrchExp, endIdx) > 0 && (0 == seekCnt || currentCnt < seekCnt)) {
			searchStringValues(str, startSrchExp, endSrchExp, startIdx + startSrchExp.length(), rtnStr, seekCnt, ++currentCnt);
		}
	}
	/**
	 * ============================================================
	 * 반복 문자가 존재하는 문자열 내에서 특정 반복문자열의 시작부분과 끝부분을 검출하여 그 안의 문자열을 추출하는 기능
	 * ============================================================
	 * [M/I/9999/0/S],1,[M/I/9200/2/S],2,,9876543,[M/M/9210/4/S],1234567 이라는 문자열에서 "[M/"을 이용해 정보를 추출할 경우
	 * 
	 * 아래의 데이터가 리스트에 담겨 반환된다.
	 * [M/I/9999/0/S],1,
	 * [M/I/9200/2/S],2,,9876543,
	 * [M/M/9210/4/S],1234567
	 * 
	 * @param str 문자열
	 * @param srchExp 반복문자열 (추출기준)
	 * @return 추출된 문자열의 리스트
	 */
	public static List<String> searchStrings(String str, String srchExp) {
		List<String> menuInfoList = new ArrayList<String>();
		int startIdx = 0;
		int endIdx = 0;
		while (startIdx > -1) {
			if (startIdx > 0) {
				endIdx = str.indexOf(srchExp, startIdx + 1) - 1;
				if (endIdx < 0) {
					endIdx = str.length() - 1;
				}
				menuInfoList.add(str.substring(startIdx, endIdx));
			}
			startIdx = str.indexOf(srchExp, endIdx);
		}
		return menuInfoList;
	}
	
	public static void subStringGetList(List<String> lResult, String psSource, String psSearchStr, String psSearchEnd, Integer nSubStart){
		String strSub = "";
		
		Integer nStart = psSource.indexOf(psSearchStr);
		if(nStart < 0) {
			return;
		}
		String strAll = psSource.substring(nStart, psSource.length());
		Integer nEnd = strAll.indexOf(psSearchEnd, 1);
		if(nEnd > 0) {
			strSub = strAll.substring(nSubStart, nEnd);
			lResult.add(strSub);
			strAll = strAll.substring(nEnd, strAll.length());
			subStringGetList(lResult, strAll, psSearchStr, psSearchEnd, nSubStart);
		}else {
			strSub = strAll.substring(nSubStart, strAll.length());
			lResult.add(strSub);
			return;
		}
	}
	
	public static String subStringGetString(String psSource, String psSearchStr, String psSearchEnd, Integer nSubStart){
		String strSub = "";
		String strResult = "";
		
		Integer nStart = psSource.indexOf(psSearchStr);
		if(nStart < 0) {
			return strResult;
		}
		String strAll = psSource.substring(nStart, psSource.length());
		Integer nEnd = strAll.indexOf(psSearchEnd, 1);
		
		strSub = strAll.substring(nSubStart, nEnd);
		strResult = strSub;
		return strResult;
	}

	/////////////////////////////////////////-- 이하 디버그용 코드 --/////////////////////////////////////////
	/**
	 * UT용 메인메서드
	 * @param args 
	 */
	public static void main(String[] args) {
		
		String str = "";
		//searchStrings(str, "[M/");
		List<String> lDetail = new ArrayList<String>();
		
		subStringGetList(lDetail, str, "[M", "[M", 0);

		if(lDetail.size() >= 1) {
			for(String strDetail : lDetail) {
				String aSource[] = strDetail.split("/");
				System.out.println(strDetail);
			}
		}

		System.out.println(subStringGetString(str, "[M", "]", 1));
		
	}
}
