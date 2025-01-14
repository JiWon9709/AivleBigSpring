package kt.aivle.util;

import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @version 1.0
 * @ClassName : NumberUtil.java
 * @Description : 숫자 데이터 처리 관련 유틸리티(Framework - utility)
 * @Modification Information
 * <pre>
 *     since          author              description
 *  ===========    =============    ===========================
 *  2016. 6. 29.                      	최초 생성
 * </pre>
 * @see
 * @since 2016. 6. 29.
 */
public class NumberUtil {

  /**
   * 특정숫자 집합에서 랜덤 숫자를 구하는 기능 시작숫자와 종료숫자 사이에서 구한 랜덤 숫자를 반환한다
   *
   * @param startNum - 시작숫자
   * @param endNum   - 종료숫자
   * @return 랜덤숫자
   */
  public static int getRandomNum(int startNum, int endNum) {
    int randomNum = 0;

    try {
      // 랜덤 객체 생성
      SecureRandom rnd = new SecureRandom();

      do {
        // 종료숫자내에서 랜덤 숫자를 발생시킨다.
        randomNum = rnd.nextInt(endNum + 1);
      } while (randomNum < startNum); // 랜덤 숫자가 시작숫자보다 작을경우 다시 랜덤숫자를 발생시킨다.
    } catch (Exception e) {
      e.printStackTrace();
    }

    return randomNum;
  }

  /**
   * 특정 숫자 집합에서 특정 숫자가 있는지 체크하는 기능 12345678에서 7이 있는지 없는지 체크하는 기능을 제공함
   *
   * @param sourceInt - 특정숫자집합
   * @param searchInt - 검색숫자
   * @return 존재여부
   */
  public static Boolean getNumSearchCheck(int sourceInt, int searchInt) {
    String sourceStr = String.valueOf(sourceInt);
    String searchStr = String.valueOf(searchInt);

    // 특정숫자가 존재하는지 하여 위치값을 리턴한다. 없을 시 -1
    if (sourceStr.indexOf(searchStr) == -1) {
      return false;
    } else {
      return true;
    }
  }

  /**
   * 숫자타입을 문자열로 변환하는 기능 숫자 20081212를 문자열 '20081212'로 변환하는 기능
   *
   * @param srcNumber - 숫자
   * @return 문자열
   */
  public static String getNumToStrCnvr(int srcNumber) {
    String rtnStr = null;

    try {
      rtnStr = String.valueOf(srcNumber);
    } catch (Exception e) {
      e.printStackTrace();
    }

    return rtnStr;
  }


  /**
   * 숫자타입을 데이트 타입으로 변환하는 기능 숫자 20081212를 데이트타입  '2008-12-12'로 변환하는 기능
   *
   * @param srcNumber - 숫자
   * @return String
   */
  public static String getNumToDateCnvr(int srcNumber) {

    String pattern = null;
    String cnvrStr = null;

    String srcStr = String.valueOf(srcNumber);

    // Date 형태인 8자리 및 14자리만 정상처리
    if (srcStr.length() != 8 && srcStr.length() != 14) {
      throw new IllegalArgumentException(
          "Invalid Number: " + srcStr + " Length=" + srcStr.trim().length());
    }

    if (srcStr.length() == 8) {
      pattern = "yyyyMMdd";
    } else if (srcStr.length() == 14) {
      pattern = "yyyyMMddhhmmss";
    }

    SimpleDateFormat dateFormatter = new SimpleDateFormat(pattern, Locale.KOREA);

    Date cnvrDate = null;

    try {
      cnvrDate = dateFormatter.parse(srcStr);
    } catch (ParseException e) {
      e.printStackTrace();
    }

    cnvrStr = String.format("%1$tY-%1$tm-%1$td", cnvrDate);

    return cnvrStr;

  }

