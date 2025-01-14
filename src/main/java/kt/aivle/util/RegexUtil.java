package kt.aivle.util;

import java.util.regex.Pattern;
import org.springframework.stereotype.Service;

/**
 * 정규식을 이용하여 유효성 검사 결과 리턴
 */
@Service
public class RegexUtil {

  /**
   * 정규식을 이용한 유효성 검사
   *
   * @param type 타입
   * @param val  문자열
   * @return
   */
  public static Boolean validate(String type, String val) {
    Boolean ret = false;
    String pattern = null;

    if(val == null)
      return false;

    switch (type) {
      case "NUM": // 숫자
        pattern = "^\\d*$";
        break;
      case "ALPHA": // 영문자
        pattern = "^[a-zA-Z]*$";
        break;
      case "ALPHA_NUM": // 영문숫자
        pattern = "^[a-zA-Z0-9]*$";
        break;
      case "LOW_ALPHA_NUM": // 영문소문자+숫자
        pattern = "^[a-z0-9]+$";
        break;
      case "HANGEUL": // 한글
        pattern = "^[가-힣]*$";
        break;
      case "TEL_NO": // 전화번호
        pattern = "^\\d{2,3}\\d{3,4}\\d{4}$";
        break;
      case "PHONE_NO": // 휴대전화번호
        pattern = "^01([0|1|6|7|8|9])([0-9]{3,4})([0-9]{4})$";
        break;
      case "JUMIN_NO": // 주민등록번호
        pattern = "\\d{6}-[1-4]\\d{6}";
        break;
      case "JUMIN_NO_ND": // 주민등록번호
        pattern = "\\d{6}[1-4]\\d{6}";
        break;
      case "ZIP_NO": // 우편번호
        pattern = "^\\d{3}\\d{2}$";
        break;
      case "BR_NO": // 사업자등록번호
        pattern = "^\\d{3}-\\d{2}-\\d{5}$";
        break;
      case "BR_NO_ND": // 사업자등록번호
        pattern = "^\\d{3}\\d{2}\\d{5}$";
        break;
      case "CR_NO": // 법인등록번호
        pattern = "^\\d{6}-\\d{7}$";
        break;
      case "EMAIL": // 이메일
        pattern = "^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$";
        break;
      case "PASSWORD": // 비밀번호 : 영문/숫자/특수문자 조합 2가지 이상 8~16자리
        pattern = "^(?!((?:[A-Za-z]+)|(?:[~!@#$%^&*()_+=]+)|(?:[0-9]+))$)[A-Za-z\\d~!@#$%^&*()_+=]{8,16}$";
        break;
      case "KOREAN_NUM_ALPHA": // 한글, 영어, 숫자만
        pattern = "^[a-zA-Z0-9\\p{IsHangul}\\s]+$";
        break;
      case "HTML": // html 태그
        pattern = "<[^>]+>";
        break;
      case "SIZE": // 사이즈양식
        pattern = "(\\d+)x(\\d+)x(\\d+)";
        break;
      default:
        return false;
    }

    ret = Pattern.matches(pattern, val);

    return ret;
  }

}
