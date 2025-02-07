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

  /**
   * 공고고유번호
   */
  @ApiModelProperty(required = true, notes = "공고고유번호", position = 2)
  private Long gongoSn;

  /**
   * 유저고유번호
   */
  @ApiModelProperty(required = true, notes = "유저고유번호", position = 3)
  private Long userSn;

  /**
   * 입력순위
   */
  @ApiModelProperty(required = true, notes = "입력순위", position = 4)
  private Long inputRank;
}
