package kt.aivle.util;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SimpleTimeZone;
import java.util.TimeZone;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DateUtil {

  /**
   * The Constant YEAR.
   */
  public static final int YEAR = 1;
  /**
   * The Constant MONTH.
   */
  public static final int MONTH = 2;
  /**
   * The Constant DATE.
   */
  public static final int DATE = 3;
  /**
   * The Constant MONTHFIRST.
   */
  public static final int MONTHFIRST = 4;
  /**
   * The Constant MONTHEND.
   */
  public static final int MONTHEND = 5;
  /**
   * 달의 일자.
   */
  public static final String[] DATES_OF_MONTH = {"01", "02", "03", "04", "05", "06", "07", "08",
      "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23",
      "24", "25", "26", "27", "28", "29", "30", "31"};
  /**
   * 년의 달들.
   */
  public static final String[] MONTHES_OF_YEAR = {"01", "02", "03", "04", "05", "06", "07", "08",
      "09", "10", "11", "12"};
  /**
   * Month Names.
   */
  public static final String[] MONTH_NAMES = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul",
      "Aug", "Sep", "Oct", "Nov", "Dec"};
  public static final String DEFAULT_DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

  public static final SimpleDateFormat DATETIME_PATTERN = new SimpleDateFormat(
      "yyyy-MM-dd HH:mm:ss.S");

  /**
   * The Constant patterns.
   */
  private static String[] patterns = {"yyyy-MM-dd HH:mm:ss.SSS", "yyyy-MM-dd HH:mm:ss.S",
      "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM-dd HH", "yyyy-MM-dd", "yyyyMMddHHmmssSSS",
      "yyyyMMddHHmmss", "yyyyMMddHHmm", "yyyyMMddHH", "yyyyMMdd", "yyMMdd"};

  // today
  public static Date getToday() {
    return new Date();
  }

  public static Date convertDate(int field, int amount) {
    Calendar calendar = Calendar.getInstance();
    calendar.add(field, amount);

    return calendar.getTime();
  }

  /**
   * Gets the date format.
   *
   * @param pattern the pattern
   * @return the date format
   */
  public static DateFormat getDateFormat(String pattern) {
    return new SimpleDateFormat(pattern, Locale.KOREAN);
  }

  /**
   * <p>
   * 문자열을 입력받아 패턴에 맞게 날짜형(Date)으로 변환하여 반환한다.
   * </p>
   *
   * <pre>
   * toDate(&quot;2008-11-11 06:15:00&quot;, &quot;yyyy-MM-dd HH:mm:ss&quot;) = Date@&quot;2008-11-11 06:15:00&quot;
   * toDate(&quot;Time is Gold&quot;, &quot;yyyy-MM-dd HH:mm:ss&quot;)        = ParseException
   * toDate(null, *)                                      = ParseException
   * </pre>
   *
   * @param pattern the pattern
   * @param strDate the str date
   * @return the date
   * @throws ParseException 변환을 실패할때 발생
   *                        <p>
   *                        patterns = { "yyyy-MM-dd HH:mm:ss.SSS", "yyyy-MM-dd HH:mm:ss.S", "yyyy-MM-dd HH:mm:ss"
   *                        ,"yyyy-MM-dd HH:mm", "yyyy-MM-dd HH", "yyyy-MM-dd", "yyyyMMddHHmmssSSS", "yyyyMMddHHmmss",
   *                        "yyyyMMddHHmm", "yyyyMMddHH", "yyyyMMdd", "yyMMdd"};
   */
  public static Date toDate(String strDate, String pattern) {
    Date returnDate = null;
    if (strDate != null) {
      DateFormat dateFormat = getDateFormat(pattern);
      try {
        Date date = dateFormat.parse(strDate);
        if (strDate.equals(dateFormat.format(date))) {
          returnDate = date;
        } else {
          returnDate = null;
        }
      } catch (ParseException e) {
        e.printStackTrace();
        returnDate = null;
      }
    }
    return returnDate;
  }

  /**
   * <p>
   * 문자열을 입력받아 날짜형(Date)으로 변환한다.
   * </p>
   *
   * <pre>
   * DateUtils.toDate(null)                  = null
   * DateUtils.toDate(&quot;2008-11-11&quot;)          = Date@&quot;2008-11-11 00:00:00&quot;
   * DateUtils.toDate(&quot;2008-11-11 06:15:00&quot;) = Date@&quot;2008-11-11 06:15:00&quot;
   * </pre>
   *
   * <h4>지원하는형식</h4>
   * <ul>
   * <li>yyyy-MM-dd HH:mm:ss.SSS
   * <li>yyyy-MM-dd HH:mm:ss.S
   * <li>yyyy-MM-dd HH:mm:ss
   * <li>yyyy-MM-dd HH:mm
   * <li>yyyy-MM-dd HH
   * <li>yyyy-MM-dd
   * <li>yyyyMMddHHmmssSSS
   * <li>yyyyMMddHHmmss
   * <li>yyyyMMddHHmm
   * <li>yyyyMMddHH
   * <li>yyyyMMdd
   * </ul>
   *
   * @param strDate the str date
   * @return the date
   * @throws IllegalArgumentException 지원하지 않는 형식일 경우
   */

  public static Date toDate(String strDate) throws IllegalArgumentException {

    Date returnDate = null;
    if (strDate != null) {
      String tempStrDate = strDate;
      tempStrDate = tempStrDate.trim();
      int lenStrDate = tempStrDate.length();

      for (String pattern : patterns) {

        if (lenStrDate == pattern.length()) {
          DateFormat dateFormat = getDateFormat(pattern);
          try {
            Date date = dateFormat.parse(tempStrDate);
            if (tempStrDate.equals(dateFormat.format(date))) {
              returnDate = date;
            } else {
              returnDate = null;
            }
          } catch (ParseException e) {
            returnDate = null;
          }
        }
      }
    }
    return returnDate;
  }

  /**
   * Gets the default date.
   *
   * @param calType the calendar type
   * @param val     the value
   * @param format  the format pattern
   * @return the default date of Calendar's Date/Month/Year +/- value by yyyyMMdd/yyyy-MM-dd
   */
  public static String getDefaultDate(int calType, int val, String format) {

    Calendar cal = Calendar.getInstance();
    cal.add(calType, val); // Calendar.Date , -1

    TimeZone tz;
    Date date = cal.getTime();
    DateFormat df = new SimpleDateFormat(format, Locale.KOREAN);

    tz = TimeZone.getTimeZone("Greenwich"); // GMT +000
    df.setTimeZone(tz);

    return df.format(date);
  }

  /**
   * get Default Date
   *
   * @param calType
   * @param val
   * @param format
   * @param yyyyMMdd
   * @return
   */
  public static String getDefaultDate(int calType, int val, String format, String yyyyMMdd) {

    Calendar cal = Calendar.getInstance();

    cal.set(Integer.parseInt(yyyyMMdd.substring(0, 4)),
        Integer.parseInt(yyyyMMdd.substring(4, 6)) - 1, Integer.parseInt(yyyyMMdd.substring(6, 8)));

    cal.add(calType, val); // Calendar.Date , -1

    TimeZone tz;
    Date date = cal.getTime();
    DateFormat df = new SimpleDateFormat(format, Locale.KOREAN);

    tz = TimeZone.getTimeZone("Greenwich"); // GMT +000
    df.setTimeZone(tz);

    return df.format(date);
  }

  /**
   * <p>
   * 입력한 날짜에 해당하는 GregorianCalendar 객체를 반환한다.
   *
   * @param yyyymmdd 날짜 인수
   * @return GregorianCalendar
   * @see Calendar
   * @see GregorianCalendar <p>
   */
  public static GregorianCalendar getGregorianCalendar(String yyyymmdd) {

    int yyyy = 0;
    int mm = 0;
    int dd = 0;
    if (yyyymmdd != null && yyyymmdd.length() > 0) {
      mm = Integer.parseInt(yyyymmdd.substring(4, 6));
      dd = Integer.parseInt(yyyymmdd.substring(6));
      yyyy = Integer.parseInt(yyyymmdd.substring(0, 4));
    }

    GregorianCalendar calendar = new GregorianCalendar(yyyy, mm - 1, dd, 0, 0, 0);

    return calendar;

  }

  /**
   * <p>
   * 현재 날짜와 시각을 yyyyMMddHHmmss 형태로 변환 후 return.
   *
   * @return yyyyMMddHHmmss
   * @see Date
   * @see Locale <p>
   */
  public static String getLocalDateTime() {
    Date today = new Date();
    Locale currentLocale = new Locale("KOREAN", "KOREA");
    String pattern = "yyyyMMddHHmmss";
    SimpleDateFormat formatter = new SimpleDateFormat(pattern, currentLocale);
    return formatter.format(today);
  }

  /**
   * <p>
   * 현재 날짜와 시각을 yyyyMMddHHmmssSSS 형태로 변환 후 return.
   *
   * @return yyyyMMddHHmmssSSS
   */
  public static String getLocalDateMilisecondTime() {
    Date today = new Date();
    Locale currentLocale = new Locale("KOREAN", "KOREA");
    String pattern = "yyyyMMddHHmmssSSS";
    SimpleDateFormat formatter = new SimpleDateFormat(pattern, currentLocale);
    return formatter.format(today);
  }

  /**
   * <p>
   * BigDecimal type MilisecondTime 를 문자열 날짜로 변환 후 return;
   *
   * @return yyyyMMddHHmmssSSS
   */
  public static String getMilisecondTimeToStrDate(BigDecimal sMilisecondTime, String sPattern) {
    String result = "";
    String pattern = "yyyyMMddHHmmssSSS";

    Calendar cal = Calendar.getInstance();
    if (sMilisecondTime != null) {
      if (sPattern != null && !"".equals(sPattern)) {
        pattern = sPattern;
        cal.setTimeInMillis(sMilisecondTime.longValue());
        result = getDateFormat(cal, pattern);
      } else {
        cal.setTimeInMillis(sMilisecondTime.longValue());
        result = getDateFormat(cal, pattern);
      }
    }
    return result;
  }

  /**
   * <p>
   * 현재 시각을 HHmmss 형태로 변환 후 return.
   *
   * @return HHmmss
   * @see Date
   * @see Locale <p>
   */
  public static String getLocalTime() {
    Date today = new Date();
    Locale currentLocale = new Locale("KOREAN", "KOREA");
    String pattern = "HHmmss";
    SimpleDateFormat formatter = new SimpleDateFormat(pattern, currentLocale);
    return formatter.format(today);

  }

  /**
   * <p>
   * 현재 날짜를 yyyyMMdd 형태로 변환 후 return.
   *
   * @return String yyyymmdd
   * <p>
   */
  public static String getLocalDate() {
    return getLocalDateTime().substring(0, 8);
  }

  /**
   * <p>
   * 지정된 플래그에 따라 연도 , 월 , 일자를 연산한다.
   *
   * @param field    연산 필드
   * @param amount   더할 수
   * @param yyyymmdd 연산 대상 날짜
   * @return 연산된 날짜
   * @see Calendar <p>
   */
  public static String calculateDate(int field, int amount, String yyyymmdd) {

    return calculateDate(field, amount, yyyymmdd, "yyyyMMdd");
  }

  /**
   * <p>
   * 지정된 플래그에 따라 연도 , 월 , 일자를 연산한다. 지정된 pattern format으로 리턴
   *
   * @param field    연산 필드
   * @param amount   더할 수
   * @param yyyymmdd 연산 대상 날짜 ( foramt은 'yyyyMMdd' )
   * @param pattern  the pattern
   * @return 연산된 날짜
   * @see Calendar <p>
   */
  public static String calculateDate(int field, int amount, String yyyymmdd, String pattern) {

    GregorianCalendar calDate = getGregorianCalendar(yyyymmdd);

    if (field == Calendar.YEAR) {
      calDate.add(GregorianCalendar.YEAR, amount);
    } else if (field == Calendar.MONTH) {
      calDate.add(GregorianCalendar.MONTH, amount);
    } else {
      calDate.add(GregorianCalendar.DATE, amount);
    }

    return getDateFormat(calDate, pattern);

  }

  /**
   * <p>
   * 입력된 일자를 더한 주를 구하여 해당 요일을 return한다 일 - 1 월 2 화 3 수 4 목 5 금 6 토 7.
   *
   * @param yyyymmdd - 년도별
   * @param addDay   - 추가일
   * @return int - 연산된 요일
   * @see Calendar <p>
   */
  public static int calculateDayOfWeek(String yyyymmdd, int addDay) {
    Calendar cal = Calendar.getInstance();
    int newYy = Integer.parseInt(yyyymmdd.substring(0, 4));
    int newMm = Integer.parseInt(yyyymmdd.substring(4, 6));
    int newDd = Integer.parseInt(yyyymmdd.substring(6, 8));

    cal.set(newYy, newMm - 1, newDd);
    cal.add(Calendar.DATE, addDay);

    int week = cal.get(Calendar.DAY_OF_WEEK);
    return week;
  }

  /**
   * <p>
   * 입력된 년월의 마지막 일수를 return 한다.
   *
   * @param year  the year
   * @param month the month
   * @return 마지막 일수
   * @see Calendar <p>
   */
  public static int getLastDayOfMonth(int year, int month) {

    Calendar cal = Calendar.getInstance();
    cal.set(year, month - 1, 1);
    return cal.getActualMaximum(Calendar.DAY_OF_MONTH);

  }

  /**
   * <p>
   * 입력된 년월의 마지막 일수를 return한다.
   *
   * @param yyyymm the yyyymm
   * @return 마지막 일수
   * <p>
   */
  public static int getLastDayOfMonth(String yyyymm) {

    Calendar cal = Calendar.getInstance();
    int yyyy = Integer.parseInt(yyyymm.substring(0, 4));
    int mm = Integer.parseInt(yyyymm.substring(4)) - 1;

    cal.set(yyyy, mm, 1);
    return cal.getActualMaximum(Calendar.DAY_OF_MONTH);
  }

  /**
   * <p>
   * 입력된 날자가 올바른지 확인합니다.
   *
   * @param yyyymmdd the yyyymmdd
   * @return boolean
   * <p>
   */
  public static boolean isCorrect(String yyyymmdd) {
    boolean flag = false;
    if (yyyymmdd == null || yyyymmdd.length() == 0 || yyyymmdd.length() < 8) {
      flag = false;
    } else {
      try {
        int yyyy = Integer.parseInt(yyyymmdd.substring(0, 4));
        int mm = Integer.parseInt(yyyymmdd.substring(4, 6));
        int dd = Integer.parseInt(yyyymmdd.substring(6));
        flag = DateUtil.isCorrect(yyyy, mm, dd);
      } catch (Exception ex) {
        flag = false;
      }
    }
    return flag;
  }

  /**
   * <p>
   * 입력된 날자가 올바른 날자인지 확인합니다.
   *
   * @param yyyy the yyyy
   * @param mm   the mm
   * @param dd   the dd
   * @return boolean
   * <p>
   */
  public static boolean isCorrect(int yyyy, int mm, int dd) {
    boolean result = true;
    if (yyyy < 0 || mm < 0 || dd < 0) {
      result = false;
    } else if (mm > 12 || dd > 31) {
      result = false;
    } else {

      String year = "" + yyyy;
      String month = "00" + mm;
      String yearStr = year + month.substring(month.length() - 2);
      int endday = DateUtil.getLastDayOfMonth(yearStr);

      if (dd > endday) {
        result = false;
      }
    }
    return result;

  }

  /**
   * <p>
   * 두 날짜간의 날짜수를 반환(윤년을 감안함).
   *
   * @param startDateAgr 시작 날짜
   * @param endDateAgr   끝 날짜
   * @return 날수
   * @see GregorianCalendar <p>
   */
  public static long getDifferenceOfDays(String startDateAgr, String endDateAgr) {

    GregorianCalendar startDate = getGregorianCalendar(startDateAgr);
    GregorianCalendar endDate = getGregorianCalendar(endDateAgr);
    long difer = (endDate.getTime().getTime() - startDate.getTime().getTime()) / 86400000;
    return difer;

  }

  /**
   * <p>
   * 두 날짜의 차이를 계산한다. 차이값의 단위는 3번쨰 필드로 넘겨진 값을 가지고 결정한다. 만약에 정의되지 않은 값이 넘겨졌다면 millisecond단위로 한다. 필드정의값은
   * java.util.Calendar에 정의된 값을 사용한다. 사용하는 값은 아래와 같다.<br/>
   * <font color='red'>주의! TimeZone offset이 반영되서 계산이 된다.</font>
   * </p>
   * <li>연도:YEAR</li> <li>월:MONTH</li> <li>일:DATE</li> <li>시:HOUR_OF_DAY</li> <li>분:MINUTE</li>
   * <li>초:SECOND</li> <li>밀리초:MILLISECOND</li> <li>주:WEEK_OF_YEAR</li>
   *
   * @param date1 날짜1
   * @param date2 날짜2
   * @param field 비교할 필드값 (java.util.Calendar에 정의된 필드정의값을 참조)
   * @return 차이값 = 날짜2 - 날짜1
   */
  public static long difference(Date date1, Date date2, int field) {
    Calendar cal1 = Calendar.getInstance();
    Calendar cal2 = Calendar.getInstance();

    cal1.setTime(date1);
    cal2.setTime(date2);

    long resultLong;
    if (field == Calendar.YEAR || field == Calendar.WEEK_OF_YEAR) {
      resultLong = cal2.get(field) - cal1.get(field);
    } else if (field == Calendar.MONTH) {
      resultLong = (cal2.get(Calendar.YEAR) * 12 + cal2.get(field)) - (cal1.get(Calendar.YEAR) * 12
          + cal1.get(field));
    } else {
      long time1 = cal1.getTimeInMillis() + cal1.getTimeZone().getRawOffset();
      long time2 = cal2.getTimeInMillis() + cal2.getTimeZone().getRawOffset();

      switch (field) {
        case Calendar.DATE:
          time1 /= 86400000;
          time2 /= 86400000;
          break;
        case Calendar.HOUR_OF_DAY:
          time1 /= 3600000;
          time2 /= 3600000;
          break;
        case Calendar.MINUTE:
          time1 /= 60000;
          time2 /= 60000;
          break;
        case Calendar.SECOND:
          time1 /= 1000;
          time2 /= 1000;
          break;
        default:
          break;
      }

      resultLong = time2 - time1;
    }
    return resultLong;
  }

  /**
   * <p>
   * 현재의 요일을 구한다.
   *
   * @return 요일
   * @see Calendar <p>
   */
  public static int getDayOfWeek() {
    Calendar rightNow = Calendar.getInstance();
    int dayOfWeek = rightNow.get(Calendar.DAY_OF_WEEK);
    return dayOfWeek;
  }

  /**
   * <p>
   * 현재주가 올해 전체의 몇째주에 해당되는지 계산한다.
   *
   * @return 요일
   * @see Calendar <p>
   */
  public static int getWeekOfYear() {
    Locale localeCountry = Locale.KOREA;
    Calendar rightNow = Calendar.getInstance(localeCountry);
    // 삼성전자 ISO8601 용
    // rightNow.setFirstDayOfWeek(Calendar.MONDAY);
    // rightNow.setMinimalDaysInFirstWeek(4);

    int weekOfYear = rightNow.get(Calendar.WEEK_OF_YEAR);
    return weekOfYear;
  }

  /**
   * <p>
   * 현재주가 현재월에 몇째주에 해당되는지 계산한다.
   *
   * @return 요일
   * @see Calendar <p>
   */
  public static int getWeekOfMonth() {
    Locale localeCountry = Locale.KOREA;
    Calendar rightNow = Calendar.getInstance(localeCountry);
    int weekOfMonth = rightNow.get(Calendar.WEEK_OF_MONTH);
    return weekOfMonth;
  }

  /**
   * <p>
   * 입력한 날짜에 해당하는 Calendar 객체를 반환함.
   *
   * @param yyyymmdd the yyyymmdd
   * @return Calendar
   * @see Calendar <p>
   */
  public static Calendar getCalendarInstance(String yyyymmdd) {

    Calendar retCal = Calendar.getInstance();

    if (yyyymmdd != null && yyyymmdd.length() == 8) {
      int year = Integer.parseInt(yyyymmdd.substring(0, 4));
      int month = Integer.parseInt(yyyymmdd.substring(4, 6)) - 1;
      int date = Integer.parseInt(yyyymmdd.substring(6));

      retCal.set(year, month, date);
    }
    return retCal;
  }

  /**
   * <p>
   * 입력받은 시작일과 종료일 사이의.
   *
   * @param from the from
   * @param to   the to
   * @return <p>
   */
  public static String[] getBetweenDays(String from, String to) {
    return getBetweenDaysFormat(from, to, "yyyyMMdd");
  }

  /**
   * <p>
   * 시작일부터 종료일까지 사이의 날짜를 배열에 담아 리턴 시작일과 종료일을 모두 포함한다.
   *
   * @param from    - 시작일
   * @param to      - 종료일
   * @param pattern - 날짜 문자 패턴(예: yyyyMMdd, yyyy-MM-dd )
   * @return String[] - pattern 포멧의 날짜가 담긴 문자열 배열
   * <p>
   * @throws NullPointerException
   */
  public static String[] getBetweenDaysFormat(String from, String to, String pattern) {
    SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.KOREAN);
    ArrayList<String> list = new ArrayList<String>();
    Calendar cal = Calendar.getInstance();
    Date fmdate = null;
    Date todate = null;
    String[] result = null;
    try {
      if (from != null && from.length() > 0) {
        fmdate = sdf.parse(from);
      }
      if (to != null && to.length() > 0) {
        todate = sdf.parse(to);
      }

      if (fmdate != null) {
        cal.setTime(fmdate);
      }

      if (fmdate != null) {
        while (fmdate.compareTo(todate) <= 0) {
          list.add(sdf.format(fmdate));
          cal.add(Calendar.DATE, 1);
          fmdate = cal.getTime();
        }
      }

      result = new String[list.size()];
      list.toArray(result);
    } catch (Exception e) {
      log.error("DateUtils.getBetweenDaysFormat() error.........");
    }

    return result;
  }

  /**
   * <p>
   * 시작일부터 종료일까지 사이의 날짜를 배열에 담아 리턴 시작일과 종료일을 모두 포함한다.
   *
   * @param from    - 시작일
   * @param to      - 종료일
   * @param pattern - 날짜 문자 패턴(예: yyyyMMdd, yyyy-MM-dd )
   * @return String[] - pattern 포멧의 날짜가 담긴 문자열 배열
   * <p>
   * @throws NullPointerException
   */
  public static String[] getBetweenMonthsFormat(String from, String to, String pattern) {
    SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.KOREAN);
    ArrayList<String> list = new ArrayList<String>();
    Calendar cal = Calendar.getInstance();
    Date fmdate = null;
    Date todate = null;
    String[] result = null;
    try {
      if (from != null && from.length() > 0) {
        fmdate = sdf.parse(from);
      }
      if (to != null && to.length() > 0) {
        todate = sdf.parse(to);
      }

      if (fmdate != null) {
        cal.setTime(fmdate);
      }

      if (fmdate != null) {
        while (fmdate.compareTo(todate) <= 0) {
          list.add(sdf.format(fmdate));
          cal.add(Calendar.MONDAY, 1);
          fmdate = cal.getTime();
        }
      }

      result = new String[list.size()];
      list.toArray(result);
    } catch (Exception e) {
      log.error("DateUtils.getBetweenMonthsFormat() error.........");
    }
    return result;
  }

  /**
   * 입력 받은 패턴에 맞추어서, 입력 받은 날짜를 현재 날짜와 비교, 차이가 나는 일수를 리턴.
   *
   * @param pattern  the pattern
   * @param fromDate the from date
   * @return 입력일과 현재일이 동일한 경우 0 리턴
   * @throws NullPointerException
   */
  public static Integer getDateOffset(String pattern, String fromDate) {
    String toDate = getTimeFormat(pattern);
    String[] offset = getBetweenDaysFormat(fromDate, toDate, pattern);
    return offset.length;
  }

  /**
   * 입력 받은 패턴에 맞게 현재 시각을 반환한다.
   *
   * @param pattern 출력할 패턴
   * @return 현재 시각
   * @throws NullPointerException
   */
  public static String getTimeFormat(String pattern) {
    return new SimpleDateFormat(pattern, Locale.KOREAN).format(new Date());
  }

  /**
   * <p>
   * 입력 받은 Calendar 객체를 입력받은 패턴의 문자열로 반환.
   *
   * @param cal     the cal
   * @param pattern the pattern
   * @return yyyyMMdd
   * <p>
   */
  public static String getDateFormat(Calendar cal, String pattern) {
    Locale currentLocale = new Locale("KOREAN", "KOREA");
    return getDateFormat(cal, pattern, currentLocale);
  }

  /**
   * <p>
   * 입력 받은 Calendar 객체를 입력받은 패턴의 문자열로 반환. <br> 입력 받은 locale 설정을 따른다.
   *
   * @param cal     the cal
   * @param pattern the pattern
   * @param locale  the locale
   * @return yyyyMMdd
   * <p>
   * @throws NullPointerException
   */
  public static String getDateFormat(Calendar cal, String pattern, Locale locale) {
    SimpleDateFormat formatter = new SimpleDateFormat(pattern, locale);
    return formatter.format(cal.getTime());
  }

  /**
   * Date 객체를 주어진 locale 과 pattern 형식으로 변환한다.
   *
   * @param date    Date object
   * @param pattern date pattern
   * @param locale  Locale object
   * @return the string
   */
  public static String dateToString(Date date, String pattern, Locale locale) {
    DateFormat df = new SimpleDateFormat(pattern, locale);
    return df.format(date);
  }

  /**
   * <p>
   * Date -> String
   * </p>
   *
   * @param date Date which you want to change.
   * @return String The Date string. Type, yyyyMMdd HH:mm:ss.
   */
  public static String toString(Date date, String format, Locale locale) {

    if (StringUtil.isEmpty(format)) {
      format = "yyyy-MM-dd HH:mm:ss";
    }

    if (ObjectUtil.isNull(locale)) {
      locale = Locale.KOREA;
    }

    SimpleDateFormat sdf = new SimpleDateFormat(format, locale);

    String tmp = sdf.format(date);

    return tmp;
  }

  /**
   * Gets the day of this week. 이번주의 해당 요일의 날짜를 Date형식으로 가져오는 함수. 사용법: 이번주 월요일을 가져오려면,
   * DateUtils.getTheDayOfThisWeek(Calendar.MONDAY);
   *
   * @param dayOfWeek the day of week
   * @return the day of this week
   */
  public static Date getTheDayOfThisWeek(int dayOfWeek) {
    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.DAY_OF_WEEK, dayOfWeek);
    return cal.getTime();
  }

  /**
   * Gets the the first day of this quarter.
   *
   * @param cal the calendar
   * @return the the first day of this quarter
   */
  public static Date getTheFirstDayOfThisQuarter(Calendar cal) {
    Calendar firstDateOfFirstQuarter = Calendar.getInstance();
    Calendar firstDateOfSecondQuarter = Calendar.getInstance();
    Calendar firstDateOfThirdQuarter = Calendar.getInstance();
    Calendar firstDateOfForthQuarter = Calendar.getInstance();

    firstDateOfFirstQuarter.set(cal.get(Calendar.YEAR), 0, 1); // 1월1일
    firstDateOfSecondQuarter.set(cal.get(Calendar.YEAR), 3, 1); // 4월1일
    firstDateOfThirdQuarter.set(cal.get(Calendar.YEAR), 6, 1); // 7월1일
    firstDateOfForthQuarter.set(cal.get(Calendar.YEAR), 9, 1); // 10월1일

    Date date = null;
    if (cal.getTime().compareTo(firstDateOfSecondQuarter.getTime()) < 0) {
      date = firstDateOfFirstQuarter.getTime();
    } else if (cal.getTime().compareTo(firstDateOfThirdQuarter.getTime()) < 0) {
      date = firstDateOfSecondQuarter.getTime();
    } else if (cal.getTime().compareTo(firstDateOfForthQuarter.getTime()) < 0) {
      date = firstDateOfThirdQuarter.getTime();
    } else {
      date = firstDateOfForthQuarter.getTime();
    }
    return date;
  }

  /**
   * Gets the day of previous week. 지난 주의 해당 요일의 날짜를 Date형식으로 가져오는 함수. 사용법: 지난 주 월요일을 가져오려면,
   * DateUtils.getTheDayOfPreviousWeek(Calendar.MONDAY);
   *
   * @param dayOfWeek the day of week
   * @return the day of previous week
   */
  public static Date getTheDayOfPreviousWeek(int dayOfWeek) {
    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.DAY_OF_WEEK, dayOfWeek);
    cal.add(Calendar.DATE, -7);
    return cal.getTime();
  }

  /**
   * Gets yesterday.
   *
   * @return yesterday
   */
  public static Date getYesterday() {
    return getCalculatedDay(-1);
  }

  /**
   * 현재 날짜에서 offset 만큼 일일차가 발생한 날짜의 Date를 return.
   *
   * @param offset the offset
   * @return the calculated day
   */
  public static Date getCalculatedDay(int offset) {
    Calendar cal = Calendar.getInstance();
    cal.add(Calendar.DATE, offset);
    return cal.getTime();
  }

  /**
   * 1년의 월 목록을 출력한다.
   *
   * @return List<String> / 월 목록
   */
  public static List<String> getMonthList() {
    return Arrays.asList(MONTHES_OF_YEAR);
  }

  /**
   * 1개월의 날 목록을 출력한다.
   *
   * @return List<String> / 날 목록
   */
  public static List<String> getDateList() {
    return Arrays.asList(DATES_OF_MONTH);
  }

  /**
   * GMT의 시각 형태로 표현하기 위한 Formatter.
   *
   * @return DateFormat / GMT Time formatter
   */
  public static DateFormat getGMTDateFormat() {
    DateFormat format = new SimpleDateFormat("yyyyMMddHHmmss", Locale.KOREAN);
    format.setTimeZone(TimeZone.getTimeZone("GMT"));

    return format;
  }

  /**
   * yyyyMMddHHmmss의 문자열을 GMT의 Date형 객체로 반환하는 메소드.
   *
   * @param date the date
   * @return Date / GMT 시간으로 설정된 Date 객체
   */
  public static Date getGMTDate(String date) {
    Date returnResult = null;
    DateFormat format = getGMTDateFormat();
    try {
      returnResult = format.parse(date);
    } catch (ParseException e) {
      returnResult = null;
    }
    return returnResult;
  }

  /**
   * 현재 날짜에서 날짜수를 더한만큼 리턴.
   *
   * @param day the day
   * @return 'yyyy/MM/dd'
   */
  public static String getDayAfter(int day) {
    return getDayAfter(getCurrentDate(), day);
  }

  /**
   * 특정날짜(yyyy/mm/dd)에서 날짜수를 더한만큼 리턴.
   *
   * @param startDate the start date
   * @param day       the day
   * @return 'yyyy/MM/dd'
   */
  public static String getDayAfter(String startDate, int day) {
    Calendar cal = Calendar.getInstance();
    cal.set(Integer.parseInt(startDate.substring(0, 4)),
        Integer.parseInt(startDate.substring(5, 7)) - 1,
        Integer.parseInt(startDate.substring(8, 10)));
    Date currentTime = cal.getTime();

    Date oneTime = new Date((long) 24 * 60 * 60 * 1000 * day);
    Date findTime = new Date(currentTime.getTime() + oneTime.getTime());

    return getDateStr(findTime, "/");
  }

  /**
   * 현재 날짜를 YYYY/MM/DD형식으로 반환한다.
   *
   * @return the current date
   */
  public static String getCurrentDate() {
    DateFormat df = new SimpleDateFormat("yyyy/MM/dd", Locale.KOREAN);
    return df.format(new Date(System.currentTimeMillis()));
  }

  /**
   * 날짜 객체를 입력으로 받아 문자열로 반환한다.
   *
   * @param date      날짜 객체
   * @param delimiter 구분자
   * @return 문자열의 날짜가 반환된다.
   */
  public static String getDateStr(Date date, char delimiter) {
    return getDateStr(date, delimiter + "");
  }

  /**
   * 날짜 객체를 입력으로 받아 문자열로 반환한다.
   *
   * @param date      날짜 객체
   * @param delimiter 구분자
   * @return 문자열의 날짜가 반환된다.
   */
  public static String getDateStr(Date date, String delimiter) {
    String returnResult = null;
    if (date != null) {
      DateFormat df = new SimpleDateFormat("yyyy" + delimiter + "MM" + delimiter + "dd",
          Locale.KOREAN);
      returnResult = df.format(date);
    }

    return returnResult;
  }

  /**
   * 1개월의 월의 이름을 출력한다.
   *
   * @param month the month
   * @return the date name list
   */
  public static String getDateNameList(int month) {
    return MONTH_NAMES[month - 1];
  }

  /**
   * Gets the current date string as specified pattern.
   *
   * @param pattern the pattern
   * @return date as form of pattern
   */
  public static String getCurrentDateString(String pattern) {
    DateFormat df = new SimpleDateFormat(pattern, Locale.KOREAN);
    return df.format(new Date(System.currentTimeMillis()));
  }

  /**
   * To date for special case.
   * <p>
   * "Oct 28, 2009 11:40:25 AM" 이형식을 "10-Oct-2009" 패턴으로 변경
   * </p>
   *
   * @param fromDate the from date
   * @return the string
   */
  public static String toDateForSpecialCase(String fromDate) {
    String returnResult = "";
    try {
      String[] arr = fromDate.split(" ");

      if (arr.length >= 3) {

        String day = null;

        day = arr[1];
        if (day.length() == 2) {
          day = day.substring(0, 1);
        } else {
          day = day.substring(0, 2);
        }

        returnResult = day + "-" + arr[0] + "-" + arr[2];
      }
    } catch (Exception e) {
      returnResult = "";
    }
    return returnResult;
  }

  /**
   * 주민등록번호와 기준일자를 입력받아 기준일자(나이)와 비교
   *
   * @param socialNumber String 주민등록번호
   * @param baseAge      int 기준일자(19, 14)
   * @return the boolean
   */
  public static boolean isAdult(String socialNumber, int baseAge) {

    DateFormat df = new SimpleDateFormat("yyyyMMdd", Locale.KOREAN);
    String currentDate = df.format(new Date(System.currentTimeMillis()));

    String birthDate = socialNumber.substring(0, 6);
    String flag = socialNumber.substring(6, 7);

    String birthYear = "";
    String birthMonth = "";
    String birthDay = "";

    String currentYear = currentDate.substring(0, 4);
    String currentMonth = currentDate.substring(4, 6);
    String currentDay = currentDate.substring(6, 8);

    boolean result = true;

    if ("1".equals(flag) || "2".equals(flag) || "5".equals(flag) || "6".equals(flag)) {
      birthYear = "19";
    } else if ("3".equals(flag) || "4".equals(flag) || "7".equals(flag) || "8".equals(flag)) {
      birthYear = "20";
    }

    birthYear += birthDate.substring(0, 2);
    birthMonth += birthDate.substring(2, 4);
    birthDay += birthDate.substring(4, 6);

    int yearInterval = Integer.parseInt(currentYear) - Integer.parseInt(birthYear);

    if (baseAge > yearInterval) {
      result = false;
    } else if (baseAge == yearInterval) {
      if (Integer.parseInt(birthMonth) > Integer.parseInt(currentMonth)) {
        result = false;
      } else if (Integer.parseInt(birthMonth) == Integer.parseInt(currentMonth)
          && Integer.parseInt(birthDay) > Integer.parseInt(currentDay)) {
        result = false;
      }
    }

    return result;
  }

  public static Date getTodayDate() {
    Calendar calendar = Calendar.getInstance(new SimpleTimeZone(0x1ee6280, "KST"));
    return calendar.getTime();
  }

  public static String modifyMinute(Calendar cal, int min) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm", Locale.KOREAN);

    cal.add(Calendar.MINUTE, min);
    return sdf.format(cal.getTime());
  }

  public static String getCurrentTimestamp() {
    long timemillis = System.currentTimeMillis();
    Date date = new Date(timemillis);
    Time time = new Time(timemillis);
    String datetime = date.toString() + " " + time.toString();
    return datetime;
  }

  /**
   * <p>yyyyMMdd 혹은 yyyy-MM-dd 형식의 날짜 문자열을 입력 받아 년, 월, 일을
   * 증감한다. 년, 월, 일은 가감할 수를 의미하며, 음수를 입력할 경우 감한다.</p>
   *
   * <pre>
   * DateUtil.addYearMonthDay("19810828", 0, 0, 19)  = "19810916"
   * DateUtil.addYearMonthDay("20060228", 0, 0, -10) = "20060218"
   * DateUtil.addYearMonthDay("20060228", 0, 0, 10)  = "20060310"
   * DateUtil.addYearMonthDay("20060228", 0, 0, 32)  = "20060401"
   * DateUtil.addYearMonthDay("20050331", 0, -1, 0)  = "20050228"
   * DateUtil.addYearMonthDay("20050301", 0, 2, 30)  = "20050531"
   * DateUtil.addYearMonthDay("20050301", 1, 2, 30)  = "20060531"
   * DateUtil.addYearMonthDay("20040301", 2, 0, 0)   = "20060301"
   * DateUtil.addYearMonthDay("20040229", 2, 0, 0)   = "20060228"
   * DateUtil.addYearMonthDay("20040229", 2, 0, 1)   = "20060301"
   * </pre>
   *
   * @param sDate 날짜 문자열(yyyyMMdd, yyyy-MM-dd의 형식)
   * @param year  가감할 년. 0이 입력될 경우 가감이 없다
   * @param month 가감할 월. 0이 입력될 경우 가감이 없다
   * @param day   가감할 일. 0이 입력될 경우 가감이 없다
   * @return yyyyMMdd 형식의 날짜 문자열
   * @throws IllegalArgumentException 날짜 포맷이 정해진 바와 다를 경우. 입력 값이 <code>null</code>인 경우.
   */
  public static String addYearMonthDay(String sDate, int year, int month, int day) {

    String dateStr = validChkDate(sDate);

    Calendar cal = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
    try {
      cal.setTime(sdf.parse(dateStr));
    } catch (ParseException e) {
      throw new IllegalArgumentException("Invalid date format: " + dateStr);
    }

    if (year != 0) {
      cal.add(Calendar.YEAR, year);
    }
    if (month != 0) {
      cal.add(Calendar.MONTH, month);
    }
    if (day != 0) {
      cal.add(Calendar.DATE, day);
    }
    return sdf.format(cal.getTime());
  }

  /**
   * <p>yyyyMMdd 혹은 yyyy-MM-dd 형식의 날짜 문자열을 입력 받아 년을
   * 증감한다. <code>year</code>는 가감할 수를 의미하며, 음수를 입력할 경우 감한다.</p>
   *
   * <pre>
   * DateUtil.addYear("20000201", 62)  = "20620201"
   * DateUtil.addYear("20620201", -62) = "20000201"
   * DateUtil.addYear("20040229", 2)   = "20060228"
   * DateUtil.addYear("20060228", -2)  = "20040228"
   * DateUtil.addYear("19000101", 200) = "21000101"
   * </pre>
   *
   * @param dateStr 날짜 문자열(yyyyMMdd, yyyy-MM-dd의 형식)
   * @param year    가감할 년. 0이 입력될 경우 가감이 없다
   * @return yyyyMMdd 형식의 날짜 문자열
   * @throws IllegalArgumentException 날짜 포맷이 정해진 바와 다를 경우. 입력 값이 <code>null</code>인 경우.
   */
  public static String addYear(String dateStr, int year) {
    return addYearMonthDay(dateStr, year, 0, 0);
  }

  /**
   * <p>yyyyMMdd 혹은 yyyy-MM-dd 형식의 날짜 문자열을 입력 받아 월을
   * 증감한다. <code>month</code>는 가감할 수를 의미하며, 음수를 입력할 경우 감한다.</p>
   *
   * <pre>
   * DateUtil.addMonth("20010201", 12)  = "20020201"
   * DateUtil.addMonth("19800229", 12)  = "19810228"
   * DateUtil.addMonth("20040229", 12)  = "20050228"
   * DateUtil.addMonth("20050228", -12) = "20040228"
   * DateUtil.addMonth("20060131", 1)   = "20060228"
   * DateUtil.addMonth("20060228", -1)  = "20060128"
   * </pre>
   *
   * @param dateStr 날짜 문자열(yyyyMMdd, yyyy-MM-dd의 형식)
   * @param month   가감할 월. 0이 입력될 경우 가감이 없다
   * @return yyyyMMdd 형식의 날짜 문자열
   * @throws IllegalArgumentException 날짜 포맷이 정해진 바와 다를 경우. 입력 값이 <code>null</code>인 경우.
   */
  public static String addMonth(String dateStr, int month) {
    return addYearMonthDay(dateStr, 0, month, 0);
  }

  /**
   * <p>yyyyMMdd 혹은 yyyy-MM-dd 형식의 날짜 문자열을 입력 받아 일(day)를
   * 증감한다. <code>day</code>는 가감할 수를 의미하며, 음수를 입력할 경우 감한다. <br/><br/> 위에 정의된 addDays 메서드는 사용자가 ParseException을 반드시 처리해야
   * 하는 불편함이 있기 때문에 추가된 메서드이다.</p>
   *
   * <pre>
   * DateUtil.addDay("19991201", 62) = "20000201"
   * DateUtil.addDay("20000201", -62) = "19991201"
   * DateUtil.addDay("20050831", 3) = "20050903"
   * DateUtil.addDay("20050831", 3) = "20050903"
   * // 2006년 6월 31일은 실제로 존재하지 않는 날짜이다 -> 20060701로 간주된다
   * DateUtil.addDay("20060631", 1) = "20060702"
   * </pre>
   *
   * @param dateStr 날짜 문자열(yyyyMMdd, yyyy-MM-dd의 형식)
   * @param day     가감할 일. 0이 입력될 경우 가감이 없다
   * @return yyyyMMdd 형식의 날짜 문자열
   * @throws IllegalArgumentException 날짜 포맷이 정해진 바와 다를 경우. 입력 값이 <code>null</code>인 경우.
   */
  public static String addDay(String dateStr, int day) {
    return addYearMonthDay(dateStr, 0, 0, day);
  }

  /**
   * <p>yyyyMMdd 혹은 yyyy-MM-dd 형식의 날짜 문자열 <code>dateStr1</code>과 <code>
   * dateStr2</code> 사이의 일 수를 구한다.<br>
   * <code>dateStr2</code>가 <code>dateStr1</code> 보다 과거 날짜일 경우에는
   * 음수를 반환한다. 동일한 경우에는 0을 반환한다.</p>
   *
   * <pre>
   * DateUtil.getDaysDiff("20060228","20060310") = 10
   * DateUtil.getDaysDiff("20060101","20070101") = 365
   * DateUtil.getDaysDiff("19990228","19990131") = -28
   * DateUtil.getDaysDiff("20060801","20060802") = 1
   * DateUtil.getDaysDiff("20060801","20060801") = 0
   * </pre>
   *
   * @param sDate1 날짜 문자열(yyyyMMdd, yyyy-MM-dd의 형식)
   * @param sDate2 날짜 문자열(yyyyMMdd, yyyy-MM-dd의 형식)
   * @return 일 수 차이.
   * @throws IllegalArgumentException 날짜 포맷이 정해진 바와 다를 경우. 입력 값이 <code>null</code>인 경우.
   */
  public static int getDaysDiff(String sDate1, String sDate2) {
    String dateStr1 = validChkDate(sDate1);
    String dateStr2 = validChkDate(sDate2);

    if (!checkDate(sDate1) || !checkDate(sDate2)) {
      throw new IllegalArgumentException(
          "Invalid date format: args[0]=" + sDate1 + " args[1]=" + sDate2);
    }
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());

    Date date1 = null;
    Date date2 = null;
    try {
      date1 = sdf.parse(dateStr1);
      date2 = sdf.parse(dateStr2);
    } catch (ParseException e) {
      throw new IllegalArgumentException(
          "Invalid date format: args[0]=" + dateStr1 + " args[1]=" + dateStr2);
    }
    int days1 = (int) ((date1.getTime() / 3600000) / 24);
    int days2 = (int) ((date2.getTime() / 3600000) / 24);

    return days2 - days1;
  }

  /**
   * <p>yyyyMMdd 혹은 yyyy-MM-dd 형식의 날짜 문자열을 입력 받아 유효한 날짜인지 검사.</p>
   *
   * <pre>
   * DateUtil.checkDate("1999-02-35") = false
   * DateUtil.checkDate("2000-13-31") = false
   * DateUtil.checkDate("2006-11-31") = false
   * DateUtil.checkDate("2006-2-28")  = false
   * DateUtil.checkDate("2006-2-8")   = false
   * DateUtil.checkDate("20060228")   = true
   * DateUtil.checkDate("2006-02-28") = true
   * </pre>
   *
   * @param sDate 날짜 문자열(yyyyMMdd, yyyy-MM-dd의 형식)
   * @return 유효한 날짜인지 여부
   */
  public static boolean checkDate(String sDate) {
    String dateStr = validChkDate(sDate);

    String year = dateStr.substring(0, 4);
    String month = dateStr.substring(4, 6);
    String day = dateStr.substring(6);

    return checkDate(year, month, day);
  }

  /**
   * <p>입력한 년, 월, 일이 유효한지 검사.</p>
   *
   * @param year  연도
   * @param month 월
   * @param day   일
   * @return 유효한 날짜인지 여부
   */
  public static boolean checkDate(String year, String month, String day) {
    try {
      SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd", Locale.getDefault());

      Date result = formatter.parse(year + "." + month + "." + day);
      String resultStr = formatter.format(result);
      if (resultStr.equalsIgnoreCase(year + "." + month + "." + day)) {
        return true;
      } else {
        return false;
      }
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * 날짜형태의 String의 날짜 포맷 및 TimeZone을 변경해 주는 메서드
   *
   * @param strSource      바꿀 날짜 String
   * @param fromDateFormat 기존의 날짜 형태
   * @param toDateFormat   원하는 날짜 형태
   * @param strTimeZone    변경할 TimeZone(""이면 변경 안함)
   * @return 소스 String의 날짜 포맷을 변경한 String
   */
  public static String convertDate(String strSource, String fromDateFormat, String toDateFormat,
      String strTimeZone) {
    SimpleDateFormat simpledateformat = null;
    Date date = null;
    String _fromDateFormat = "";
    String _toDateFormat = "";

    if (StringUtil.isNullToString(strSource).trim().equals("")) {
      return "";
    }
    if (StringUtil.isNullToString(fromDateFormat).trim().equals("")) {
      _fromDateFormat = "yyyyMMddHHmmss";                    // default값
    }
    if (StringUtil.isNullToString(toDateFormat).trim().equals("")) {
      _toDateFormat = "yyyy-MM-dd HH:mm:ss";                 // default값
    }

    try {
      simpledateformat = new SimpleDateFormat(_fromDateFormat, Locale.getDefault());
      date = simpledateformat.parse(strSource);
      if (!StringUtil.isNullToString(strTimeZone).trim().equals("")) {
        simpledateformat.setTimeZone(TimeZone.getTimeZone(strTimeZone));
      }
      simpledateformat = new SimpleDateFormat(_toDateFormat, Locale.getDefault());
    } catch (Exception exception) {
      exception.printStackTrace();
    }
    return simpledateformat.format(date);

  }


  /**
   * yyyyMMdd 형식의 날짜문자열을 원하는 캐릭터(ch)로 쪼개 돌려준다<br/>
   * <pre>
   * ex) 20030405, ch(.) -> 2003.04.05
   * ex) 200304, ch(.) -> 2003.04
   * ex) 20040101,ch(/) --> 2004/01/01 로 리턴
   * </pre>
   *
   * @param sDate yyyyMMdd 형식의 날짜문자열
   * @param ch    구분자
   * @return 변환된 문자열
   */
  public static String formatDate(String sDate, String ch) {
    String dateStr = validChkDate(sDate);

    String str = dateStr.trim();
    String yyyy = "";
    String mm = "";
    String dd = "";

    if (str.length() == 8) {
      yyyy = str.substring(0, 4);
      if (yyyy.equals("0000")) {
        return "";
      }

      mm = str.substring(4, 6);
      if (mm.equals("00")) {
        return yyyy;
      }

      dd = str.substring(6, 8);
      if (dd.equals("00")) {
        return yyyy + ch + mm;
      }

      return yyyy + ch + mm + ch + dd;
    } else if (str.length() == 6) {
      yyyy = str.substring(0, 4);
      if (yyyy.equals("0000")) {
        return "";
      }

      mm = str.substring(4, 6);
      if (mm.equals("00")) {
        return yyyy;
      }

      return yyyy + ch + mm;
    } else if (str.length() == 4) {
      yyyy = str.substring(0, 4);
      if (yyyy.equals("0000")) {
        return "";
      } else {
        return yyyy;
      }
    } else {
      return "";
    }
  }

  /**
   * HH24MISS 형식의 시간문자열을 원하는 캐릭터(ch)로 쪼개 돌려준다 <br>
   * <pre>
   *     ex) 151241, ch(/) -> 15/12/31
   * </pre>
   *
   * @param sTime HH24MISS 형식의 시간문자열
   * @param ch    구분자
   * @return 변환된 문자열
   */
  public static String formatTime(String sTime, String ch) {
    String timeStr = validChkTime(sTime);
    return timeStr.substring(0, 2) + ch + timeStr.substring(2, 4) + ch + timeStr.substring(4, 6);
  }

  /**
   * <p>입력받은 연도가 윤년인지 아닌지 검사한다.</p>
   *
   * <pre>
   * DateUtil.isLeapYear(2004) = false
   * DateUtil.isLeapYear(2005) = true
   * DateUtil.isLeapYear(2006) = true
   * </pre>
   *
   * @param year 연도
   * @return 윤년 여부
   */
  public static boolean isLeapYear(int year) {
    if (year % 4 == 0 && year % 100 != 0 || year % 400 == 0) {
      return false;
    }
    return true;
  }

  /**
   * 현재 날짜정보를 얻는다.                     <BR> 표기법은 yyyy-mm-dd                                  <BR>
   *
   * @return String      yyyymmdd형태의 현재시간.   <BR>
   */
  public static String getTodayString() {
    return getCurrentDate("");
  }

  /**
   * 현재 날짜정보를 얻는다.                     <BR> 표기법은 yyyy-mm-dd                                  <BR>
   *
   * @return String      yyyymmdd형태의 현재시간.   <BR>
   */
  public static String getCurrentDate(String dateType) {
    Calendar aCalendar = Calendar.getInstance();

    int year = aCalendar.get(Calendar.YEAR);
    int month = aCalendar.get(Calendar.MONTH) + 1;
    int date = aCalendar.get(Calendar.DATE);
    String strDate = Integer.toString(year) + ((month < 10) ? "0" + Integer.toString(month)
        : Integer.toString(month)) + ((date < 10) ? "0" + Integer.toString(date)
        : Integer.toString(date));

    if (!"".equals(dateType)) {
      strDate = convertDate(strDate, dateToString(new Date(), "MMdd", Locale.KOREA), dateType);
    }

    return strDate;
  }

  /**
   * format에 맞는 현재 날짜 및 시간을 리턴
   *
   * @param format yyyy-MM-dd HH:mm:ss
   * @return String
   */
  public static String getCurrentDateTime(String format) {
    return new SimpleDateFormat(format).format(new Date());
  }

  /**
   * 포맷에 맞는 문자열을 java.util.Date 형으로 변환한다.
   *
   * @param strDate
   * @param format
   * @return Date
   */
  public static Date stringToDate(String strDate, String format) {
    Date date = null;

    if (strDate == null) {
      return null;

    } else {
      try {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        date = formatter.parse(strDate);
      } catch (ParseException e) {
        return null;
      }
    }
    return date;
  }

  /**
   * yyyy/MM/dd 형태의 문자열을 java.util.Date 형으로 변환한다.
   *
   * @param strDate
   * @return Date
   */
  public static Date stringToDate(String strDate) {
    Date date = null;
    if (strDate != null && !"".equals(strDate)) {
      date = stringToDate(strDate, "yyyy-MM-dd");
    }
    return date;
  }

  /**
   * yyyy/MM/dd 형태의 문자열을 java.sql.Date 형으로 변환한다.
   *
   * @param strDate
   * @return java.sql.Date
   */
  public static java.sql.Date stringToSqlDate(String strDate) {
    return dateToSqlDate(stringToDate(strDate));
  }

  /**
   * java.util.Date 형의 데이터를 java.sql.Date형으로 변환한다.
   *
   * @param date
   * @return java.sql.Date
   */
  public static java.sql.Date dateToSqlDate(Date date) {

    java.sql.Date sqlDate = null;

    if (date != null) {

      sqlDate = new java.sql.Date(date.getTime());

    }
    return sqlDate;
  }

  /**
   * 날짜형태의 String의 날짜 포맷만을 변경해 주는 메서드
   *
   * @param sDate      날짜
   * @param sTime      시간
   * @param sFormatStr 포멧 스트링 문자열
   * @return 지정한 날짜/시간을 지정한 포맷으로 출력
   * @See Letter  Date or Time Component  Presentation  Examples G  Era designator  Text  AD y  Year Year  1996; 96 M
   * Month in year  Month  July; Jul; 07 w  Week in year  Number  27 W  Week in month  Number  2 D  Day in year  Number
   * 189 d  Day in month  Number  10 F  Day of week in month  Number  2 E  Day in week  Text  Tuesday; Tue a  Am/pm
   * marker  Text  PM H  Hour in day (0-23)  Number  0 k  Hour in day (1-24)  Number  24 K  Hour in am/pm (0-11)  Number
   *  0 h  Hour in am/pm (1-12)  Number  12 m  Minute in hour  Number  30 s  Second in minute  Number  55 S Millisecond
   * Number  978 z  Time zone  General time zone  Pacific Standard Time; PST; GMT-08:00 Z  Time zone  RFC 822 time zone
   * -0800
   * <p>
   * <p>
   * <p>
   * Date and Time Pattern  Result "yyyy.MM.dd G 'at' HH:mm:ss z"  2001.07.04 AD at 12:08:56 PDT "EEE, MMM d, ''yy"
   * Wed, Jul 4, '01 "h:mm a"  12:08 PM "hh 'o''clock' a, zzzz"  12 o'clock PM, Pacific Daylight Time "K:mm a, z"  0:08
   * PM, PDT "yyyyy.MMMMM.dd GGG hh:mm aaa"  02001.July.04 AD 12:08 PM "EEE, d MMM yyyy HH:mm:ss Z"  Wed, 4 Jul 2001
   * 12:08:56 -0700 "yyMMddHHmmssZ" 010704120856-0700
   */
  public static String convertDate(String sDate, String sTime, String sFormatStr) {
    String dateStr = validChkDate(sDate);
    String timeStr = validChkTime(sTime);

    Calendar cal = null;
    cal = Calendar.getInstance();

    cal.set(Calendar.YEAR, Integer.parseInt(dateStr.substring(0, 4)));
    cal.set(Calendar.MONTH, Integer.parseInt(dateStr.substring(4, 6)) - 1);
    cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateStr.substring(6, 8)));
    cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeStr.substring(0, 2)));
    cal.set(Calendar.MINUTE, Integer.parseInt(timeStr.substring(2, 4)));

    SimpleDateFormat sdf = new SimpleDateFormat(sFormatStr, Locale.ENGLISH);

    return sdf.format(cal.getTime());
  }

  /**
   * 입력받은 일자 사이의 임의의 일자를 반환
   *
   * @param sDate1 시작일자
   * @param sDate2 종료일자
   * @return 임의일자
   */
  public static String getRandomDate(String sDate1, String sDate2) {
    String dateStr1 = validChkDate(sDate1);
    String dateStr2 = validChkDate(sDate2);

    String randomDate = null;

    int sYear, sMonth, sDay;
    int eYear, eMonth, eDay;

    sYear = Integer.parseInt(dateStr1.substring(0, 4));
    sMonth = Integer.parseInt(dateStr1.substring(4, 6));
    sDay = Integer.parseInt(dateStr1.substring(6, 8));

    eYear = Integer.parseInt(dateStr2.substring(0, 4));
    eMonth = Integer.parseInt(dateStr2.substring(4, 6));
    eDay = Integer.parseInt(dateStr2.substring(6, 8));

    GregorianCalendar beginDate = new GregorianCalendar(sYear, sMonth - 1, sDay, 0, 0);
    GregorianCalendar endDate = new GregorianCalendar(eYear, eMonth - 1, eDay, 23, 59);

    if (endDate.getTimeInMillis() < beginDate.getTimeInMillis()) {
      throw new IllegalArgumentException("Invalid input date : " + sDate1 + "~" + sDate2);
    }

    SecureRandom r = new SecureRandom();

    long rand =
        ((r.nextLong() >>> 1) % (endDate.getTimeInMillis() - beginDate.getTimeInMillis() + 1))
            + beginDate.getTimeInMillis();

    GregorianCalendar cal = new GregorianCalendar();
    //SimpleDateFormat calformat = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat calformat = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);
    cal.setTimeInMillis(rand);
    randomDate = calformat.format(cal.getTime());

    // 랜덤문자열를 리턴
    return randomDate;
  }

  /**
   * 입력받은 요일의 영문명을 국문명의 요일로 반환
   *
   * @param sWeek 영문 요일명
   * @return 국문 요일명
   */
  public static String convertWeek(String sWeek) {
    String retStr = null;

    if (sWeek.equals("SUN")) {
      retStr = "일요일";
    } else if (sWeek.equals("MON")) {
      retStr = "월요일";
    } else if (sWeek.equals("TUE")) {
      retStr = "화요일";
    } else if (sWeek.equals("WED")) {
      retStr = "수요일";
    } else if (sWeek.equals("THR")) {
      retStr = "목요일";
    } else if (sWeek.equals("FRI")) {
      retStr = "금요일";
    } else if (sWeek.equals("SAT")) {
      retStr = "토요일";
    }

    return retStr;
  }

  /**
   * 입력일자의 유효 여부를 확인
   *
   * @param sDate 일자
   * @return 유효 여부
   */
  public static boolean validDate(String sDate) {
    String dateStr = validChkDate(sDate);

    Calendar cal;
    boolean ret = false;

    cal = Calendar.getInstance();

    cal.set(Calendar.YEAR, Integer.parseInt(dateStr.substring(0, 4)));
    cal.set(Calendar.MONTH, Integer.parseInt(dateStr.substring(4, 6)) - 1);
    cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateStr.substring(6, 8)));

    String year = String.valueOf(cal.get(Calendar.YEAR));
    String month = String.valueOf(cal.get(Calendar.MONTH) + 1);
    String day = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));

    String pad4Str = "0000";
    String pad2Str = "00";

    String retYear = (pad4Str + year).substring(year.length());
    String retMonth = (pad2Str + month).substring(month.length());
    String retDay = (pad2Str + day).substring(day.length());

    String retYMD = retYear + retMonth + retDay;

    if (sDate.equals(retYMD)) {
      ret = true;
    }

    return ret;
  }

  /**
   * 입력일자, 요일의 유효 여부를 확인
   *
   * @param sDate 일자
   * @param sWeek 요일 (DAY_OF_WEEK)
   * @return 유효 여부
   */
  public static boolean validDate(String sDate, int sWeek) {
    String dateStr = validChkDate(sDate);

    Calendar cal;
    boolean ret = false;

    cal = Calendar.getInstance();

    cal.set(Calendar.YEAR, Integer.parseInt(dateStr.substring(0, 4)));
    cal.set(Calendar.MONTH, Integer.parseInt(dateStr.substring(4, 6)) - 1);
    cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateStr.substring(6, 8)));

    int Week = cal.get(Calendar.DAY_OF_WEEK);

    if (validDate(sDate)) {
      if (sWeek == Week) {
        ret = true;
      }
    }

    return ret;
  }

  /**
   * 입력시간의 유효 여부를 확인
   *
   * @param sTime 입력시간
   * @return 유효 여부
   */
  public static boolean validTime(String sTime) {
    String timeStr = validChkTime(sTime);

    Calendar cal;
    boolean ret = false;

    cal = Calendar.getInstance();

    cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeStr.substring(0, 2)));
    cal.set(Calendar.MINUTE, Integer.parseInt(timeStr.substring(2, 4)));

    String HH = String.valueOf(cal.get(Calendar.HOUR_OF_DAY));
    String MM = String.valueOf(cal.get(Calendar.MINUTE));

    String pad2Str = "00";

    String retHH = (pad2Str + HH).substring(HH.length());
    String retMM = (pad2Str + MM).substring(MM.length());

    String retTime = retHH + retMM;

    if (sTime.equals(retTime)) {
      ret = true;
    }

    return ret;
  }

  /**
   * 입력된 일자에 연, 월, 일을 가감한 날짜의 요일을 반환
   *
   * @param sDate 날짜
   * @param year  연
   * @param month 월
   * @param day   일
   * @return 계산된 일자의 요일(DAY_OF_WEEK)
   */
  public static String addYMDtoWeek(String sDate, int year, int month, int day) {
    String dateStr = validChkDate(sDate);

    dateStr = addYearMonthDay(dateStr, year, month, day);

    Calendar cal = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);
    try {
      cal.setTime(sdf.parse(dateStr));
    } catch (ParseException e) {
      throw new IllegalArgumentException("Invalid date format: " + dateStr);
    }

    SimpleDateFormat rsdf = new SimpleDateFormat("E", Locale.ENGLISH);

    return rsdf.format(cal.getTime());
  }

  /**
   * 입력된 일자에 연, 월, 일, 시간, 분을 가감한 날짜, 시간을 포멧스트링 형식으로 반환
   *
   * @param sDate     날짜
   * @param sTime     시간
   * @param year      연
   * @param month     월
   * @param day       일
   * @param hour      시간
   * @param minute    분
   * @param formatStr 포멧스트링
   * @return 포멧스트링형식으로 변환된 문자열
   */
  public static String addYMDtoDayTime(String sDate, String sTime, int year, int month, int day,
      int hour, int minute, String formatStr) {
    String dateStr = validChkDate(sDate);
    String timeStr = validChkTime(sTime);

    dateStr = addYearMonthDay(dateStr, year, month, day);

    dateStr = convertDate(dateStr, timeStr, "yyyyMMddHHmm");

    Calendar cal = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm", Locale.ENGLISH);

    try {
      cal.setTime(sdf.parse(dateStr));
    } catch (ParseException e) {
      throw new IllegalArgumentException("Invalid date format: " + dateStr);
    }

    if (hour != 0) {
      cal.add(Calendar.HOUR, hour);
    }

    if (minute != 0) {
      cal.add(Calendar.MINUTE, minute);
    }

    SimpleDateFormat rsdf = new SimpleDateFormat(formatStr, Locale.ENGLISH);

    return rsdf.format(cal.getTime());
  }

  /**
   * 입력된 일자를 int 형으로 반환
   *
   * @param sDate 일자
   * @return int(일자)
   */
  public static int datetoInt(String sDate) {
    return Integer.parseInt(convertDate(sDate, "0000", "yyyyMMdd"));
  }

  /**
   * 입력된 시간을 int 형으로 반환
   *
   * @param sTime 시간
   * @return int(시간)
   */
  public static int timetoInt(String sTime) {
    return Integer.parseInt(convertDate("00000101", sTime, "HHmm"));
  }

  /**
   * 입력된 일자 문자열을 확인하고 8자리로 리턴
   *
   * @param dateStr
   * @return 8자리 문자열
   */
  public static String validChkDate(String dateStr) {
    String _dateStr = dateStr;

    if (dateStr == null || !(dateStr.trim().length() == 8 || dateStr.trim().length() == 10)) {
      throw new IllegalArgumentException("Invalid date format: " + dateStr);
    }
    if (dateStr.length() == 10) {
      _dateStr = StringUtil.removeMinusChar(dateStr);
    }
    return _dateStr;
  }

  /**
   * 입력된 일자 문자열을 확인하고 8자리로 리턴
   *
   * @param timeStr
   * @return 8자리 문자열
   */
  public static String validChkTime(String timeStr) {
    String _timeStr = timeStr;

    if (_timeStr.length() == 5) {
      _timeStr = StringUtil.remove(_timeStr, ':');
    }
    if (_timeStr == null || !(_timeStr.trim().length() == 4)) {
      throw new IllegalArgumentException("Invalid time format: " + _timeStr);
    }

    return _timeStr;
  }

  /**
   * 날짜가 며칠 지났는지 일자를 구한다.
   *
   * @param frDate yyyyMMdd
   * @param toDate yyyyMMdd
   * @return daliy Count
   */
  public static long daliyCount(String frDate, String toDate) {
    long count = 0;

    if (frDate.length() < 8 || toDate.length() < 8) {
      return 0;
    }

    Calendar cal = Calendar.getInstance();
    cal.set(NumberUtil.stringToInt(toDate.substring(0, 4)),
        NumberUtil.stringToInt(toDate.substring(6, 8)));

    Calendar cl = Calendar.getInstance();
    cl.set(NumberUtil.stringToInt(frDate.substring(0, 4)),
        NumberUtil.stringToInt(frDate.substring(4, 6)),
        NumberUtil.stringToInt(frDate.substring(6, 8)));

    while (!cl.after(cal)) {
      count++;
      cl.add(Calendar.DATE, 1);
    }

    return count;
  }

  /**
   * 날짜가 몇개월 지났는지 일자를 구한다.
   *
   * @param frDate yyyyMM
   * @param toDate yyyyMM
   * @return daliy Count
   */
  public static long monthlyCount(String frDate, String toDate) {
    long count = 0;

    if (frDate.length() < 6 || toDate.length() < 6) {
      return 0;
    }

    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.YEAR, NumberUtil.stringToInt(toDate.substring(0, 4)));
    cal.set(Calendar.MONTH, NumberUtil.stringToInt(toDate.substring(4, 6)));

    Calendar cl = Calendar.getInstance();
    cl.set(Calendar.YEAR, NumberUtil.stringToInt(frDate.substring(0, 4)));
    cl.set(Calendar.MONTH, NumberUtil.stringToInt(frDate.substring(4, 6)));

    while (!cl.after(cal)) {
      count++;
      cl.add(Calendar.MONTH, 1);
    }

    return count;
  }

  /**
   * 공통코드 함수 현재년도 기준으로 마이너스 count 값 까지 돌려서 년도 리스트를 반환한다. 기준년도인 1973년 미만으로는 값을 가져오지 않는다.
   *
   * @param minusCount 마이너스 할 년도 값
   * @param plusCount  플러스 할 년도 값
   * @param lok
   * @return
   */
  public static List<Map<String, Object>> getYearList(int minusCount, int plusCount, Locale lok) {
    if (lok == null) {
      lok = Locale.KOREA;
    }

    Calendar calendar = new GregorianCalendar(lok);
    Map<String, Object> yearMap = new HashMap<String, Object>();
    List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();

    int nYear = calendar.get(Calendar.YEAR);
    int tmpYear = nYear + plusCount;
    int tmpMinusCnt = minusCount + plusCount;

    if (minusCount == 0) {
      //minusCount 값이 0이면 기준년도 1973년 까지 가져온다.
      while (tmpYear > 1972) {
        yearMap = new HashMap<String, Object>();
        yearMap.put("CD", tmpYear);
        yearMap.put("CD_NM", tmpYear);

        dataList.add(yearMap);
        tmpYear -= 1;
      }
    } else {
      //minusCount 값이 0이 아니면 추가된 년도부터 현재년도 기준 마이너스 년도까지 합쳐진 데이터가 반환된다.
      for (int i = 0; i < tmpMinusCnt; i++) {
        yearMap = new HashMap<String, Object>();
        yearMap.put("CD", tmpYear);
        yearMap.put("CD_NM", tmpYear);

        dataList.add(yearMap);
        tmpYear -= 1;
        //마이너스 년도 값이 크더라도 1973년 이하로 가져오지 않는다.
        if (tmpYear == 1972) {
          break;
        }
      }
    }

    return dataList;
  }

  /**
   * 연도를 입력 받아 해당 연도 2월의 말일(일수)를 문자열로 반환한다.
   *
   * @param year
   * @return 해당 연도 2월의 말일(일수)
   */
  public String leapYear(int year) {
    if (year % 4 == 0 && year % 100 != 0 || year % 400 == 0) {
      return "29";
    }

    return "28";
  }

  /**
   * 요일에 대한 int를 리턴 0=일요일,1=월요일,2=화요일,3=수요일,4=목요일,5=금요일,6=토요일
   *
   * @param year
   * @param month
   * @param day
   * @return String
   */
  public int getWeekDay(int year, int month, int day) {
    Calendar cal = Calendar.getInstance();
    cal.set(year, month - 1, day);
    return cal.get(Calendar.DAY_OF_WEEK) - 1;
  }
}
