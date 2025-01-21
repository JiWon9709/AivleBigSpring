package kt.aivle.mypage.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class UserPwRequest {
    @ApiModelProperty(notes = "사용자번호", position = 1)
    private int userSn;

    @ApiModelProperty(notes="현재 비밀번호", position = 2)
    private String currentPassword;

    @ApiModelProperty(notes="새로운 비밀번호", position = 3)
    private String newPassword;
}
