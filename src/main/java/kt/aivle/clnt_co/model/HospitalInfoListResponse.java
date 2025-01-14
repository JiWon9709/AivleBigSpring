package kt.aivle.clnt_co.model;

import io.swagger.annotations.ApiModelProperty;
import kt.aivle.base.BaseResListModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class HospitalInfoListResponse extends BaseResListModel {
  private static final long serialVersionUID = 1L;

  /**
   * 병원정보리스트
   */
  @ApiModelProperty(notes = "병원정보리스트", position = 1)
  private List<HospitalInfo> infoList;



}
