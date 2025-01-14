package kt.aivle.clnt_co.model;

import io.swagger.annotations.ApiModelProperty;
import kt.aivle.base.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

@Data
@EqualsAndHashCode(callSuper = false)
public class TestRequest extends BaseModel {
  private static final long serialVersionUID = 1L;

  /**
   * 직원이름
   */
  @NotBlank(message = "직원이름은 필수입니다.")
  @ApiModelProperty(required = true, notes = "직원이름", position = 1)
  private String employeeId;
  /**
   * 직원이름
   */
  @NotBlank(message = "직원이름은 필수입니다.")
  @ApiModelProperty(required = true, notes = "직원이름", position = 1)
  private String employeeName;

  /**
   * 성별
   */
  @NotBlank(message = "성별은 필수입니다.")
  @ApiModelProperty(required = true, notes = "성별", position = 2)
  private String gender;

  /**
   * 전화번호
   */
  @NotBlank(message = "전화번호는 필수입니다.")
  @ApiModelProperty(required = true, notes = "전화번호", position = 3)
  private String phone;

  /**
   * 이메일
   */
  @NotBlank(message = "이메일은 필수입니다.")
  @ApiModelProperty(required = true, notes = "이메일", position = 4)
  private String email;

}
