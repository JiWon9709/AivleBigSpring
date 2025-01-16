package kt.aivle.signup.model;

import io.swagger.annotations.ApiModelProperty;
import kt.aivle.base.BaseListModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ClntCoListRequest extends BaseListModel {
  private static final long serialVersionUID = 1L;


  /**
   * 의뢰사명
   */
  @ApiModelProperty(required = true, notes = "의뢰사명", position = 1)
  private String keyword;

  /**
   * 회사코드
   */
  @ApiModelProperty(required = false, notes = "회사코드", position = 3)
  private int searchType;

  /**
   * 거래여부
   */
  @ApiModelProperty(required = false, notes = "거래여부", position = 3)
  private int dealYn;
}
