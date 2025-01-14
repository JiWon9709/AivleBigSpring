package kt.aivle.clnt_co.model;

import io.swagger.annotations.ApiModelProperty;
import kt.aivle.base.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

@Data
@EqualsAndHashCode(callSuper = false)
public class TestRequest2 extends BaseModel {
  private static final long serialVersionUID = 1L;

  /**
   * 직원이름
   */
  @NotBlank(message = "직원이름은 필수입니다.")
  @ApiModelProperty(required = true, notes = "직원이름", position = 1)
  private String text;
  /**
   * 직원이름
   */
  @NotBlank(message = "직원이름은 필수입니다.")
  @ApiModelProperty(required = true, notes = "직원이름", position = 1)
  private String lat;

  /**
   * 성별
   */
  @NotBlank(message = "성별은 필수입니다.")
  @ApiModelProperty(required = true, notes = "성별", position = 2)
  private String lng;



}
