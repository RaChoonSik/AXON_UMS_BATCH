package kr.co.enders.batch.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * =======================================================
 * 설명 : 날짜관련 유틸
 * =======================================================
 */
public class DateUtil {
	/**
	 * 지정한 년도만큼 더하는 처리
	 * @param yyyyMMdd
	 * @param addAmount (YEAR)
	 * @return 지정한 기간만큼 더해진 날짜값
	 */
	public static String addYear(final String yyyyMMdd, final int addAmount) {
		return addDate(yyyyMMdd, addAmount, Calendar.YEAR, "yyyyMMdd");
	}
	/**
	 * 지정한 년도만큼 더하는 처리
	 * @param dateStr
	 * @param addAmount (YEAR)
	 * @param format Date형식
	 * @return 지정한 기간만큼 더해진 날짜값 (지정한 형식으로 반환)
	 */
	public static String addYear(final String dateStr, final int addAmount, final String format) {
		return addDate(dateStr, addAmount, Calendar.YEAR, format);
	}
	/**
	 * 지정한 개월만큼 더하는 처리
	 * @param yyyyMMdd
	 * @param addAmount (MONTH)
	 * @return 지정한 기간만큼 더해진 날짜값
	 */
	public static String addMonth(final String yyyyMMdd, final int addAmount) {
		return addDate(yyyyMMdd, addAmount, Calendar.MONTH, "yyyyMMdd");
	}
	/**
	 * 지정한 개월만큼 더하는 처리
	 * @param dateStr
	 * @param addAmount (MONTH)
	 * @param format Date형식
	 * @return 지정한 기간만큼 더해진 날짜값 (지정한 형식으로 반환)
	 */
	public static String addMonth(final String dateStr, final int addAmount, final String format) {
		return addDate(dateStr, addAmount, Calendar.MONTH, format);
	}
	/**
	 * 지정한 날짜만큼 더하는 처리
	 * @param yyyyMMdd
	 * @param addAmount (DAY)
	 * @return 지정한 기간만큼 더해진 날짜값
	 */
	public static String addDay(final String yyyyMMdd, final int addAmount) {
		return addDate(yyyyMMdd, addAmount, Calendar.DATE, "yyyyMMdd");
	}
	/**
	 * 지정한 날짜만큼 더하는 처리
	 * @param dateStr
	 * @param addAmount (DAY)
	 * @param format Date형식
	 * @return 지정한 기간만큼 더해진 날짜값 (지정한 형식으로 반환)
	 */
	public static String addDay(final String dateStr, final int addAmount, final String format) {
		return addDate(dateStr, addAmount, Calendar.DATE, format);
	}
	/**
	 * 지정한 시간만큼 더하는 처리
	 * @param dateStr
	 * @param addAmount (HOUR)
	 * @param format Date형식
	 * @return 지정한 기간만큼 더해진 날짜값 (지정한 형식으로 반환)
	 */
	public static String addHour(final String dateStr, final int addAmount, final String format) {
		return addDate(dateStr, addAmount, Calendar.HOUR, format);
	}
	/**
	 * 전날 날짜를 구하는 처리
	 * @return Yesterday yyyyMMdd
	 */
	public static String getYesterday() {
		return addDate(simpleGetCurrentDate("yyyyMMdd"), -1, Calendar.DATE, "yyyyMMdd");
	}
	/**
	 * 전날 날짜를 구하는 처리
	 * @param format 날짜형식
	 * @return Yesterday yyyyMMdd
	 */
	public static String getYesterday(String format) {
		return addDate(simpleGetCurrentDate(format), -1, Calendar.DATE, format);
	}
	/**
	 * 현재DATE를 정해진 포맷으로 가져오는 처리
	 * @param format 출력하려는 날짜포맷 
	 * @return
	 */
	public static String simpleGetCurrentDate(final String format) {
		SimpleDateFormat dFormat = new SimpleDateFormat(format, Locale.KOREA);
		return dFormat.format(new Date());
	}
	/**
	 * 날짜형식이 맞는지 검증하는 처리.
	 * @param s date string you want to check.
	 * @param format string representation of the date format. For example, "yyyy-MM-dd".
	 * @return boolean true 날짜 형식이 맞고, 존재하는 날짜일 때
	 *                 false 날짜 형식이 맞지 않거나, 존재하지 않는 날짜일 때
	 */
	public static boolean isValid(final String s, final String format) {
		SimpleDateFormat formatter = new SimpleDateFormat (format, Locale.KOREA);
		try {
			Date date = formatter.parse(s);
			if (!formatter.format(date).equals(s)) return false;
		} catch(ParseException e) {
            return false;
		}
        return true;
	}
	/**
	 * 지정한 Date에 일정기간을 더하는 처리 (String -> String)
	 * @param yyyyMMdd
	 * @param addAmount
	 * @param field
	 * @param format Date형식
	 * @return 지정한 기간만큼 더해진 날짜값
	 */
	public static String addDate(final String yyyyMMdd, final int addAmount, final int field, final String format) {
		SimpleDateFormat sdfmt = new SimpleDateFormat(format); 
		Date date = null;
		Calendar cal = null;
		try {
			date = sdfmt.parse(yyyyMMdd);
			cal = Calendar.getInstance();
			cal.setTime(date);
			cal.add(field, addAmount);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return sdfmt.format(cal.getTime());
	}
	/**
	 * 지정한 Date에 일정기간을 더하는 처리 (Date -> Date)
	 * @param yyyyMMdd
	 * @param addAmount
	 * @param field
	 * @param format Date형식
	 * @return 지정한 기간만큼 더해진 날짜값
	 */
	public static Date addDate(final Date date, final int addAmount, final int field) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(field, addAmount);
		return cal.getTime();
	}
	/**
	 * String형 날짜값을 Date형으로 변환하는 처리
	 * @param dateStr
	 * @param format
	 * @return Date로 변환된 날짜값
	 * @throws ParseException
	 */
	public static Date stringToDate(final String dateStr, final String format) throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat (format, Locale.KOREA);
		return formatter.parse(dateStr);
	}
	/**
	 * Date형 날짜값을 String형으로 변환하는 처리
	 * @param date
	 * @param format
	 * @return String으로 변환된 날짜값
	 * @throws ParseException
	 */
	public static String dateToString(final Date date, final String format) throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat (format, Locale.KOREA);
		return formatter.format(date);
	}
	public static int getNumberByPattern(String pattern) {
		java.text.SimpleDateFormat formatter =
            new java.text.SimpleDateFormat (pattern, java.util.Locale.KOREA);
		String dateString = formatter.format(new java.util.Date());
		return Integer.parseInt(dateString);
	}
	
	public static int getYear() {
		return getNumberByPattern("yyyy");
	}

	public static int getHour() {
		return getNumberByPattern("HH");
	}
	
	/////////////////////////////////////////-- 이하 디버그용 코드 --/////////////////////////////////////////
	/**
	 * UT용 메인메서드
	 * @param args 
	 * @throws Exception
	 */
	public static void main(String[] args) {
		
		
//		System.out.println(addDay("2012-06-12 00:00:00", 1, "yyyy-MM-dd hh:mm:ss"));
//		System.out.println(getWorkDate());
//		System.out.println(isValid("20120229", "yyyyMMdd"));
		System.out.println(simpleGetCurrentDate("yyyyMMdd"));
		System.out.println(getYesterday());
		System.out.println("addYear = " + addYear("20130618", 2));
		System.out.println("addYear = " + addYear("201306180812", 2, "yyyyMMddHHmm"));
		System.out.println("addMonth = " + addMonth("20130618", 2));
		System.out.println("addMonth = " + addMonth("201306180812", 2, "yyyyMMddHHmm"));
		System.out.println("addMonth = " + addMonth("20131218", 1));
		System.out.println("addDay = " + addDay("20131231", 1));
		System.out.println("addDay = " + addDay("20130626", -90));
		
		SimpleDateFormat formatter = new SimpleDateFormat ("yyyyMMdd", Locale.KOREA);
		try {
			Date date = formatter.parse("20130611");
			System.out.println(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		System.out.println(simpleGetCurrentDate("yyyyMMdd_HH"));
		
		System.out.println(addHour(simpleGetCurrentDate("yyyyMMdd_HH"), -1, "yyyyMMdd_HH"));
		
		System.out.println(getHour());
		
		
		System.out.println("----------------------------------------");
		String fileDateFormat = DateUtil.simpleGetCurrentDate("yyyyMMdd_HH");
		int currentHH = DateUtil.getHour();
		
		
		for (int i = currentHH; i > 0; i--) {
			System.out.println(DateUtil.addHour(fileDateFormat, i, "yyyyMMdd_HH"));
		}
		System.out.println("----------------------------------------");
		
	}
}
