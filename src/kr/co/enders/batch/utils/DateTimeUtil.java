package kr.co.enders.batch.utils;

/**
 * @(#) DateTime.java NOTICE ! You can copy or redistribute this code freely, but you should not
 *      remove the information about the copyright notice and the author.
 * @author  WonYoung Lee, javaservice@hanmail.net
 * @author  SooKyung Lim, sukyunglim@lgeds.lg.co.kr. 
 * @history 2000.05.29 isValid(날짜 체크) , whichDay(요일 찾기) 메쏘드  추가 ageBetween, daysBetween 메쏘드 추가 
 *          2000.07.19 addDays, addMonths, addYears 메쏘드 추가
 *          2000.07.22 whichDay, ageBetween, daysBetween, addDays, addMonths, addYears 메쏘드들은 형식이 맞지않거나 존재하지 않는 날짜인 경우 java.text.ParseException 발생 
 *          2000.07.24 daysBetween 메쏘드의 from보다 to가 이전 날짜라면 -999리턴이 아니라 minus(-)값을 리턴함. 
 *          2000.07.29 check 메쏘드의 리턴값(java.util.Date) 설정 addMonths 메쏘드 수정 monthsBetween, lastDayOfMonth 메쏘드 추가
 */
/**
 * @author HAPPYJUNE
 * @version 1.0
 */
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public final class DateTimeUtil
{

  private static final SimpleDateFormat korFormats[];

  private static final SimpleDateFormat engFormats[];

  private static final SimpleDateFormat korTimeFormats[];

  static {
    korFormats = new SimpleDateFormat[4];
    korFormats[0] = new SimpleDateFormat("yyyy/mm/dd", Locale.KOREA);
    korFormats[1] = new SimpleDateFormat("yyyymmdd", Locale.KOREA);
    korFormats[2] = new SimpleDateFormat("yyyy.mm.dd", Locale.KOREA);
    korFormats[3] = new SimpleDateFormat("yyyy-mm-dd", Locale.KOREA);

    engFormats = new SimpleDateFormat[4];
    engFormats[0] = new SimpleDateFormat("mm/dd/yyyy", Locale.KOREA);
    engFormats[1] = new SimpleDateFormat("mmddyyyy", Locale.KOREA);
    engFormats[2] = new SimpleDateFormat("mm.dd.yyyy", Locale.KOREA);
    engFormats[3] = new SimpleDateFormat("mm-dd-yyyy", Locale.KOREA);

    korTimeFormats = new SimpleDateFormat[4];
    korTimeFormats[0] = new SimpleDateFormat("HH:mm:ss", Locale.KOREA);
    korTimeFormats[1] = new SimpleDateFormat("HHmmss", Locale.KOREA);
    korTimeFormats[2] = new SimpleDateFormat("HH.mm.ss", Locale.KOREA);
    korTimeFormats[3] = new SimpleDateFormat("HH-mm-ss", Locale.KOREA);
  }

  /**
   * Don't let anyone instantiate this class
   */
  private DateTimeUtil() {
  }

  /**
   * return add day to date strings
   * 
   * @param String
   *          date string
   * @param int
   *        더할 일수
   * @return int 날짜 형식이 맞고, 존재하는 날짜일 때 일수 더하기 형식이 잘못 되었거나 존재하지 않는 날짜: java.text.ParseException 발생
   */
  public static String addDays(String s, int day) throws java.text.ParseException
  {
    return addDays(s, day, "yyyyMMdd");
  }

  /**
   * return add day to date strings with user defined format.
   * 
   * @param String
   *          date string
   * @param int
   *        더할 일수
   * @param format
   *          string representation of the date format. For example, "yyyy-MM-dd".
   * @return int 날짜 형식이 맞고, 존재하는 날짜일 때 일수 더하기 형식이 잘못 되었거나 존재하지 않는 날짜: java.text.ParseException 발생
   */
  public static String addDays(String s, int day, String format) throws java.text.ParseException
  {
    java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat(format,
                                                                          java.util.Locale.KOREA);
    java.util.Date date = check(s, format);

    date.setTime(date.getTime() + ((long) day * 1000 * 60 * 60 * 24));
    return formatter.format(date);
  }

  /**
   * return add month to date strings
   * 
   * @param String
   *          date string
   * @param int
   *        더할 월수
   * @return int 날짜 형식이 맞고, 존재하는 날짜일 때 월수 더하기 형식이 잘못 되었거나 존재하지 않는 날짜: java.text.ParseException 발생
   */
  public static String addMonths(String s, int month) throws Exception
  {
    return addMonths(s, month, "yyyyMMdd");
  }

  /**
   * return add month to date strings with user defined format.
   * 
   * @param String
   *          date string
   * @param int
   *        더할 월수
   * @param format
   *          string representation of the date format. For example, "yyyy-MM-dd".
   * @return int 날짜 형식이 맞고, 존재하는 날짜일 때 월수 더하기 형식이 잘못 되었거나 존재하지 않는 날짜: java.text.ParseException 발생
   */
  public static String addMonths(String s, int addMonth, String format) throws Exception
  {
    java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat(format,
                                                                          java.util.Locale.KOREA);
    java.util.Date date = check(s, format);

    java.text.SimpleDateFormat yearFormat = new java.text.SimpleDateFormat("yyyy",
                                                                           java.util.Locale.KOREA);
    java.text.SimpleDateFormat monthFormat = new java.text.SimpleDateFormat("MM",
                                                                            java.util.Locale.KOREA);
    java.text.SimpleDateFormat dayFormat = new java.text.SimpleDateFormat("dd",
                                                                          java.util.Locale.KOREA);
    int year = Integer.parseInt(yearFormat.format(date));
    int month = Integer.parseInt(monthFormat.format(date));
    int day = Integer.parseInt(dayFormat.format(date));

    month += addMonth;
    if (addMonth > 0) {
      while (month > 12) {
        month -= 12;
        year += 1;
      }
    }
    else {
      while (month <= 0) {
        month += 12;
        year -= 1;
      }
    }
    java.text.DecimalFormat fourDf = new java.text.DecimalFormat("0000");
    java.text.DecimalFormat twoDf = new java.text.DecimalFormat("00");
    String tempDate = String.valueOf(fourDf.format(year)) + String.valueOf(twoDf.format(month))
                      + String.valueOf(twoDf.format(day));
    java.util.Date targetDate = null;

    try {
      targetDate = check(tempDate, "yyyyMMdd");
    }
    catch (java.text.ParseException pe) {
      day = lastDay(year, month);
      tempDate = String.valueOf(fourDf.format(year)) + String.valueOf(twoDf.format(month))
                 + String.valueOf(twoDf.format(day));
      targetDate = check(tempDate, "yyyyMMdd");
    }

    return formatter.format(targetDate);
  }

  public static String addYears(String s, int year) throws java.text.ParseException
  {
    return addYears(s, year, "yyyyMMdd");
  }

  public static String addYears(String s, int year, String format) throws java.text.ParseException
  {
    java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat(format,
                                                                          java.util.Locale.KOREA);
    java.util.Date date = check(s, format);
    date.setTime(date.getTime() + ((long) year * 1000 * 60 * 60 * 24 * (365 + 1)));
    return formatter.format(date);
  }

  /**
   * return age between two date strings with default defined format.("yyyyMMdd")
   * 
   * @param String
   *          from date string
   * @param String
   *          to date string
   * @return int 날짜 형식이 맞고, 존재하는 날짜일 때 2개 일자 사이의 나이 리턴 형식이 잘못 되었거나 존재하지 않는 날짜:
   *         java.text.ParseException 발생
   */
  public static int ageBetween(String from, String to) throws java.text.ParseException
  {
    return ageBetween(from, to, "yyyyMMdd");
  }

  /**
   * return age between two date strings with user defined format.
   * 
   * @param String
   *          from date string
   * @param String
   *          to date string
   * @param format
   *          string representation of the date format. For example, "yyyy-MM-dd".
   * @return int 날짜 형식이 맞고, 존재하는 날짜일 때 2개 일자 사이의 나이 리턴 형식이 잘못 되었거나 존재하지 않는 날짜:
   *         java.text.ParseException 발생
   */
  public static int ageBetween(String from, String to, String format)
  throws java.text.ParseException
  {
    return (daysBetween(from, to, format) / 365);
  }

  /**
   * check date string validation with the default format "yyyyMMdd".
   * 
   * @param s
   *          date string you want to check with default format "yyyyMMdd".
   * @return date java.util.Date
   */
  public static java.util.Date check(String s) throws java.text.ParseException
  {
    return check(s, "yyyyMMdd");
  }

  /**
   * check date string validation with an user defined format.
   * 
   * @param s
   *          date string you want to check.
   * @param format
   *          string representation of the date format. For example, "yyyy-MM-dd".
   * @return date java.util.Date
   */
  public static java.util.Date check(String s, String format) throws java.text.ParseException
  {
    if (s == null) { throw new java.text.ParseException("date string to check is null", 0); }
    if (format == null) { throw new java.text.ParseException("format string to check date is null",
                                                             0); }

    java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat(format,
                                                                          java.util.Locale.KOREA);
    java.util.Date date = null;
    try {
      date = formatter.parse(s);
    }
    catch (java.text.ParseException e) {
      throw new java.text.ParseException(
                                         " wrong date:\"" + s + "\" with format \"" + format + "\"",
                                         0);
    }

    if (!formatter.format(date).equals(s)) { throw new java.text.ParseException(
                                                                                "Out of bound date:\""
                                                                                + s
                                                                                + "\" with format \""
                                                                                + format + "\"", 0); }
    return date;
  }

  public static String dateToEngFormat(String dateString, char separator)
  {
    Date date = null;

    for (int i = 0; i < korFormats.length; i++) {
      try {
        date = korFormats[i].parse(dateString);
        break;
      }
      catch (Exception e) {
      }
    }

    if (date == null) { return "          "; }

    SimpleDateFormat format = new SimpleDateFormat("mm" + separator + "dd" + separator + "yyyy",
                                                   Locale.KOREA);
    return format.format(date);
  }

  public static String dateToKorFormat(String dateString, char separator)
  {
    Date date = null;

    for (int i = 0; i < korFormats.length; i++) {
      try {
        date = korFormats[i].parse(dateString);
        break;
      }
      catch (Exception e) {
      }
    }

    if (date == null) { return "          "; }

    SimpleDateFormat format = new SimpleDateFormat("yyyy" + separator + "mm" + separator + "dd",
                                                   Locale.KOREA);
    return format.format(date);
  }

  /**
   * return days between two date strings with default defined format.("yyyyMMdd")
   * 
   * @param String
   *          from date string
   * @param String
   *          to date string
   * @return int 날짜 형식이 맞고, 존재하는 날짜일 때 2개 일자 사이의 나이 리턴 형식이 잘못 되었거나 존재하지 않는 날짜:
   *         java.text.ParseException 발생
   */
  public static int daysBetween(String from, String to) throws java.text.ParseException
  {
    return daysBetween(from, to, "yyyyMMdd");
  }

  /**
   * return days between two date strings with user defined format.
   * 
   * @param String
   *          from date string
   * @param String
   *          to date string
   * @return int 날짜 형식이 맞고, 존재하는 날짜일 때 2개 일자 사이의 일자 리턴 형식이 잘못 되었거나 존재하지 않는 날짜:
   *         java.text.ParseException 발생
   */
  public static int daysBetween(String from, String to, String format)
  throws java.text.ParseException
  {
    //java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat(format,
    //    java.util.Locale.KOREA);
    java.util.Date d1 = check(from, format);
    java.util.Date d2 = check(to, format);

    long duration = d2.getTime() - d1.getTime();

    return (int) (duration / (1000 * 60 * 60 * 24));
    // seconds in 1 day
  }

  /**
   * @return formatted string representation of current day with "yyyy-MM-dd".
   */
  public static String getDateString()
  {
    java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyy-MM-dd",
                                                                          java.util.Locale.KOREA);
    return formatter.format(new java.util.Date());
  }

  /**
   * For example, String time = DateTime.getFormatString("yyyy-MM-dd HH:mm:ss");
   * 
   * @param java
   *          .lang.String
   *          pattern "yyyy, MM, dd, HH, mm, ss and more"
   * @return formatted string representation of current day and time with your pattern.
   */
  public static int getDay()
  {
    return getNumberByPattern("dd");
  }

  /**
   * For example, String time = DateTime.getFormatString("yyyy-MM-dd HH:mm:ss");
   * 
   * @param java
   *          .lang.String
   *          pattern "yyyy, MM, dd, HH, mm, ss and more"
   * @return formatted string representation of current day and time with your pattern.
   */
  public static String getFormatString(String pattern)
  {
    java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat(pattern,
                                                                          java.util.Locale.KOREA);
    String dateString = formatter.format(new java.util.Date());
    return dateString;
  }

  /**
   * For example, String time = DateTime.getFormatString("yyyy-MM-dd HH:mm:ss");
   * 
   * @param java
   *          .lang.String
   *          pattern "yyyy, MM, dd, HH, mm, ss and more"
   * @return formatted string representation of current day and time with your pattern.
   */
  public static int getMonth()
  {
    return getNumberByPattern("MM");
  }

  public static int getHour()
  {
    return getNumberByPattern("HH");
  }

  public static String getStrHour()
  {
    String strHour = "00";
    int hour = getNumberByPattern("HH");
    if (hour < 10) strHour = "0" + hour;
    else strHour = "" + hour;
    return strHour;
  }

  /**
   * For example, String time = DateTime.getFormatString("yyyy-MM-dd HH:mm:ss");
   * 
   * @param java
   *          .lang.String
   *          pattern "yyyy, MM, dd, HH, mm, ss and more"
   * @return formatted string representation of current day and time with your pattern.
   */
  public static int getNumberByPattern(String pattern)
  {
    java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat(pattern,
                                                                          java.util.Locale.KOREA);
    String dateString = formatter.format(new java.util.Date());
    return Integer.parseInt(dateString);
  }

  /**
   * @return formatted string representation of current day with "yyyyMMdd".
   */
  public static String getShortDateString()
  {
    java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyyMMdd",
                                                                          java.util.Locale.KOREA);
    return formatter.format(new java.util.Date());
  }

  /**
   * @return formatted string representation of current time with "HHmmss".
   */
  public static String getShortTimeString()
  {
    java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("HHmmss",
                                                                          java.util.Locale.KOREA);
    return formatter.format(new java.util.Date());
  }

  /**
   * @return formatted string representation of current time with "yyyy-MM-dd-HH:mm:ss".
   */
  public static String getTimeStampString()
  {
    java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat(
                                                                          "yyyy-MM-dd-HH:mm:ss:SSS",
                                                                          java.util.Locale.KOREA);
    return formatter.format(new java.util.Date());
  }

  /**
   * @return formatted string representation of current time with "HH:mm:ss".
   */
  public static String getTimeString()
  {
    java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("HH:mm:ss",
                                                                          java.util.Locale.KOREA);
    return formatter.format(new java.util.Date());
  }

  /**
   * For example, String time = DateTime.getFormatString("yyyy-MM-dd HH:mm:ss");
   * 
   * @param java
   *          .lang.String
   *          pattern "yyyy, MM, dd, HH, mm, ss and more"
   * @return formatted string representation of current day and time with your pattern.
   */
  public static int getYear()
  {
    return getNumberByPattern("yyyy");
  }

  /**
   * check date string validation with the default format "yyyyMMdd".
   * 
   * @param s
   *          date string you want to check with default format "yyyyMMdd"
   * @return boolean true 날짜 형식이 맞고, 존재하는 날짜일 때 false 날짜 형식이 맞지 않거나, 존재하지 않는 날짜일 때
   */
  public static boolean isValid(String s) throws Exception
  {
    return DateTimeUtil.isValid(s, "yyyyMMdd");
  }

  /**
   * check date string validation with an user defined format.
   * 
   * @param s
   *          date string you want to check.
   * @param format
   *          string representation of the date format. For example, "yyyy-MM-dd".
   * @return boolean true 날짜 형식이 맞고, 존재하는 날짜일 때 false 날짜 형식이 맞지 않거나, 존재하지 않는 날짜일 때
   */
  public static boolean isValid(String s, String format)
  {
    /*
     * if ( s == null ) throw new NullPointerException("date string to check is null"); if ( format
     * ==
     * null ) throw new NullPointerException("format string to check date is null");
     */
    java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat(format,
                                                                          java.util.Locale.KOREA);
    java.util.Date date = null;
    try {
      date = formatter.parse(s);
    }
    catch (java.text.ParseException e) {
      return false;
    }

    if (!formatter.format(date).equals(s)) { return false; }

    return true;
  }

  private static int lastDay(int year, int month) throws java.text.ParseException
  {
    int day = 0;
    switch (month) {
    case 1:
    case 3:
    case 5:
    case 7:
    case 8:
    case 10:
    case 12:
      day = 31;
      break;
    case 2:
      if ((year % 4) == 0) {
        if (((year % 100) == 0) && ((year % 400) != 0)) {
          day = 28;
        }
        else {
          day = 29;
        }
      }
      else {
        day = 28;
      }
      break;
    default:
      day = 30;
    }
    return day;
  }

  public static String lastDayOfMonth(String src) throws java.text.ParseException
  {
    return lastDayOfMonth(src, "yyyyMMdd");
  }

  public static String lastDayOfMonth(String src, String format) throws java.text.ParseException
  {
    java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat(format,
                                                                          java.util.Locale.KOREA);
    java.util.Date date = check(src, format);

    java.text.SimpleDateFormat yearFormat = new java.text.SimpleDateFormat("yyyy",
                                                                           java.util.Locale.KOREA);
    java.text.SimpleDateFormat monthFormat = new java.text.SimpleDateFormat("MM",
                                                                            java.util.Locale.KOREA);

    int year = Integer.parseInt(yearFormat.format(date));
    int month = Integer.parseInt(monthFormat.format(date));
    int day = lastDay(year, month);

    java.text.DecimalFormat fourDf = new java.text.DecimalFormat("0000");
    java.text.DecimalFormat twoDf = new java.text.DecimalFormat("00");
    String tempDate = String.valueOf(fourDf.format(year)) + String.valueOf(twoDf.format(month))
                      + String.valueOf(twoDf.format(day));
    date = check(tempDate, format);

    return formatter.format(date);
  }

  public static int monthsBetween(String from, String to) throws java.text.ParseException
  {
    return monthsBetween(from, to, "yyyyMMdd");
  }

  public static int monthsBetween(String from, String to, String format)
  throws java.text.ParseException
  {
    //java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat(format,
    //    java.util.Locale.KOREA);
    java.util.Date fromDate = check(from, format);
    java.util.Date toDate = check(to, format);

    // if two date are same, return 0.
    if (fromDate.compareTo(toDate) == 0) { return 0; }

    java.text.SimpleDateFormat yearFormat = new java.text.SimpleDateFormat("yyyy",
                                                                           java.util.Locale.KOREA);
    java.text.SimpleDateFormat monthFormat = new java.text.SimpleDateFormat("MM",
                                                                            java.util.Locale.KOREA);
    java.text.SimpleDateFormat dayFormat = new java.text.SimpleDateFormat("dd",
                                                                          java.util.Locale.KOREA);

    int fromYear = Integer.parseInt(yearFormat.format(fromDate));
    int toYear = Integer.parseInt(yearFormat.format(toDate));
    int fromMonth = Integer.parseInt(monthFormat.format(fromDate));
    int toMonth = Integer.parseInt(monthFormat.format(toDate));
    int fromDay = Integer.parseInt(dayFormat.format(fromDate));
    int toDay = Integer.parseInt(dayFormat.format(toDate));

    int result = 0;
    result += ((toYear - fromYear) * 12);
    result += (toMonth - fromMonth);

    // if (((toDay - fromDay) < 0) ) result += fromDate.compareTo(toDate);
    // ceil과 floor의 효과
    if (((toDay - fromDay) > 0)) {
      result += toDate.compareTo(fromDate);
    }

    return result;
  }

  /**
   * return sub month to date strings
   * 
   * @param String
   *          date string
   * @param int
   *        뺄 월수
   * @return int 날짜 형식이 맞고, 존재하는 날짜일 때 월수 더하기 형식이 잘못 되었거나 존재하지 않는 날짜: java.text.ParseException 발생
   */
  public static String subMonths(String s, int month) throws Exception
  {
    return subMonths(s, month, "yyyyMMdd");
  }

  /**
   * return sub month to date strings with user defined format.
   * 
   * @param String
   *          date string
   * @param int
   *        뺄 월수
   * @param format
   *          string representation of the date format. For example, "yyyy-MM-dd".
   * @return int 날짜 형식이 맞고, 존재하는 날짜일 때 월수 더하기 형식이 잘못 되었거나 존재하지 않는 날짜: java.text.ParseException 발생
   */
  public static String subMonths(String s, int subMonth, String format) throws Exception
  {
    java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat(format,
                                                                          java.util.Locale.KOREA);
    java.util.Date date = check(s, format);

    java.text.SimpleDateFormat yearFormat = new java.text.SimpleDateFormat("yyyy",
                                                                           java.util.Locale.KOREA);
    java.text.SimpleDateFormat monthFormat = new java.text.SimpleDateFormat("MM",
                                                                            java.util.Locale.KOREA);
    java.text.SimpleDateFormat dayFormat = new java.text.SimpleDateFormat("dd",
                                                                          java.util.Locale.KOREA);
    int year = Integer.parseInt(yearFormat.format(date));
    int month = Integer.parseInt(monthFormat.format(date));
    int day = Integer.parseInt(dayFormat.format(date));

    month -= subMonth;
    if (month <= 0) {
      month += 12;
      year -= 1;
    }
    java.text.DecimalFormat fourDf = new java.text.DecimalFormat("0000");
    java.text.DecimalFormat twoDf = new java.text.DecimalFormat("00");
    String tempDate = String.valueOf(fourDf.format(year)) + String.valueOf(twoDf.format(month))
                      + String.valueOf(twoDf.format(day));
    java.util.Date targetDate = null;

    try {
      targetDate = check(tempDate, "yyyyMMdd");
    }
    catch (java.text.ParseException pe) {
      day = lastDay(year, month);
      tempDate = String.valueOf(fourDf.format(year)) + String.valueOf(twoDf.format(month))
                 + String.valueOf(twoDf.format(day));
      targetDate = check(tempDate, "yyyyMMdd");
    }

    return formatter.format(targetDate);
  }

  public static String TimeToKorFormat(String dateString, char separator)
  {
    Date date = null;

    for (int i = 0; i < korTimeFormats.length; i++) {
      try {
        date = korTimeFormats[i].parse(dateString);
        break;
      }
      catch (Exception e) {
      }
    }

    if (date == null) { return "          "; }

    SimpleDateFormat format = new SimpleDateFormat("HH" + separator + "mm" + separator + "ss",
                                                   Locale.KOREA);
    return format.format(date);
  }

  /**
   * return days between two date strings with default defined format.(yyyyMMdd)
   * 
   * @param s
   *          date string you want to check.
   * @return int 날짜 형식이 맞고, 존재하는 날짜일 때 요일을 리턴 형식이 잘못 되었거나 존재하지 않는 날짜: java.text.ParseException 발생 0:
   *         일요일 (java.util.Calendar.SUNDAY 와 비교) 1: 월요일 (java.util.Calendar.MONDAY 와 비교) 2: 화요일
   *         (java.util.Calendar.TUESDAY 와 비교) 3: 수요일 (java.util.Calendar.WENDESDAY 와 비교) 4: 목요일
   *         (java.util.Calendar.THURSDAY 와 비교) 5: 금요일 (java.util.Calendar.FRIDAY 와 비교) 6: 토요일
   *         (java.util.Calendar.SATURDAY 와 비교) 예) String s = "20000529"; int dayOfWeek =
   *         whichDay(s, format); if (dayOfWeek == java.util.Calendar.MONDAY) System.out.println("
   *         월요일: " + dayOfWeek); if (dayOfWeek == java.util.Calendar.TUESDAY) System.out.println("
   *         화요일: " + dayOfWeek);
   */
  public static int whichDay(String s) throws java.text.ParseException
  {
    return whichDay(s, "yyyyMMdd");
  }

  /**
   * return days between two date strings with user defined format.
   * 
   * @param s
   *          date string you want to check.
   * @param format
   *          string representation of the date format. For example, "yyyy-MM-dd".
   * @return int 날짜 형식이 맞고, 존재하는 날짜일 때 요일을 리턴 형식이 잘못 되었거나 존재하지 않는 날짜: java.text.ParseException 발생 0:
   *         일요일 (java.util.Calendar.SUNDAY 와 비교) 1: 월요일 (java.util.Calendar.MONDAY 와 비교) 2: 화요일
   *         (java.util.Calendar.TUESDAY 와 비교) 3: 수요일 (java.util.Calendar.WENDESDAY 와 비교) 4: 목요일
   *         (java.util.Calendar.THURSDAY 와 비교) 5: 금요일 (java.util.Calendar.FRIDAY 와 비교) 6: 토요일
   *         (java.util.Calendar.SATURDAY 와 비교) 예) String s = "2000-05-29"; int dayOfWeek =
   *         whichDay(s, "yyyy-MM-dd"); if (dayOfWeek == java.util.Calendar.MONDAY)
   *         System.out.println(" 월요일: " + dayOfWeek); if (dayOfWeek == java.util.Calendar.TUESDAY)
   *         System.out.println(" 화요일: " + dayOfWeek);
   */

  public static int whichDay(String s, String format) throws java.text.ParseException
  {
    SimpleDateFormat formatter = new SimpleDateFormat(format, java.util.Locale.KOREA);
    Date date = check(s, format);

    Calendar calendar = formatter.getCalendar();
    calendar.setTime(date);
    return calendar.get(java.util.Calendar.DAY_OF_WEEK);
  }

  /**
   * s : yyyymmdd 형태(이형태가 이날 s리턴) s1 : 년월일구분지을특수문자 ("20010101","-") --> "2001-01-01"
   */
  public static String convertDate(String s, String s1) throws java.text.ParseException
  {
    String newDate = null;

    try {
      if (!isValid(s)) { return s; }
    }
    catch (Exception e) {
    }

    String stryear = s.substring(0, 4);
    String strmonth = s.substring(4, 6);
    String strday = s.substring(6, 8);
    newDate = stryear + s1 + strmonth + s1 + strday;

    return newDate;

  }

  public static String getDateFromNow(int diff)
  {
    String rtn_date = null;
    try {
      Calendar calendar = new GregorianCalendar(Locale.KOREA);
      Date trialTime = new Date();
      calendar.setTime(trialTime);

      calendar.add(Calendar.DATE, diff);
      String year = String.valueOf(calendar.get(Calendar.YEAR));
      String month = String.valueOf(calendar.get(Calendar.MONTH) + 1);
      String date = String.valueOf(calendar.get(Calendar.DATE));
      if (month.length() < 2) month = "0" + month;
      if (date.length() < 2) date = "0" + date;
      rtn_date = year + month + date;
    }
    catch (Exception e) {

    }
    return rtn_date;
  }
}
