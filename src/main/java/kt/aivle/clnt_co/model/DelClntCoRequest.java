package kt.aivle.clnt_co.model;

import io.swagger.annotations.ApiModelProperty;
import kt.aivle.base.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class DelClntCoRequest extends BaseModel {
  private static final long serialVersionUID = 1L;

  /**
   * 삭제할 의뢰사고유번호 리스트
   */
  @ApiModelProperty(required = true, notes = "삭제할 의뢰사고유번호 리스트", position = 1)
  private List<Long> clntCoSnList;

  /**
   * 삭제할 의뢰사고유번호
   */
  @ApiModelProperty(hidden = true, notes = "삭제할 의뢰사고유번호")
  private Long clntCoSn;

}
