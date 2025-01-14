package kt.aivle.clnt_co.model;

import io.swagger.annotations.ApiModelProperty;
import kt.aivle.base.BaseModel;
import kt.aivle.base.BaseResListModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ClntCoListResponse {
  private static final long serialVersionUID = 1L;

  /**
   * 의뢰사고유번호
   */
  @ApiModelProperty(notes = "의뢰사고유번호", position = 1)
  private Long clntCoSn;

  /**
   * 의뢰사명
   */
  @ApiModelProperty(notes = "의뢰사명", position = 2)
  private String clntNm;

  /**
   * 회사코드
   */
  @ApiModelProperty(notes = "회사코드", position = 3)
  private String coCd;

  /**
   * 우편번호
   */
  @ApiModelProperty(notes = "우편번호", position = 4)
  private String zip;

  /**
   * 주소
   */
  @ApiModelProperty(notes = "주소", position = 5)
  private String addr;

  /**
   * 상세주소
   */
  @ApiModelProperty(notes = "상세주소", position = 6)
  private String daddr;

  /**
   * 대표번호
   */
  @ApiModelProperty(notes = "대표번호", position = 7)
  private String telno;

  /**
   * 팩스번호
   */
  @ApiModelProperty(notes = "팩스번호", position = 8)
  private String fxno;

  /**
   * 담당자
   */
  @ApiModelProperty(notes = "담당자", position = 9)
  private String mngr;

  /**
   * 담당자연락처
   */
  @ApiModelProperty(notes = "담당자연락처", position = 10)
  private String mngrTelno;

  /**
   * 담당자이메일
   */
  @ApiModelProperty(notes = "담당자이메일", position = 11)
  private String mngrEmail;

  /**
   * 메모
   */
  @ApiModelProperty(notes = "메모", position = 12)
  private String memo;

  /**
   * 거래여부
   */
  @ApiModelProperty(notes = "거래여부", position = 13)
  private String dealYn;

}
