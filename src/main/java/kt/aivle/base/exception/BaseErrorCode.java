package kt.aivle.base.exception;

import kt.aivle.util.StringUtil;

/**
 * 에러코드 관리용 enum : BaseMsg.FAILED 인 경우 활용
 * <p>
 * -1xx : 파라미터 관련 -2xx : DB 관련 -4xx : HttpStatus 관련 -9xx : 시스템 관련
 */
public enum BaseErrorCode {

  SUCCESS(0, "정상 처리 되었습니다."),
  ERROR_PARAMETER_VALID(-100, "파라메터 유효성 에러."),
  ERROR_PARAM_REQUIRED(-101, "필수 파라미터 에러."),
  ERROR_PARAM_TYPE(-102, "파라미터 타입 에러."),
  ERROR_PARAM_LENGTH(-103, "파라미터 길이 에러."),
  ERROR_PARAM_FILE_EXT(-104, "파일 확장자 에러."),
  ERROR_PARAM_FILE_SIZE(-105, "파일 사이즈 에러."),
  ERROR_SELECT_NULL(-106, "조회 NULL 에러."),
  ERROR_SELECT_DOUBLE(-107, "중복값 에러."),
  ERROR_DB_PK(-201, "Primary Key 에러."),
  ERROR_DB_UK(-202, "Unique Key 에러."),
  ERROR_DB_SELECT(-210, "DB SELECT 에러."),
  ERROR_DB_INSERT(-211, "DB INSERT 에러."),
  ERROR_DB_UPDATE(-212, "DB UPDATE 에러."),
  ERROR_DB_DELETE(-213, "DB DELETE 에러."),
  ERROR_DB_UPSERT(-214, "DB UPSERT 에러."),
  ERROR_STATUS_BAD_REQUEST(-400, "400 Bad Request."),
  ERROR_STATUS_UNAUTHORIZED(-401, "401 Unauthorized."),
  ERROR_STATUS_FORBIDDEN(-403, "403 Forbidden."),
  ERROR_STATUS_NOT_FOUND(-404, "404 Not Found."),
  ERROR_STATUS_NOT_ACCEPTABLE(-406, "406 Not Acceptable."),
  ERROR_DB_CONNECT(-997, "DB 접속 에러."),
  ERROR_SYSTEM(-996, "시스템 에러."),
  ERROR_UNKNOWN(-998, "알 수 없는 에러."),
  ERROR_ETC(-999, "기타 에러."),
  ERROR_CP_CHK(-9825, "발송처리 시 쿠팡주문 확인됨 에러."),
  ERROR_ORDR_NOT_MATCHED(-615, "주문 선택 에러."),
  ERROR_EXCEL_VALUE(-620, "엑셀 입력값 에러.");


  private int code;
  private String value;

  BaseErrorCode(int code, String value) {
    this.code = code;
    this.value = value;
  }

  public int getCode() {
    return this.code;
  }

  public String getValue() {
    return this.value;
  }

  /**
   * code 값에 해당하는 value 를 포함하여 문자열 생성 - 에러 상세 내용을 출력하고자 하는 경우 활용 ex) BaseErrorCode.ERROR_PARAM_REQUIRED.mergeValue("
   * [userNm]")
   *
   * @param str
   * @return
   */
  public String mergeValue(String str) {
    return this.value + (StringUtil.isEmpty(str) ? "" : str);
  }
}
