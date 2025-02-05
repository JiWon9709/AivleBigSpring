package kt.aivle.rslt_list.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class FavExChkRequest {
  private static final long serialVersionUID = 1L;

  /**
   * 사용자고유번호
   */
  @ApiModelProperty(required = true, notes = "사용자고유번호", position = 1)
  private Long userSn;

  /**
   * 공고고유번호
   */
  @ApiModelProperty(required = true, notes = "공고고유번호", position = 2)
  private Long gongoSn;

  /**
   * 주택상세고유번호
   */
  @ApiModelProperty(required = true, notes = "주택상세고유번호", position = 3)
  private Long jutaekDtlSn;

}
