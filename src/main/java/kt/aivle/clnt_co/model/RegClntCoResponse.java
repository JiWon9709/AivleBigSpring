package kt.aivle.clnt_co.model;

import io.swagger.annotations.ApiModelProperty;
import kt.aivle.base.BaseResListModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class RegClntCoResponse {
  private static final long serialVersionUID = 1L;

  /**
   * 총결과건수
   */
  @ApiModelProperty(notes = "총결과건수")
  private int totRsltCnt;

  /**
   * val
   */
  @ApiModelProperty(notes = "val")
  private String val;

}