  /**
   * 체크할 숫자 중에서 숫자인지 아닌지 체크하는 기능 숫자이면 True, 아니면 False를 반환한다
   *
   * @param checkStr - 체크문자열
   * @return 숫자여부
   */
  public static Boolean getNumberValidCheck(String checkStr) {

    int i;
    //String sourceStr = String.valueOf(sourceInt);

    int checkStrLt = checkStr.length();

    try {
      if(checkStrLt == 0)
        return false;
      for (i = 0; i < checkStrLt; i++) {
        // 아스키코드값( '0'-> 48, '9' -> 57)
        if (checkStr.charAt(i) > 47 && checkStr.charAt(i) < 58) {
          continue;
        } else {
          return false;
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }

    return true;
  }

  /**
   * 특정숫자를 다른 숫자로 치환하는 기능 숫자 12345678에서 123를 999로 변환하는 기능을 제공(99945678)
   *
   * @param srcNumber      - 숫자집합
   * @param cnvrSrcNumber  - 원래숫자
   * @param cnvrTrgtNumber - 치환숫자
   * @return 치환숫자
   */

  public static int getNumberCnvr(int srcNumber, int cnvrSrcNumber, int cnvrTrgtNumber) {

    // 입력받은 숫자를 문자열로 변환
    String source = String.valueOf(srcNumber);
    String subject = String.valueOf(cnvrSrcNumber);
    String object = String.valueOf(cnvrTrgtNumber);

    StringBuffer rtnStr = new StringBuffer();
    String preStr = "";
    String nextStr = source;

    try {

      // 원본숫자에서 변환대상숫자의 위치를  찾는다.
      while (source.indexOf(subject) >= 0) {
        preStr = source.substring(0, source.indexOf(subject)); // 변환대상숫자 위치까지 숫자를 잘라낸다
        nextStr = source.substring(source.indexOf(subject) + subject.length(), source.length());
        source = nextStr;
        rtnStr.append(preStr).append(object); // 변환대상위치 숫자에 변환할 숫자를 붙여준다.
      }
      rtnStr.append(nextStr); // 변환대상 숫자 이후 숫자를 붙여준다.
    } catch (Exception e) {
      e.printStackTrace();
    }

    return Integer.parseInt(rtnStr.toString());
  }

  /**
   * 특정숫자가 실수인지, 정수인지, 음수인지 체크하는 기능 123이 실수인지, 정수인지, 음수인지 체크하는 기능을 제공함
   *
   * @param srcNumber - 숫자집합
   * @return -1(음수), 0(정수), 1(실수)
   */
  public static int checkRlnoInteger(double srcNumber) {

    // byte 1바이트 		▶소수점이 없는 숫자로, 범위 -2^7 ~ 2^7 -1
    // short 2바이트		▶소수점이 없는 숫자로, 범위 -2^15 ~ 2^15 -1
    // int 4바이트 		▶소수점이 없는 숫자로, 범위 -2^31 ~ 2^31 - 1
    // long 8바이트 		▶소수점이 없는 숫자로, 범위 -2^63 ~ 2^63-1

    // float 4바이트		▶소수점이 있는 숫자로, 끝에 F 또는 f 가 붙는 숫자 (예:3.14f)
    // double 8바이트	▶소수점이 있는 숫자로, 끝에 아무것도 붙지 않는 숫자 (예:3.14)
    //			▶소수점이 있는 숫자로, 끝에 D 또는 d 가 붙는 숫자(예:3.14d)

    String cnvrString = null;

    if (srcNumber < 0) {
      return -1;
    } else {
      cnvrString = String.valueOf(srcNumber);

      if (cnvrString.indexOf(".") == -1) {
        return 0;
      } else {
        return 1;
      }
    }
  }

  /**
   * String을 Int형으로 변환
   *
   * @param str 문자열
   * @return int  리턴값
   */
  public static int stringToInt(String str) {
    if (str == null || str.equals("") || str.trim().length() < 1) {
      return 0;
    } else {
      return Integer.parseInt(str.trim());
    }
  }

  /**
   * String을 Int형으로 변환
   *
   * @param str 문자열
   * @return int  리턴값
   */
  public static int stringToInt(String str, int defaultValue) {
    if (str == null || str.equals("") || str.trim().length() < 1) {
      return defaultValue;
    } else {
      return Integer.parseInt(str.trim());
    }
  }

  /**
   * String을 Int형으로 변환
   *
   * @param str 문자열
   * @return int  리턴값
   */
  public static double stringToDouble(String str) {
    if (str == null || str.equals("") || str.trim().length() < 1) {
      return 0;
    } else {
      return Double.parseDouble(str.trim());
    }
  }

  /**
   * String을 Int형으로 변환
   *
   * @param str 문자열
   * @return int  리턴값
   */
  public static double stringToDouble(String str, int defaultValue) {
    if (str == null || str.equals("") || str.trim().length() < 1) {
      return defaultValue;
    } else {
      return Double.parseDouble(str.trim());
    }
  }

  /**
   * 두 지점간의 거리 계산
   *
   * @param lat1 지점 1 위도
   * @param lon1 지점 1 경도
   * @param lat2 지점 2 위도
   * @param lon2 지점 2 경도
   * @param unit 거리 표출단위
   * @return
   */
  public static double distance(double lat1, double lon1, double lat2, double lon2, String unit) {

    double theta = lon1 - lon2;
    double dist =
        Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(
            deg2rad(lat2)) * Math.cos(deg2rad(theta));

    dist = Math.acos(dist);
    dist = rad2deg(dist);
    dist = dist * 60 * 1.1515;

    if (unit == "kilometer") {
      dist = dist * 1.609344;
    } else if (unit == "meter") {
      dist = dist * 1609.344;
    }

    return (dist);
  }

  public static double CalculationByDistance(double initialLat, double initialLong,
      double finalLat, double finalLong) {
    int R = 6371; // km (Earth radius)
    double dLat = toRadians(finalLat - initialLat);
    double dLon = toRadians(finalLong - initialLong);
    initialLat = toRadians(initialLat);
    finalLat = toRadians(finalLat);

    double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
        Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(initialLat) * Math.cos(finalLat);
    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    return R * c;
  }

  public static double CalculationByDistanceKm(double lat1, double lng1, double lat2, double lng2) {
    double earthRadius = 6371000; //meters
    double dLat = Math.toRadians(lat2 - lat1);
    double dLng = Math.toRadians(lng2 - lng1);
    double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
        Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
            Math.sin(dLng / 2) * Math.sin(dLng / 2);
    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    float dist = (float) (earthRadius * c);

    return dist;
  }

  public static double toRadians(double deg) {
    return deg * (Math.PI / 180);
  }

  public static double getKilometers(double lat1, double long1, double lat2, double long2) {
    double PI_RAD = Math.PI / 180.0;
    double phi1 = lat1 * PI_RAD;
    double phi2 = lat2 * PI_RAD;
    double lam1 = long1 * PI_RAD;
    double lam2 = long2 * PI_RAD;
    return 6371.01 * Math.acos(
        Math.sin(phi1) * Math.sin(phi2) + Math.cos(phi1) * Math.cos(phi2) * Math.cos(lam2 - lam1));
  }

  // This function converts decimal degrees to radians
  public static double deg2rad(double deg) {
    return (deg * Math.PI / 180.0);
  }

  // This function converts radians to decimal degrees
  public static double rad2deg(double rad) {
    return (rad * 180 / Math.PI);
  }


  public static double directDistance(double lat1, double lng1, double lat2, double lng2) {

    double earthRadius = 3958.75;
    double dLat = ToRadians(lat2 - lat1);
    double dLng = ToRadians(lng2 - lng1);
    double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
        Math.cos(ToRadians(lat1)) * Math.cos(ToRadians(lat2)) *
            Math.sin(dLng / 2) * Math.sin(dLng / 2);
    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    double dist = earthRadius * c;
    double meterConversion = 1609.00;
    return dist * meterConversion;

  }

  public static double ToRadians(double degrees) {
    double radians = degrees * 3.1415926535897932385 / 180;
    return radians;
  }
}
