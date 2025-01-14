package kt.aivle.clnt_co.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class HospitalInfo {
  private static final long serialVersionUID = 1L;

  /**
   * 병원명
   */
  @ApiModelProperty(notes = "병원명", position = 1)
  private String name;

  /**
   * 주소
   */
  @ApiModelProperty(notes = "주소", position = 2)
  private String address;

  /**
   * 응급의료기관 종류
   */
  @ApiModelProperty(notes = "응급의료기관 종류", position = 3)
  private String emergencyType;

  /**
   * 전화번호1
   */
  @ApiModelProperty(notes = "전화번호1", position = 4)
  private String phone1;

  /**
   * 전화번호2
   */
  @ApiModelProperty(notes = "전화번호2", position = 5)
  private String phone3;

  /**
   * 위도
   */
  @ApiModelProperty(notes = "위도", position = 6)
  private String latitude;

  /**
   * 경도
   */
  @ApiModelProperty(notes = "경도", position = 7)
  private String longitude;

  /**
   * 거리
   */
  @ApiModelProperty(notes = "거리", position = 8)
  private String distance;


}
