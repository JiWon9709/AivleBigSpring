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

  /**
   * 지역명리스트
   */
  @ApiModelProperty(required = true, notes = "지역명리스트", position = 2)
  private List<String> location;

  /**
   * 최소보증금
   */
  @ApiModelProperty(required = true, notes = "최소보증금", position = 3)
  private String minGuarantee;

  /**
   * 최대보증금
   */
  @ApiModelProperty(required = true, notes = "최대보증금", position = 4)
  private String maxGuarantee;

  /**
   * 최소월세
   */
  @ApiModelProperty(required = true, notes = "최소월세", position = 5)
  private String minMonthly;

  /**
   * 최대월세
   */
  @ApiModelProperty(required = true, notes = "최대월세", position = 6)
  private String maxMonthly;

  /**
   * 최소크기
   */
  @ApiModelProperty(required = true, notes = "최소크기", position = 7)
  private String minSize;

  /**
   * 최대크기
   */
  @ApiModelProperty(required = true, notes = "최대크기", position = 8)
  private String maxSize;

}
