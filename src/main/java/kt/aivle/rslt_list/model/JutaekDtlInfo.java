package kt.aivle.rslt_list.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class JutaekDtlInfo {
  private static final long serialVersionUID = 1L;

  /**
   * 주택상세고유번호
   */
  @ApiModelProperty(required = true, notes = "주택상세고유번호", position = 1)
  private Long jutaekDtlSn;

  /**
   * 주택고유번호
   */
  @ApiModelProperty(required = true, notes = "주택고유번호", position = 2)
  private Long jutaekSn;

  /**
   * 주택크기
   */
  @ApiModelProperty(required = true, notes = "주택크기", position = 3)
  private String jutaekSize;

  /**
   * 주택타입
   */
  @ApiModelProperty(required = true, notes = "주택타입", position = 7)
  private String jutaekType;

  /**
   * 보증금
   */
  @ApiModelProperty(required = true, notes = "보증금", position = 7)
  private String guarantee;

  /**
   * 월세
   */
  @ApiModelProperty(required = true, notes = "월세", position = 8)
  private String monthly;

  /**
   * 주택명
   */
  @ApiModelProperty(required = true, notes = "주택명", position = 9)
  private String jutaekName;

  /**
   * 주소
   */
  @ApiModelProperty(required = true, notes = "주소", position = 10)
  private String jutaekAddress;

  /**
   * 경도
   */
  @ApiModelProperty(required = true, notes = "경도", position = 11)
  private String longitude;

  /**
   * 위도
   */
  @ApiModelProperty(required = true, notes = "위도", position = 12)
  private String latitude;
}
