package kt.aivle.clnt_co.model;

import io.swagger.annotations.ApiModelProperty;
import kt.aivle.base.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

@Data
@EqualsAndHashCode(callSuper = false)
public class TestList extends BaseModel {
  private static final long serialVersionUID = 1L;

  /**
   * 직원이름
   */
  @ApiModelProperty(required = true, notes = "직원이름", position = 1)
  private String employeeId;
  /**
   * 직원이름
   */
  @ApiModelProperty(required = true, notes = "직원이름", position = 1)
  private String employeeName;

  /**
   * 성별
   */
  @ApiModelProperty(required = true, notes = "성별", position = 2)
  private String gender;

  /**
   * 전화번호
   */
  @ApiModelProperty(required = true, notes = "전화번호", position = 3)
  private String phone;

  /**
   * 이메일
   */
  @ApiModelProperty(required = true, notes = "이메일", position = 4)
  private String email;

  /**
   * 고용일자
   */
  @ApiModelProperty(required = true, notes = "고용일자", position = 5)
  private String hireDate;
}
