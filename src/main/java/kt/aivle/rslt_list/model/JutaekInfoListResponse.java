package kt.aivle.rslt_list.model;

import io.swagger.annotations.ApiModelProperty;
import kt.aivle.base.BaseResListModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class JutaekInfoListResponse extends BaseResListModel {
  private static final long serialVersionUID = 1L;

  /**
   * 주택정보리스트
   */
  @ApiModelProperty(required = true, notes = "주택정보리스트", position = 1)
  private List<JutaekInfo> jutaekInfoList;

}
