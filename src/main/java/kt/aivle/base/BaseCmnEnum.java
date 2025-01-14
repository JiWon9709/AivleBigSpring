package kt.aivle.base;

import java.util.Arrays;
import java.util.List;

/**
 * 공통 enum 관리
 * <p>
 * TODO 분류코드가 추가되는 경우 활용
 */
public enum BaseCmnEnum {
  YN("예아니오", Arrays.asList("Y", "N")),
  AUTH_CLS("권한등급", Arrays.asList("AD", "SL", "SP", "LG", "DL")),
  CO_CLS("회사분류", Arrays.asList("P", "C")),
  TRMS_TYPE("검색유형", Arrays.asList("M", "P")),
  AUTH_MTHD_TYPE("인증방법유형", Arrays.asList("S", "E")),
  AUTH_CTGR_TYPE("인증카테고리유형", Arrays.asList("I", "P", "J")),
  SEARCH_TYPE_0("검색유형", Arrays.asList("C", "I", "N", "T", "E")),
  SEARCH_TYPE("검색유형", Arrays.asList("A", "T", "C", "U")),
  DSCSN_STTS_tYPE("상담상태유형", Arrays.asList("W"/*상담전*/, "I"/*상담중*/, "C"/*상담완료*/)),
  SVC_FEE_TYPE("서비스요금유형", Arrays.asList("SLM", "ERP", "DLM", "ETP","LGM"));


  private String title;
  private List<String> list;

  BaseCmnEnum(String title, List<String> list) {
    this.title = title;
    this.list = list;
  }

  public String getTitle() {
    return this.title;
  }

  public String getList() {
    return this.list.toString();
  }

  /**
   * 문자열이 해당 enum 타입 값에 존재하는지 유효성 검사
   *
   * @param str
   * @return
   */
  public Boolean validate(String str) {
    return this.getList().contains(str);
  }

}
