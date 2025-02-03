package kt.aivle.rslt_list.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class InputInfo {
  private static final long serialVersionUID = 1L;

  /**
   * 청약유형
   */
  @ApiModelProperty(required = true, notes = "청약유형", position = 1)
  private String inputType;

  /**
   * 청약순위
   */
  @ApiModelProperty(required = true, notes = "청약순위", position = 2)
  private Long inputRank;

  /**
   * 청약점수
   */
  @ApiModelProperty(required = true, notes = "청약점수", position = 3)
  private Long inputScore;

}
