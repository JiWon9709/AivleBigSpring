package kt.aivle.rslt_list.model;

import io.swagger.annotations.ApiModelProperty;
import kt.aivle.base.BaseListModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class JutaekListRequest extends BaseListModel {
  private static final long serialVersionUID = 1L;

  /**
   * 주택Sn리스트
   */
  @ApiModelProperty(required = true, notes = "주택Sn리스트", position = 1)
  private List<Integer> jutaekSnList;

}
