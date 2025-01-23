package kt.aivle.rslt_list.model;

import io.swagger.annotations.ApiModelProperty;
import kt.aivle.base.BaseListModel;
import kt.aivle.base.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class JutaekDtlRequest extends BaseModel {
  private static final long serialVersionUID = 1L;

  /**
   * 주택DtlSn
   */
  @ApiModelProperty(required = true, notes = "주택DtlSn", position = 1)
  private Long jutaekDtlSn;



}
