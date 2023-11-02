package kr.co.enders.batch.utils;

/**
 * @author HAPPYJUNE
 * @version 1.0
 */
import java.io.UnsupportedEncodingException;

/**
 * 한글이 인식되지 못하고 깨지는 현상을 막기 위해 한글코드로 변환 Converts Korean to English or viceversa.
 * 
 * @see Class
 * @version 1.00, 15 Feb 2004
 */

public class StrConvertUtil
{
  /**
   * Returns a string which is converted into Korean characters.
   * 
   * @param String
   *          which should be converted into Korean characters.
   * @return String
   */
  public String convertToKr(String ch) throws Exception
  {
    String rtnString = "";
    if (ch != null) {
      rtnString = new String(ch.getBytes("8859_1"), "EUC_KR");
    }
    return rtnString;
  }

  public String convertToEn(String ch) throws Exception
  {
    String rtnString = "";
    if (ch != null) {
      rtnString = new String(ch.getBytes("EUC_KR"), "8859_1");
    }
    return rtnString;
  }

  public String convertAlti(String ch) throws Exception
  {
    String rtnString = "";
    if (ch != null) {
      rtnString = new String(ch.getBytes("EUC_KR"), "KSC5601");
    }
    return rtnString;
  }

  public String convertToUTF(String ch) throws Exception
  {
    String rtnString = "";
    if (ch != null) {
      rtnString = new String(ch.getBytes("EUC_KR"), "UTF-8");
    }
    return rtnString;
  }

  public String a2h(String ch) throws Exception
  {
    String rtnString = "";
    if (ch != null) {
      rtnString = new String(ch.getBytes("US7ASCII"), "KSC5601");
    }
    return rtnString;
  }

  /**
   * Returns a string which is converted into Korean characters.
   * 
   * @param String
   *          []
   *          which should be converted into Korean characters.
   * @return String[]
   */
  public String[] convertToKr(String[] chArray) throws Exception
  {
    String[] rtnStringArray = new String[chArray.length];
    for (int i = 0; i < chArray.length; i++) {
      rtnStringArray[i] = convertToKr(chArray[i]);
    }
    return rtnStringArray;
  }

  /**
   * 8859_1 --> KSC5601.
   */
  public static String E2K(String english)
  {
    String korean = null;

    if (english == null) { return null; }
    // if (english == null ) return "";

    try {
      korean = new String(english.getBytes("8859_1"), "KSC5601");
    }
    catch (UnsupportedEncodingException e) {
      korean = new String(english);
    }
    return korean;
  }

  /**
   * KSC5601 --> 8859_1.
   */
  public static String K2E(String korean)
  {
    String english = null;

    if (korean == null) { return null; }
    // if (korean == null ) return "";

    english = new String(korean);
    try {
      english = new String(korean.getBytes("KSC5601"), "8859_1");
    }
    catch (UnsupportedEncodingException e) {
      english = new String(korean);
    }
    return english;
  }

  /**
   * 2003.07.10 Add zhangse. 바이트로변환후 한국유니코드적용
   */
  public static String us2kr(String src)
  {
    String ret = "";
    try {
      if (src.length() > 0) {
        ret = new String(src.getBytes("8859_1"), "KSC5601");
      }
    }
    catch (Exception e) {
    }
    return ret;
  }

  /**
   * Class constructor.
   */
  public StrConvertUtil() {
  }
}
