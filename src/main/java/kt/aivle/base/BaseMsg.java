package kt.aivle.base;

/**
 * Response ResultCode 용
 * */
public enum BaseMsg {

  SUCCESS(0, "정상 처리 되었습니다."),
  FAILED(-999, "오류가 발생하였습니다.");

  private int code;
  private String value;

  BaseMsg(int code, String value) {
    this.code = code;
    this.value = value;
  }

  public int getCode() {
    return this.code;

  }

  public String getValue() {
    return this.value;
  }
}
