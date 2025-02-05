package kt.aivle.rslt_list.model;

import io.swagger.annotations.ApiModelProperty;
import kt.aivle.base.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class JutaekInfo {
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
   * 거주공간
   */
  @ApiModelProperty(required = true, notes = "거주공간", position = 4)
  private String residentialArea;

  /**
   * 공용공간
   */
  @ApiModelProperty(required = true, notes = "공용공간", position = 5)
  private String commonArea;

  /**
   * 기타공간
   */
  @ApiModelProperty(required = true, notes = "기타공간", position = 6)
  private String otherArea;

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
   * 사진 리스트
   */
  @ApiModelProperty(required = true, notes = "사진 리스트", position = 11)
  private List<String> jutaekImg;
}
